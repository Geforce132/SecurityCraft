package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class SecurityCamera extends Entity {
	protected final double cameraSpeed = ConfigHandler.CLIENT.cameraSpeed.get();
	public int screenshotSoundCooldown = 0;
	protected int redstoneCooldown = 0;
	protected int toggleNightVisionCooldown = 0;
	private boolean shouldProvideNightVision = false;
	protected float zoomAmount = 1F;
	protected boolean zooming = false;
	private int viewDistance = -1;
	private boolean loadedChunks = false;

	public SecurityCamera(EntityType<SecurityCamera> type, World world) {
		super(SCContent.eTypeSecurityCamera, world);
		noPhysics = true;
		forcedLoading = true;
	}

	public SecurityCamera(World world, BlockPos pos) {
		this(SCContent.eTypeSecurityCamera, world);

		TileEntity te = world.getBlockEntity(pos);

		if (!(te instanceof SecurityCameraBlockEntity)) {
			remove();
			return;
		}

		SecurityCameraBlockEntity cam = (SecurityCameraBlockEntity) te;
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.5D;
		double z = pos.getZ() + 0.5D;

		if (cam.down)
			y += 0.25D;

		setPos(x, y, z);
		setInitialPitchYaw(cam);
	}

	public SecurityCamera(World world, BlockPos pos, SecurityCamera oldCamera) {
		this(world, pos);
		oldCamera.discardCamera();
	}

	private void setInitialPitchYaw(SecurityCameraBlockEntity te) {
		xRot = 30F;

		Direction facing = level.getBlockState(blockPosition()).getValue(SecurityCameraBlock.FACING);

		if (facing == Direction.NORTH)
			yRot = 180F;
		else if (facing == Direction.WEST)
			yRot = 90F;
		else if (facing == Direction.SOUTH)
			yRot = 0F;
		else if (facing == Direction.EAST)
			yRot = 270F;
		else if (facing == Direction.DOWN)
			xRot = 75F;
	}

	@Override
	protected boolean repositionEntityAfterLoad() {
		return false;
	}

	@Override
	public void tick() {
		if (level.isClientSide) {
			if (screenshotSoundCooldown > 0)
				screenshotSoundCooldown -= 1;

			if (redstoneCooldown > 0)
				redstoneCooldown -= 1;

			if (toggleNightVisionCooldown > 0)
				toggleNightVisionCooldown -= 1;

			if (shouldProvideNightVision)
				SecurityCraft.channel.sendToServer(new GiveNightVision());
		}
		else if (level.getBlockState(blockPosition()).getBlock() != SCContent.SECURITY_CAMERA.get())
			remove();
	}

	public void toggleRedstonePower() {
		BlockPos pos = blockPosition();

		if (((IModuleInventory) level.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			SecurityCraft.channel.sendToServer(new SetCameraPowered(pos, !level.getBlockState(pos).getValue(SecurityCameraBlock.POWERED)));
	}

	public void toggleNightVision() {
		toggleNightVisionCooldown = 30;
		shouldProvideNightVision = !shouldProvideNightVision;
	}

	public float getZoomAmount() {
		return zoomAmount;
	}

	public boolean isCameraDown() {
		return level.getBlockEntity(blockPosition()) instanceof SecurityCameraBlockEntity && ((SecurityCameraBlockEntity) level.getBlockEntity(blockPosition())).down;
	}

	//here to make this method accessible to CameraController
	@Override
	protected void setRot(float yaw, float pitch) {
		super.setRot(yaw, pitch);
	}

	public void stopViewing(ServerPlayerEntity player) {
		if (!level.isClientSide) {
			discardCamera();
			player.camera = player;
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> player), new SetCameraView(player));
		}
	}

	public void discardCamera() {
		if (!level.isClientSide) {
			TileEntity te = level.getBlockEntity(blockPosition());

			if (te instanceof SecurityCameraBlockEntity)
				((SecurityCameraBlockEntity) te).stopViewing();

			SectionPos chunkPos = SectionPos.of(blockPosition());
			int viewDistance = this.viewDistance <= 0 ? level.getServer().getPlayerList().getViewDistance() : this.viewDistance;

			for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
				for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
					ForgeChunkManager.forceChunk((ServerWorld) level, SecurityCraft.MODID, this, x, z, false, false);
				}
			}
		}

		remove();
	}

	public void setHasLoadedChunks(int initialViewDistance) {
		loadedChunks = true;
		viewDistance = initialViewDistance;
	}

	public boolean hasLoadedChunks() {
		return loadedChunks;
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	public void addAdditionalSaveData(CompoundNBT tag) {}

	@Override
	public void readAdditionalSaveData(CompoundNBT tag) {}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
