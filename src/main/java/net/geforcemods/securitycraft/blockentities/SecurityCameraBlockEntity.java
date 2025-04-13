package net.geforcemods.securitycraft.blockentities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IEMPAffectedBE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.geforcemods.securitycraft.inventory.SingleLensMenu.SingleLensContainer;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class SecurityCameraBlockEntity extends DisguisableBlockEntity implements ITickableTileEntity, IEMPAffectedBE, INamedContainerProvider, IInventoryChangedListener, SingleLensContainer {
	public static final float HALF_PI = ((float) Math.PI / 2F);
	public static final float TWO_PI = ((float) Math.PI * 2F);
	private static final Map<ServerPlayerEntity, Set<SecurityCameraBlockEntity>> RECENTLY_UNVIEWED_CAMERAS = new HashMap<>();
	private static final Set<Long> FORCE_LOADED_CAMERA_CHUNKS = new HashSet<>();
	private static int forceLoadingCounter = 0;
	private double cameraRotation = 0.0D;
	private double oCameraRotation = 0.0D;
	private boolean addToRotation = SecurityCraft.RANDOM.nextBoolean();
	private final Set<Long> chunkForceLoadQueue = new HashSet<>();
	private Map<UUID, ChunkTrackingView> cameraFeedChunks = new HashMap<>();
	private Map<UUID, Set<Long>> linkedFrames = new HashMap<>();
	private Set<UUID> playersRequestingChunks = new HashSet<>();
	private int maxChunkLoadingRadius = 0;
	private boolean down = false, initialized = false;
	private int playersViewing = 0;
	private boolean shutDown = false;
	private float initialXRotation, initialYRotation, initialZoom = 1.0F;
	private DoubleOption rotationSpeedOption = new DoubleOption(this::getBlockPos, "rotationSpeed", 0.018D, 0.01D, 0.025D, 0.001D);
	private DoubleOption movementSpeedOption = new DoubleOption(this::getBlockPos, "movementSpeed", 2.0D, 0.0D, 20.0D, 0.1D);
	private BooleanOption shouldRotateOption = new BooleanOption("shouldRotate", true);
	private DoubleOption customRotationOption = new DoubleOption(this::getBlockPos, "customRotation", getCameraRotation(), 1.55D, -1.55D, rotationSpeedOption.get());
	private DisabledOption disabled = new DisabledOption(false);
	private IntOption opacity = new IntOption(this::getBlockPos, "opacity", 100, 0, 255, 1);
	private LazyOptional<IItemHandler> insertOnlyHandler, lensHandler;
	private LensContainer lens = new LensContainer(1);

	public SecurityCameraBlockEntity() {
		super(SCContent.SECURITY_CAMERA_BLOCK_ENTITY.get());
		lens.addListener(this);
	}

	@Override
	public void tick() {
		if (!initialized) {
			Direction facing = getBlockState().getValue(SecurityCameraBlock.FACING);

			initialized = true;
			down = facing == Direction.DOWN;

			if (!isModuleEnabled(ModuleType.SMART))
				setDefaultViewingDirection(facing, initialZoom);
		}

		if (!level.isClientSide && !chunkForceLoadQueue.isEmpty()) {
			Set<Long> queueCopy = new HashSet<>(chunkForceLoadQueue);

			for (Long chunkPosLong : queueCopy) {
				if (forceLoadingCounter > 16) //Through forceloading 16 chunks per tick, a default camera view area of 32x32 chunks is fully loaded within 40 server ticks (which might take more than 2 seconds, depending on forceloading speed)
					break;

				ChunkPos chunkPos = new ChunkPos(chunkPosLong);

				if (!FORCE_LOADED_CAMERA_CHUNKS.contains(chunkPosLong)) {
					ForgeChunkManager.forceChunk((ServerWorld) level, SecurityCraft.MODID, worldPosition, chunkPos.x, chunkPos.z, true, false);
					FORCE_LOADED_CAMERA_CHUNKS.add(chunkPosLong);
					forceLoadingCounter++;
				}

				chunkForceLoadQueue.remove(chunkPosLong);
			}
		}

		oCameraRotation = getCameraRotation();

		if (!shutDown && !disabled.get()) {
			if (!shouldRotateOption.get()) {
				cameraRotation = customRotationOption.get();

				if (level.isClientSide && cameraRotation != oCameraRotation) {
					GlobalPos cameraPos = GlobalPos.of(level.dimension(), worldPosition);

					if (FrameFeedHandler.hasFeed(cameraPos))
						FrameFeedHandler.getFeed(cameraPos).requestFrustumUpdate();
				}

				return;
			}

			if (down) { //If the camera is facing down, the rotation is still important for viewing the camera in a frame
				cameraRotation = getCameraRotation() + rotationSpeedOption.get();

				if (oCameraRotation >= TWO_PI) {
					cameraRotation %= TWO_PI;
					oCameraRotation %= TWO_PI;
				}

				return;
			}

			if (addToRotation && getCameraRotation() <= HALF_PI)
				cameraRotation = getCameraRotation() + rotationSpeedOption.get();
			else
				addToRotation = false;

			if (!addToRotation && getCameraRotation() >= -HALF_PI)
				cameraRotation = getCameraRotation() - rotationSpeedOption.get();
			else
				addToRotation = true;
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (!level.isLoaded(worldPosition) || !(level.getBlockEntity(worldPosition) instanceof SecurityCameraBlockEntity))
			unlinkAllFrames();
	}

	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player, Owner oldOwner, Owner newOwner) {
		unlinkAllFrames();
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.putDouble("camera_rotation", cameraRotation);
		tag.putBoolean("add_to_rotation", addToRotation);
		tag.putBoolean("shutDown", shutDown);
		tag.put("lens", lens.createTag());
		tag.putFloat("initial_x_rotation", initialXRotation);
		tag.putFloat("initial_y_rotation", initialYRotation);
		tag.putFloat("initial_zoom", initialZoom);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		if (tag.contains("camera_rotation")) {
			double newCamRotation = tag.getDouble("camera_rotation");

			cameraRotation = newCamRotation;
			oCameraRotation = newCamRotation;
			addToRotation = tag.getBoolean("add_to_rotation");
		}

		shutDown = tag.getBoolean("shutDown");
		lens.fromTag(tag.getList("lens", Constants.NBT.TAG_COMPOUND));
		initialXRotation = tag.getFloat("initial_x_rotation");
		initialYRotation = tag.getFloat("initial_y_rotation");

		if (tag.contains("initial_zoom"))
			initialZoom = tag.getFloat("initial_zoom");
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(side, this) ? getNormalHandler().cast() : getInsertOnlyHandler().cast();
		else
			return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		if (insertOnlyHandler != null)
			insertOnlyHandler.invalidate();

		if (lensHandler != null)
			lensHandler.invalidate();

		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		insertOnlyHandler = null;
		lensHandler = null;
		super.reviveCaps();
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(lens));

		return insertOnlyHandler;
	}

	private LazyOptional<IItemHandler> getNormalHandler() {
		if (lensHandler == null)
			lensHandler = LazyOptional.of(() -> new InvWrapper(lens));

		return lensHandler;
	}

	@Override
	public void containerChanged(IInventory container) {
		if (level == null)
			return;

		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
		return new SingleLensMenu(id, level, worldPosition, inventory);
	}

	@Override
	public ITextComponent getDisplayName() {
		return super.getDisplayName();
	}

	@Override
	public Inventory getLensContainer() {
		return lens;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.REDSTONE, ModuleType.ALLOWLIST, ModuleType.SMART, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				rotationSpeedOption, shouldRotateOption, customRotationOption, disabled, opacity, movementSpeedOption
		};
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.REDSTONE)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.POWERED, false));
		else if (module == ModuleType.SMART)
			setDefaultViewingDirection(getBlockState().getValue(SecurityCameraBlock.FACING), initialZoom);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		//make players stop viewing the camera when it's disabled
		if (option.getName().equals("disabled") && !level.isClientSide && ((BooleanOption) option).get()) {
			for (ServerPlayerEntity player : ((ServerWorld) level).players()) {
				if (player.getCamera() instanceof SecurityCamera) {
					SecurityCamera camera = (SecurityCamera) player.getCamera();

					if (camera.blockPosition().equals(worldPosition))
						camera.stopViewing(player);
				}
			}
		}
	}

	@Override
	public void shutDown() {
		BlockState state = level.getBlockState(worldPosition);

		IEMPAffectedBE.super.shutDown();

		if (state.getBlock() == SCContent.SECURITY_CAMERA.get() && state.getValue(SecurityCameraBlock.POWERED))
			level.setBlockAndUpdate(worldPosition, state.setValue(SecurityCameraBlock.POWERED, false));
	}

	@Override
	public boolean isShutDown() {
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	public void startViewing() {
		if (playersViewing++ == 0)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.BEING_VIEWED, true));
	}

	public void stopViewing() {
		if (--playersViewing == 0)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.BEING_VIEWED, false));
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public boolean isDown() {
		return down;
	}

	public void linkFrameForPlayer(ServerPlayerEntity player, BlockPos framePos, int chunkLoadingDistance) {
		Set<Long> playerViewedFrames = linkedFrames.computeIfAbsent(player.getUUID(), uuid -> new HashSet<>());

		BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.track(this);
		requestChunkSending(player, chunkLoadingDistance);

		if (chunkLoadingDistance > maxChunkLoadingRadius) {
			ChunkPos cameraChunkPos = new ChunkPos(worldPosition);
			Long cameraChunkPosLong = cameraChunkPos.toLong();

			if (!FORCE_LOADED_CAMERA_CHUNKS.contains(cameraChunkPosLong)) { //The chunk the camera is in should be forceloaded immediately
				ForgeChunkManager.forceChunk((ServerWorld) level, SecurityCraft.MODID, worldPosition, cameraChunkPos.x, cameraChunkPos.z, true, false);
				chunkForceLoadQueue.add(cameraChunkPosLong);
			}

			for (int x = cameraChunkPos.x - chunkLoadingDistance; x <= cameraChunkPos.x + chunkLoadingDistance; x++) {
				for (int z = cameraChunkPos.z - chunkLoadingDistance; z <= cameraChunkPos.z + chunkLoadingDistance; z++) {
					Long forceLoadingPos = ChunkPos.asLong(x, z);

					//Currently, only forceloading new chunks (as opposed to stopping their force load) is staggered, since the latter is usually finished a lot faster
					if (!FORCE_LOADED_CAMERA_CHUNKS.contains(forceLoadingPos)) //Only queue chunks for forceloading if they haven't been forceloaded by another camera already
						chunkForceLoadQueue.add(forceLoadingPos);
				}
			}

			maxChunkLoadingRadius = chunkLoadingDistance;
		}

		playerViewedFrames.add(framePos.asLong());
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //Syncs the camera's rotation to all clients again, in case a client is desynched
	}

	public void unlinkFrameForPlayer(UUID playerUUID, BlockPos framePos) {
		if (linkedFrames.containsKey(playerUUID)) {
			Set<Long> linkedFramesPerPlayer = linkedFrames.get(playerUUID);

			if (framePos != null)
				linkedFramesPerPlayer.remove(framePos.asLong());

			if (framePos == null || linkedFramesPerPlayer.isEmpty())
				linkedFrames.remove(playerUUID);

			if (linkedFrames.isEmpty()) {
				SectionPos cameraChunkPos = SectionPos.of(worldPosition);

				addRecentlyUnviewedCamera(this);
				BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.stopTracking(this);

				for (int x = cameraChunkPos.getX() - maxChunkLoadingRadius; x <= cameraChunkPos.getX() + maxChunkLoadingRadius; x++) {
					for (int z = cameraChunkPos.getZ() - maxChunkLoadingRadius; z <= cameraChunkPos.getZ() + maxChunkLoadingRadius; z++) {
						ChunkPos chunkPos = new ChunkPos(x, z);
						Long chunkPosLong = chunkPos.toLong();

						if (FORCE_LOADED_CAMERA_CHUNKS.contains(chunkPosLong) && BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.shouldKeepChunkForceloaded(chunkPos)).isEmpty()) {
							ForgeChunkManager.forceChunk((ServerWorld) level, SecurityCraft.MODID, worldPosition, x, z, false, false);
							FORCE_LOADED_CAMERA_CHUNKS.remove(chunkPosLong);
						}
					}
				}

				maxChunkLoadingRadius = 0;
			}
		}
	}

	public void unlinkFrameForAllPlayers(BlockPos framePos) {
		for (UUID player : new HashSet<>(linkedFrames.keySet())) {
			unlinkFrameForPlayer(player, framePos);
		}
	}

	public void unlinkAllFrames() {
		for (UUID player : new HashSet<>(linkedFrames.keySet())) {
			unlinkFrameForPlayer(player, null);
		}

		if (level.isClientSide)
			FrameFeedHandler.removeAllFrameLinks(GlobalPos.of(level.dimension(), worldPosition));
	}

	public boolean hasPlayerFrameLink(PlayerEntity player) {
		return linkedFrames.containsKey(player.getUUID());
	}

	public boolean isFrameLinked(PlayerEntity player, BlockPos framePos) {
		return hasPlayerFrameLink(player) && linkedFrames.get(player.getUUID()).contains(framePos.asLong());
	}

	public void requestChunkSending(ServerPlayerEntity player, int chunkLoadingDistance) {
		setChunkLoadingDistance(player, chunkLoadingDistance);
		playersRequestingChunks.add(player.getUUID());
	}

	public ChunkTrackingView getCameraFeedChunks(ServerPlayerEntity player) {
		return cameraFeedChunks.get(player.getUUID());
	}

	public void clearCameraFeedChunks(ServerPlayerEntity player) {
		cameraFeedChunks.remove(player.getUUID());
	}

	public void setChunkLoadingDistance(ServerPlayerEntity player, int chunkLoadingDistance) {
		cameraFeedChunks.put(player.getUUID(), new ChunkTrackingView(new ChunkPos(worldPosition), chunkLoadingDistance));
	}

	public boolean shouldKeepChunkTracked(ServerPlayerEntity player, int chunkX, int chunkZ) {
		UUID uuid = player.getUUID();

		return cameraFeedChunks.containsKey(uuid) && cameraFeedChunks.get(uuid).contains(chunkX, chunkZ);
	}

	public boolean shouldSendChunksToPlayer(ServerPlayerEntity player) {
		return playersRequestingChunks.remove(player.getUUID());
	}

	public boolean shouldKeepChunkForceloaded(ChunkPos chunkPos) {
		ChunkPos cameraPos = new ChunkPos(worldPosition);

		return chunkPos.x >= cameraPos.x - maxChunkLoadingRadius && chunkPos.x <= cameraPos.x + maxChunkLoadingRadius && chunkPos.z >= cameraPos.z - maxChunkLoadingRadius && chunkPos.z <= cameraPos.z + maxChunkLoadingRadius;
	}

	public static void addRecentlyUnviewedCamera(SecurityCameraBlockEntity camera) {
		for (ServerPlayerEntity player : camera.level.getServer().getPlayerList().getPlayers()) {
			Set<SecurityCameraBlockEntity> unviewingCameras = RECENTLY_UNVIEWED_CAMERAS.computeIfAbsent(player, p -> new HashSet<>());

			unviewingCameras.add(camera);
		}
	}

	public static boolean hasRecentlyUnviewedCameras(ServerPlayerEntity player) {
		return RECENTLY_UNVIEWED_CAMERAS.containsKey(player);
	}

	public static Set<SecurityCameraBlockEntity> fetchRecentlyUnviewedCameras(ServerPlayerEntity player) {
		return RECENTLY_UNVIEWED_CAMERAS.remove(player);
	}

	public static void resetForceLoadingCounter() {
		forceLoadingCounter = 0;
	}

	public double getOriginalCameraRotation() {
		return oCameraRotation;
	}

	public double getCameraRotation() {
		return cameraRotation;
	}

	public int getOpacity() {
		return opacity.get();
	}

	public double getMovementSpeed() {
		return movementSpeedOption.get();
	}

	public boolean shouldRotate() {
		return shouldRotateOption.get();
	}

	public float getDefaultXRotation() {
		return down ? 75F : 30F;
	}

	public float getDefaultYRotation(Direction facing) {
		switch (facing) {
			case NORTH:
				return 180F;
			case WEST:
				return 90F;
			case SOUTH:
				return 0F;
			case EAST:
				return 270F;
			default:
				return 0F;
		}
	}

	public void setDefaultViewingDirection(Direction facing, float zoom) {
		setDefaultViewingDirection(getDefaultXRotation(), getDefaultYRotation(facing), zoom);
	}

	public void setDefaultViewingDirection(float initialXRotation, float initialYRotation, float initialZoom) {
		this.initialXRotation = initialXRotation;
		this.initialYRotation = initialYRotation;
		this.initialZoom = initialZoom;
		setChanged();
	}

	public float getInitialXRotation() {
		return initialXRotation;
	}

	public float getInitialYRotation() {
		return initialYRotation;
	}

	public float getInitialZoom() {
		return initialZoom;
	}

	public static class ChunkTrackingView {
		protected final ChunkPos center;
		protected final int viewDistance;

		public ChunkTrackingView(ChunkPos center, int viewDistance) {
			this.center = center;
			this.viewDistance = viewDistance;
		}

		public boolean contains(int x, int z) {
			return Utils.isInViewDistance(center.x, center.z, viewDistance, x, z);
		}

		public ChunkPos center() {
			return center;
		}

		public int viewDistance() {
			return viewDistance;
		}
	}
}
