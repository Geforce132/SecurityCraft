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
import net.geforcemods.securitycraft.inventory.SingleLensMenu.SingleLensContainer;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class SecurityCameraBlockEntity extends DisguisableBlockEntity implements IEMPAffectedBE, ITickable, IInventoryChangedListener, SingleLensContainer {
	public static final float HALF_PI = ((float) Math.PI / 2F);
	public static final float TWO_PI = ((float) Math.PI * 2F);
	private static final Map<EntityPlayerMP, Set<SecurityCameraBlockEntity>> RECENTLY_UNVIEWED_CAMERAS = new HashMap<>();
	private static final Set<Long> FORCE_LOADED_CAMERA_CHUNKS = new HashSet<>();
	private static int forceLoadingCounter = 0;
	private double cameraRotation = 0.0D;
	private double oCameraRotation = 0.0D;
	private boolean addToRotation = SecurityCraft.RANDOM.nextBoolean();
	private final Set<Long> chunkForceLoadQueue = new HashSet<>();
	private final Map<UUID, ChunkTrackingView> cameraFeedChunks = new HashMap<>();
	private final Map<UUID, Set<Long>> linkedFrames = new HashMap<>();
	private int maxChunkLoadingRadius = 0;
	private boolean down = false, initialized = false;
	private boolean shutDown = false;
	private float initialXRotation, initialYRotation, initialZoom = 1.0F;
	private DoubleOption rotationSpeedOption = new DoubleOption(this::getPos, "rotationSpeed", 0.018D, 0.01D, 0.025D, 0.001D);
	private DoubleOption movementSpeedOption = new DoubleOption(this::getPos, "movementSpeed", 2.0D, 0.0D, 20.0D, 0.1D);
	private BooleanOption shouldRotateOption = new BooleanOption("shouldRotate", true);
	private DoubleOption customRotationOption = new DoubleOption(this::getPos, "customRotation", getCameraRotation(), 1.55D, -1.55D, rotationSpeedOption.get());
	private DisabledOption disabled = new DisabledOption(false);
	private IntOption opacity = new IntOption(this::getPos, "opacity", 100, 0, 255, 1);
	private IItemHandler insertOnlyHandler, lensHandler;
	private LensContainer lens = new LensContainer(1);
	private int playersViewing = 0;
	private Ticket chunkTicket;

	public SecurityCameraBlockEntity() {
		lens.addInventoryChangeListener(this);
	}

	@Override
	public void update() {
		if (!initialized) {
			if (!isModuleEnabled(ModuleType.SMART))
				setDefaultViewingDirection(world.getBlockState(pos).getValue(SecurityCameraBlock.FACING), initialZoom);

			initialized = true;
		}

		if (!world.isRemote && !chunkForceLoadQueue.isEmpty()) {
			Set<Long> queueCopy = new HashSet<>(chunkForceLoadQueue);

			for (Long chunkPosLong : queueCopy) {
				if (forceLoadingCounter > 16) //Through forceloading 16 chunks per tick, a default camera view area of 32x32 chunks is fully loaded within 40 server ticks (which might take more than 2 seconds, depending on forceloading speed)
					break;

				ChunkPos chunkPos = new ChunkPos((int) (chunkPosLong & 0xFFFFFFFF), (int) (chunkPosLong >> 32));

				if (!FORCE_LOADED_CAMERA_CHUNKS.contains(chunkPosLong)) {
					getTicketAndForceChunk(chunkPos);
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

				if (world.isRemote && cameraRotation != oCameraRotation) {
					GlobalPos cameraPos = GlobalPos.of(world.provider.getDimension(), pos);

					if (CameraController.FRAME_CAMERA_FEEDS.containsKey(cameraPos))
						CameraController.FRAME_CAMERA_FEEDS.get(cameraPos).requestFrustumUpdate();
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
	public void shutDown() {
		IBlockState state = world.getBlockState(pos);

		IEMPAffectedBE.super.shutDown();

		if (state.getBlock() == SCContent.securityCamera && state.getValue(SecurityCameraBlock.POWERED)) {
			world.setBlockState(pos, state.withProperty(SecurityCameraBlock.POWERED, false));
			makeEveryoneStopViewingTheCamera();
		}
	}

	@Override
	public boolean isShutDown() {
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if (!world.isBlockLoaded(pos) || !(world.getTileEntity(pos) instanceof SecurityCameraBlockEntity))
			unlinkAllFrames();

		if (!world.isRemote && chunkTicket != null) {
			ForgeChunkManager.releaseTicket(chunkTicket);
			chunkTicket = null;
		}
	}

	@Override
	public void onOwnerChanged(IBlockState state, World level, BlockPos pos, EntityPlayer player, Owner oldOwner, Owner newOwner) {
		unlinkAllFrames();
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setDouble("camera_rotation", cameraRotation);
		tag.setBoolean("add_to_rotation", addToRotation);
		tag.setBoolean("ShutDown", shutDown);
		tag.setInteger("PlayersViewing", playersViewing);
		tag.setTag("lens", lens.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
		tag.setFloat("initial_x_rotation", initialXRotation);
		tag.setFloat("initial_y_rotation", initialYRotation);
		tag.setFloat("initial_zoom", initialZoom);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if (tag.hasKey("camera_rotation")) {
			double newCamRotation = tag.getDouble("camera_rotation");

			cameraRotation = newCamRotation;
			oCameraRotation = newCamRotation;
			addToRotation = tag.getBoolean("add_to_rotation");
		}

		shutDown = tag.getBoolean("ShutDown");
		playersViewing = tag.getInteger("PlayersViewing");
		lens.setInventorySlotContents(0, new ItemStack(tag.getCompoundTag("lens")));
		initialXRotation = tag.getFloat("initial_x_rotation");
		initialYRotation = tag.getFloat("initial_y_rotation");

		if (tag.hasKey("initial_zoom"))
			initialZoom = tag.getFloat("initial_zoom");
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(facing, this) ? (T) getNormalHandler() : (T) getInsertOnlyHandler();
		else
			return super.getCapability(capability, facing);
	}

	private IItemHandler getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = new InsertOnlyInvWrapper(lens);

		return insertOnlyHandler;
	}

	private IItemHandler getNormalHandler() {
		if (lensHandler == null)
			lensHandler = new InvWrapper(lens);

		return lensHandler;
	}

	@Override
	public void onInventoryChanged(IInventory container) {
		if (world == null)
			return;

		IBlockState state = world.getBlockState(pos);

		world.notifyBlockUpdate(pos, state, state, 2);
	}

	@Override
	public LensContainer getLensContainer() {
		return lens;
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.REDSTONE) {
			IBlockState newState = world.getBlockState(pos).withProperty(SecurityCameraBlock.POWERED, false);

			world.setBlockState(pos, newState);
			world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
			world.notifyNeighborsOfStateChange(pos.offset(newState.getValue(SecurityCameraBlock.FACING).getOpposite()), getBlockType(), false);
		}
		else if (module == ModuleType.SMART)
			setDefaultViewingDirection(world.getBlockState(pos).getValue(SecurityCameraBlock.FACING), initialZoom);
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
	public void onLoad() {
		super.onLoad();

		if (world != null) {
			IBlockState state = world.getBlockState(pos);

			if (state.getBlock() instanceof SecurityCameraBlock)
				down = state.getValue(SecurityCameraBlock.FACING) == EnumFacing.DOWN;
		}
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == disabled && ((BooleanOption) option).get())
			makeEveryoneStopViewingTheCamera();
	}

	private void makeEveryoneStopViewingTheCamera() {
		if (!world.isRemote) {
			for (EntityPlayer p : ((WorldServer) world).playerEntities) {
				EntityPlayerMP player = (EntityPlayerMP) p;

				if (player.getSpectatingEntity() instanceof SecurityCamera) {
					SecurityCamera camera = (SecurityCamera) player.getSpectatingEntity();

					if (camera.getPosition().equals(pos))
						camera.stopViewing(player);
				}
			}
		}
	}

	public void startViewing() {
		playersViewing++;
		markDirty();
		sync();
	}

	public void stopViewing() {
		playersViewing--;
		markDirty();
		sync();
	}

	public boolean isSomeoneViewing() {
		return playersViewing > 0;
	}

	public void linkFrameForPlayer(EntityPlayerMP player, BlockPos framePos, int chunkLoadingDistance) {
		Set<Long> playerViewedFrames = linkedFrames.computeIfAbsent(player.getUniqueID(), uuid -> new HashSet<>());
		IBlockState state = world.getBlockState(pos);
		ChunkPos cameraChunkPos = new ChunkPos(pos);
		Long cameraChunkPosLong = ChunkPos.asLong(cameraChunkPos.x, cameraChunkPos.z);

		BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.track(this);
		requestChunkSending(player, chunkLoadingDistance);

		if (!FORCE_LOADED_CAMERA_CHUNKS.contains(cameraChunkPosLong)) { //The chunk the camera is in should be forceloaded immediately
			getTicketAndForceChunk(cameraChunkPos);
			chunkForceLoadQueue.add(cameraChunkPosLong);
		}

		for (int x = cameraChunkPos.x - chunkLoadingDistance; x <= cameraChunkPos.x + chunkLoadingDistance; x++) {
			for (int z = cameraChunkPos.z - chunkLoadingDistance; z <= cameraChunkPos.z + chunkLoadingDistance; z++) {
				Long forceLoadingPos = ChunkPos.asLong(x, z);

				((WorldServer) world).getPlayerChunkMap().getOrCreateEntry(x, z).addPlayer(player); //Tracks loaded chunks for the player loading them

				//Currently, only forceloading new chunks (as opposed to stopping their force load) is staggered, since the latter is usually finished a lot faster
				if (!FORCE_LOADED_CAMERA_CHUNKS.contains(forceLoadingPos)) //Only queue chunks for forceloading if they haven't been forceloaded by another camera already
					chunkForceLoadQueue.add(forceLoadingPos);
			}
		}

		if (chunkLoadingDistance > maxChunkLoadingRadius)
			maxChunkLoadingRadius = chunkLoadingDistance;

		playerViewedFrames.add(framePos.toLong());
		world.notifyBlockUpdate(pos, state, state, 3); //Syncs the camera's rotation to all clients again, in case a client is desynched
	}

	public void unlinkFrameForPlayer(UUID playerUUID, BlockPos framePos) {
		if (linkedFrames.containsKey(playerUUID)) {
			Set<Long> linkedFramesPerPlayer = linkedFrames.get(playerUUID);
			ChunkPos cameraChunkPos = new ChunkPos(pos);

			if (framePos != null)
				linkedFramesPerPlayer.remove(framePos.toLong());

			if (framePos == null || linkedFramesPerPlayer.isEmpty()) {
				PlayerList playerList = world.getMinecraftServer().getPlayerList();
				EntityPlayerMP player = playerList.getPlayerByUUID(playerUUID);
				int viewDistance = playerList.getViewDistance();

				linkedFrames.remove(playerUUID);

				if (world instanceof WorldServer)
					untrackAllInvisibleChunks((WorldServer) world, cameraChunkPos, maxChunkLoadingRadius, viewDistance, player);
			}

			if (linkedFrames.isEmpty()) {
				addRecentlyUnviewedCamera(this);
				BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.stopTracking(this);

				for (int x = cameraChunkPos.x - maxChunkLoadingRadius; x <= cameraChunkPos.x + maxChunkLoadingRadius; x++) {
					for (int z = cameraChunkPos.z - maxChunkLoadingRadius; z <= cameraChunkPos.z + maxChunkLoadingRadius; z++) {
						ChunkPos chunkPos = new ChunkPos(x, z);
						Long chunkPosLong = ChunkPos.asLong(chunkPos.x, chunkPos.z);

						if (FORCE_LOADED_CAMERA_CHUNKS.contains(chunkPosLong) && BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getTileEntitiesWithCondition(world, be -> be.shouldKeepChunkForceloaded(chunkPos)).isEmpty()) {
							unforceChunk(chunkPos);
							FORCE_LOADED_CAMERA_CHUNKS.remove(chunkPosLong);
						}
					}
				}

				maxChunkLoadingRadius = 0;
			}
		}
	}

	public static void untrackAllInvisibleChunks(WorldServer world, ChunkPos centerPos, int chunkUntrackingDistance, int playerViewDistance, EntityPlayerMP player) {
		ChunkPos playerChunkPos = new ChunkPos(player.getPosition());

		for (int cx = centerPos.x - chunkUntrackingDistance; cx <= centerPos.x + chunkUntrackingDistance; cx++) {
			for (int cz = centerPos.z - chunkUntrackingDistance; cz <= centerPos.z + chunkUntrackingDistance; cz++) {
				if (cx >= playerChunkPos.x - playerViewDistance && cx <= playerChunkPos.x + playerViewDistance && cz >= playerChunkPos.z - playerViewDistance && cz <= playerChunkPos.z + playerViewDistance)
					continue; //Do not remove players from chunks that the player entity is supposed to see
				else if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getTileEntitiesWithCondition(world, be -> be.shouldKeepChunkTracked(player.getUniqueID(), centerPos.x, centerPos.z)).isEmpty())
					continue; //Do not remove players from chunks that are in view of a currently active frame feed

				world.getPlayerChunkMap().getOrCreateEntry(cx, cz).removePlayer(player);
			}
		}
	}

	private void getTicketAndForceChunk(ChunkPos chunkPos) {
		if (chunkTicket == null)
			chunkTicket = ForgeChunkManager.requestTicket(SecurityCraft.instance, world, Type.NORMAL);

		ForgeChunkManager.forceChunk(chunkTicket, chunkPos);
	}

	private void unforceChunk(ChunkPos chunkPos) {
		if (chunkTicket != null)
			ForgeChunkManager.unforceChunk(chunkTicket, chunkPos);
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

		if (world.isRemote)
			CameraController.removeAllFrameLinks(GlobalPos.of(world.provider.getDimension(), pos));
	}

	public boolean hasPlayerFrameLink(EntityPlayer player) {
		return linkedFrames.containsKey(player.getUniqueID());
	}

	public boolean isFrameLinked(EntityPlayer player, BlockPos framePos) {
		return hasPlayerFrameLink(player) && linkedFrames.get(player.getUniqueID()).contains(framePos.toLong());
	}

	public void requestChunkSending(EntityPlayerMP player, int chunkLoadingDistance) {
		setChunkLoadingDistance(player, chunkLoadingDistance);
	}

	public ChunkTrackingView getCameraFeedChunks(EntityPlayerMP player) {
		return cameraFeedChunks.get(player.getUniqueID());
	}

	public void clearCameraFeedChunks(EntityPlayerMP player) {
		cameraFeedChunks.remove(player.getUniqueID());
	}

	public void setChunkLoadingDistance(EntityPlayerMP player, int chunkLoadingDistance) {
		cameraFeedChunks.put(player.getUniqueID(), new ChunkTrackingView(new ChunkPos(pos), chunkLoadingDistance));
	}

	public boolean shouldKeepChunkTracked(UUID uuid, int chunkX, int chunkZ) {
		return cameraFeedChunks.containsKey(uuid) && cameraFeedChunks.get(uuid).contains(chunkX, chunkZ);
	}

	public boolean shouldKeepChunkForceloaded(ChunkPos chunkPos) {
		ChunkPos cameraPos = new ChunkPos(pos);

		return chunkPos.x >= cameraPos.x - maxChunkLoadingRadius && chunkPos.x <= cameraPos.x + maxChunkLoadingRadius && chunkPos.z >= cameraPos.z - maxChunkLoadingRadius && chunkPos.z <= cameraPos.z + maxChunkLoadingRadius;
	}

	public static void addRecentlyUnviewedCamera(SecurityCameraBlockEntity camera) {
		for (EntityPlayerMP player : camera.world.getMinecraftServer().getPlayerList().getPlayers()) {
			Set<SecurityCameraBlockEntity> unviewingCameras = RECENTLY_UNVIEWED_CAMERAS.computeIfAbsent(player, p -> new HashSet<>());

			unviewingCameras.add(camera);
		}
	}

	public static boolean hasRecentlyUnviewedCameras(EntityPlayerMP player) {
		return RECENTLY_UNVIEWED_CAMERAS.containsKey(player);
	}

	public static Set<SecurityCameraBlockEntity> fetchRecentlyUnviewedCameras(EntityPlayerMP player) {
		return RECENTLY_UNVIEWED_CAMERAS.remove(player);
	}

	public static void resetForceLoadingCounter() {
		forceLoadingCounter = 0;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public double getOriginalCameraRotation() {
		return oCameraRotation;
	}

	public double getCameraRotation() {
		return cameraRotation;
	}

	public boolean isDown() {
		return down;
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

	public float getDefaultYRotation(EnumFacing facing) {
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

	public void setDefaultViewingDirection(EnumFacing facing, float zoom) {
		setDefaultViewingDirection(getDefaultXRotation(), getDefaultYRotation(facing), zoom);
	}

	public void setDefaultViewingDirection(float initialXRotation, float initialYRotation, float initialZoom) {
		this.initialXRotation = initialXRotation;
		this.initialYRotation = initialYRotation;
		this.initialZoom = initialZoom;
		markDirty();
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
