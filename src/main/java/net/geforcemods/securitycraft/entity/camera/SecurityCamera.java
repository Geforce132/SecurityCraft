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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class SecurityCamera extends Entity
{
	protected final double cameraSpeed = ConfigHandler.CLIENT.cameraSpeed.get();
	public int screenshotSoundCooldown = 0;
	protected int redstoneCooldown = 0;
	protected int toggleNightVisionCooldown = 0;
	private int toggleLightCooldown = 0;
	private boolean shouldProvideNightVision = false;
	protected float zoomAmount = 1F;
	protected boolean zooming = false;
	private int viewDistance = -1;
	private boolean loadedChunks = false;

	public SecurityCamera(EntityType<SecurityCamera> type, Level level){
		super(SCContent.eTypeSecurityCamera, level);
		noPhysics = true;
	}

	public SecurityCamera(Level level, BlockPos pos){
		this(SCContent.eTypeSecurityCamera, level);

		BlockEntity be = level.getBlockEntity(pos);

		if(!(be instanceof SecurityCameraBlockEntity cam))
		{
			discard();
			return;
		}

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.5D;
		double z = pos.getZ() + 0.5D;

		if(cam.down)
			y += 0.25D;

		setPos(x, y, z);
		setInitialPitchYaw(cam);
	}

	public SecurityCamera(Level level, BlockPos pos, SecurityCamera oldCamera){
		this(level, pos);
		oldCamera.discardCamera();
	}

	private void setInitialPitchYaw(SecurityCameraBlockEntity te)
	{
		setXRot(30F);

		Direction facing = level.getBlockState(blockPosition()).getValue(SecurityCameraBlock.FACING);

		if(facing == Direction.NORTH)
			setYRot(180F);
		else if(facing == Direction.WEST)
			setYRot(90F);
		else if(facing == Direction.SOUTH)
			setYRot(0F);
		else if(facing == Direction.EAST)
			setYRot(270F);
		else if(facing == Direction.DOWN)
			setXRot(75F);
	}

	@Override
	protected boolean repositionEntityAfterLoad(){
		return false;
	}

	@Override
	public void tick(){
		if(level.isClientSide){
			if(screenshotSoundCooldown > 0)
				screenshotSoundCooldown--;

			if(redstoneCooldown > 0)
				redstoneCooldown--;

			if(toggleNightVisionCooldown > 0)
				toggleNightVisionCooldown--;

			if(toggleLightCooldown > 0)
				toggleLightCooldown--;

			if(shouldProvideNightVision)
				SecurityCraft.channel.sendToServer(new GiveNightVision());
		}
		else if(level.getBlockState(blockPosition()).getBlock() != SCContent.SECURITY_CAMERA.get()){
			discard();
			return;
		}
	}

	public void toggleRedstonePower() {
		BlockPos pos = blockPosition();

		if(((IModuleInventory) level.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			SecurityCraft.channel.sendToServer(new SetCameraPowered(pos, !level.getBlockState(pos).getValue(SecurityCameraBlock.POWERED)));
	}

	public void toggleNightVision() {
		toggleNightVisionCooldown = 30;
		shouldProvideNightVision = !shouldProvideNightVision;
	}

	public float getZoomAmount(){
		return zoomAmount;
	}

	public boolean isCameraDown()
	{
		return level.getBlockEntity(blockPosition()) instanceof SecurityCameraBlockEntity cam && cam.down;
	}

	public void setRotation(float yaw, float pitch)
	{
		setRot(yaw, pitch);
	}

	public void stopViewing(ServerPlayer player) {
		if (!level.isClientSide) {
			discardCamera();
			player.camera = player;
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> player), new SetCameraView(player));
		}
	}

	public void discardCamera() {
		if (!level.isClientSide) {
			if(level.getBlockEntity(blockPosition()) instanceof SecurityCameraBlockEntity camBe)
				camBe.stopViewing();

			SectionPos chunkPos = SectionPos.of(blockPosition());
			int viewDistance = this.viewDistance <= 0 ? level.getServer().getPlayerList().getViewDistance() : this.viewDistance;

			for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
				for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
					ForgeChunkManager.forceChunk((ServerLevel)level, SecurityCraft.MODID, this, x, z, false, false);
				}
			}
		}

		discard();
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
	public void addAdditionalSaveData(CompoundTag tag) {}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {}

	@Override
	public Packet<?> getAddEntityPacket()
	{
		return new ClientboundAddEntityPacket(this);
	}

	@Override
	public boolean isAlwaysTicking() {
		return true;
	}
}
