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
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.geforcemods.securitycraft.inventory.SingleLensMenu.SingleLensContainer;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ChunkTrackingView.Positioned;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class SecurityCameraBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity, IEMPAffectedBE, MenuProvider, ContainerListener, SingleLensContainer {
	private static final Map<ServerPlayer, Set<SecurityCameraBlockEntity>> RECENTLY_UNVIEWED_CAMERAS = new HashMap<>();
	private double cameraRotation = 0.0D;
	private double oCameraRotation = 0.0D;
	private boolean addToRotation = SecurityCraft.RANDOM.nextBoolean();
	private Map<UUID, ChunkTrackingView.Positioned> cameraFeedChunks = new HashMap<>();
	private Map<UUID, Set<Long>> linkedFrames = new HashMap<>();
	private Set<UUID> playersRequestingChunks = new HashSet<>();
	private boolean down = false, initialized = false;
	private int playersViewing = 0;
	private boolean shutDown = false;
	private float initialXRotation, initialYRotation, initialZoom = 1.0F;
	private DoubleOption rotationSpeedOption = new DoubleOption("rotationSpeed", 0.018D, 0.01D, 0.025D, 0.001D);
	private DoubleOption movementSpeedOption = new DoubleOption("movementSpeed", 2.0D, 0.0D, 20.0D, 0.1D);
	private BooleanOption shouldRotateOption = new BooleanOption("shouldRotate", true);
	private DoubleOption customRotationOption = new DoubleOption("customRotation", getCameraRotation(), 1.55D, -1.55D, rotationSpeedOption.get());
	private DisabledOption disabled = new DisabledOption(false);
	private IntOption opacity = new IntOption("opacity", 100, 0, 255, 1);
	private LensContainer lens = new LensContainer(1);

	public SecurityCameraBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.SECURITY_CAMERA_BLOCK_ENTITY.get(), pos, state);
		lens.addListener(this);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!initialized) {
			Direction facing = state.getValue(SecurityCameraBlock.FACING);

			initialized = true;
			down = facing == Direction.DOWN;

			if (!isModuleEnabled(ModuleType.SMART))
				setDefaultViewingDirection(facing, initialZoom);
		}

		oCameraRotation = getCameraRotation();

		if (!shutDown && !disabled.get()) {
			if (!shouldRotateOption.get()) {
				cameraRotation = customRotationOption.get();
				return;
			}

			if (down) {
				cameraRotation = getCameraRotation() + rotationSpeedOption.get();

				if (oCameraRotation >= Mth.TWO_PI) {
					cameraRotation %= Mth.TWO_PI;
					oCameraRotation %= Mth.TWO_PI;
				}

				return;
			}

			if (addToRotation && getCameraRotation() <= Mth.HALF_PI)
				cameraRotation = getCameraRotation() + rotationSpeedOption.get();
			else
				addToRotation = false;

			if (!addToRotation && getCameraRotation() >= -Mth.HALF_PI)
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
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		unlinkAllFrames();
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		tag.putBoolean("shutDown", shutDown);
		tag.put("lens", lens.createTag(lookupProvider));
		tag.putFloat("initial_x_rotation", initialXRotation);
		tag.putFloat("initial_y_rotation", initialYRotation);
		tag.putFloat("initial_zoom", initialZoom);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		shutDown = tag.getBoolean("shutDown");
		lens.fromTag(tag.getList("lens", Tag.TAG_COMPOUND), lookupProvider);
		initialXRotation = tag.getFloat("initial_x_rotation");
		initialYRotation = tag.getFloat("initial_y_rotation");

		if (tag.contains("initial_zoom"))
			initialZoom = tag.getFloat("initial_zoom");
	}

	public static IItemHandler getCapability(SecurityCameraBlockEntity be, Direction side) {
		return BlockUtils.isAllowedToExtractFromProtectedObject(side, be) ? new InvWrapper(be.lens) : new InsertOnlyInvWrapper(be.lens);
	}

	@Override
	public void containerChanged(Container container) {
		if (level == null)
			return;

		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new SingleLensMenu(id, level, worldPosition, inventory);
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}

	@Override
	public Container getLensContainer() {
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
			for (ServerPlayer player : ((ServerLevel) level).players()) {
				if (player.getCamera() instanceof SecurityCamera camera && camera.blockPosition().equals(worldPosition))
					camera.stopViewing(player);
			}
		}

		super.onOptionChanged(option);
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

	public double getCameraRotation() {
		return cameraRotation;
	}

	public double getOriginalCameraRotation() {
		return oCameraRotation;
	}

	public boolean isDown() {
		return down;
	}

	public void linkFrameForPlayer(ServerPlayer player, BlockPos framePos, int chunkLoadingDistance) {
		int maxChunkLoadingDistance = cameraFeedChunks.values().stream().mapToInt(Positioned::viewDistance).max().orElse(0);
		Set<Long> playerViewedFrames = linkedFrames.computeIfAbsent(player.getUUID(), uuid -> new HashSet<>());

		if (chunkLoadingDistance > maxChunkLoadingDistance) {
			SectionPos cameraChunkPos = SectionPos.of(worldPosition);

			BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.track(this);
			requestChunkSending(player, chunkLoadingDistance);

			for (int x = cameraChunkPos.getX() - chunkLoadingDistance; x <= cameraChunkPos.getX() + chunkLoadingDistance; x++) {
				for (int z = cameraChunkPos.getZ() - chunkLoadingDistance; z <= cameraChunkPos.getZ() + chunkLoadingDistance; z++) {
					SecurityCraft.CAMERA_TICKET_CONTROLLER.forceChunk((ServerLevel) level, worldPosition, x, z, true, false);
				}
			}
		}

		playerViewedFrames.add(framePos.asLong());
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
				int maxChunkLoadingDistance = cameraFeedChunks.values().stream().mapToInt(Positioned::viewDistance).max().orElse(0);

				addRecentlyUnviewedCamera(this);
				BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.stopTracking(this);

				for (int x = cameraChunkPos.getX() - maxChunkLoadingDistance; x <= cameraChunkPos.getX() + maxChunkLoadingDistance; x++) {
					for (int z = cameraChunkPos.getZ() - maxChunkLoadingDistance; z <= cameraChunkPos.getZ() + maxChunkLoadingDistance; z++) {
						SecurityCraft.CAMERA_TICKET_CONTROLLER.forceChunk((ServerLevel) level, worldPosition, x, z, false, false);
					}
				}
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
			CameraController.FRAME_CAMERA_FEEDS.remove(GlobalPos.of(level.dimension(), worldPosition));
	}

	public void requestChunkSending(ServerPlayer player, int chunkLoadingDistance) {
		setChunkLoadingDistance(player, chunkLoadingDistance);
		playersRequestingChunks.add(player.getUUID());
	}

	public ChunkTrackingView.Positioned getCameraFeedChunks(ServerPlayer player) {
		return cameraFeedChunks.get(player.getUUID());
	}

	public void clearCameraFeedChunks(ServerPlayer player) {
		cameraFeedChunks.remove(player.getUUID());
	}

	public void setChunkLoadingDistance(ServerPlayer player, int chunkLoadingDistance) {
		cameraFeedChunks.put(player.getUUID(), (ChunkTrackingView.Positioned) ChunkTrackingView.of(new ChunkPos(worldPosition), chunkLoadingDistance));
	}

	public boolean shouldKeepChunkLoaded(ServerPlayer player, int chunkX, int chunkZ) {
		UUID uuid = player.getUUID();

		return cameraFeedChunks.containsKey(uuid) && cameraFeedChunks.get(uuid).contains(chunkX, chunkZ);
	}

	public boolean shouldSendChunksToPlayer(ServerPlayer player) {
		return playersRequestingChunks.remove(player.getUUID());
	}

	public static void addRecentlyUnviewedCamera(SecurityCameraBlockEntity camera) {
		for (ServerPlayer player : camera.level.getServer().getPlayerList().getPlayers()) {
			Set<SecurityCameraBlockEntity> unviewingCameras = RECENTLY_UNVIEWED_CAMERAS.computeIfAbsent(player, p -> new HashSet<>());

			unviewingCameras.add(camera);
		}
	}

	public static boolean hasRecentlyUnviewedCameras(ServerPlayer player) {
		return RECENTLY_UNVIEWED_CAMERAS.containsKey(player);
	}

	public static Set<SecurityCameraBlockEntity> fetchRecentlyUnviewedCameras(ServerPlayer player) {
		return RECENTLY_UNVIEWED_CAMERAS.remove(player);
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
		return switch (facing) {
			case NORTH -> 180F;
			case WEST -> 90F;
			case SOUTH -> 0F;
			case EAST -> 270F;
			case DOWN, UP -> 0F;
		};
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
}
