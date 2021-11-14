package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

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
		oldCamera.discard();
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
			setYRot(75F);
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
}
