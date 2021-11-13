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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class SecurityCamera extends Entity
{
	protected final double cameraSpeed = ConfigHandler.CLIENT.cameraSpeed.get();
	private double cameraUseX;
	private double cameraUseY;
	private double cameraUseZ;
	private float cameraUseYaw;
	private float cameraUsePitch;
	private int id;
	public int screenshotSoundCooldown = 0;
	protected int redstoneCooldown = 0;
	protected int toggleNightVisionCooldown = 0;
	private int toggleLightCooldown = 0;
	private boolean shouldProvideNightVision = false;
	protected float zoomAmount = 1F;
	protected boolean zooming = false;

	public SecurityCamera(EntityType<SecurityCamera> type, Level world){
		super(SCContent.eTypeSecurityCamera, world);
		noPhysics = true;
	}

	public SecurityCamera(Level world, BlockPos pos, int id, Player player){
		this(SCContent.eTypeSecurityCamera, world);
		cameraUseX = player.getX();
		cameraUseY = player.getY();
		cameraUseZ = player.getZ();
		cameraUseYaw = player.getYRot();
		cameraUsePitch = player.getXRot();
		this.id = id;
		setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

		BlockEntity te = world.getBlockEntity(blockPosition());

		if(te instanceof SecurityCameraBlockEntity cam)
		{
			setInitialPitchYaw(cam);

			if(cam.down)
				setPos(pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D);
		}
	}

	public SecurityCamera(Level world, BlockPos pos, int id, SecurityCamera camera){
		this(SCContent.eTypeSecurityCamera, world);
		cameraUseX = camera.cameraUseX;
		cameraUseY = camera.cameraUseY;
		cameraUseZ = camera.cameraUseZ;
		cameraUseYaw = camera.cameraUseYaw;
		cameraUsePitch = camera.cameraUsePitch;
		this.id = id;
		setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

		BlockEntity te = world.getBlockEntity(blockPosition());

		if(te instanceof SecurityCameraBlockEntity cam)
		{
			setInitialPitchYaw(cam);

			if(cam.down)
				setPos(pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D);
		}

		camera.discard();
	}

	private void setInitialPitchYaw(SecurityCameraBlockEntity te)
	{
		if(te != null && te.hasModule(ModuleType.SMART) && te.lastPitch != Float.MAX_VALUE && te.lastYaw != Float.MAX_VALUE)
		{
			setXRot(te.lastPitch);
			setYRot(te.lastYaw);
		}
		else
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
				setYRot(75);
		}
	}

	@Override
	public double getPassengersRidingOffset(){
		return -0.75D;
	}

	@Override
	protected boolean repositionEntityAfterLoad(){
		return false;
	}

	@Override
	public boolean canBeRiddenInWater(Entity rider){
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
	public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity)
	{
		livingEntity.setYRot(cameraUseYaw % 360.0F);
		livingEntity.setXRot(Mth.clamp(cameraUsePitch, -90.0F, 90.0F) % 360.0F);
		livingEntity.yRotO = livingEntity.getYRot();
		livingEntity.xRotO = livingEntity.getXRot();
		return getPreviousPlayerPos();
	}

	public Vec3 getPreviousPlayerPos()
	{
		return new Vec3(cameraUseX, cameraUseY, cameraUseZ);
	}

	@Override
	protected void defineSynchedData(){}

	@Override
	public void addAdditionalSaveData(CompoundTag tag){
		tag.putInt("CameraID", id);
		tag.putDouble("cameraUseX", cameraUseX);
		tag.putDouble("cameraUseY", cameraUseY);
		tag.putDouble("cameraUseZ", cameraUseZ);
		tag.putDouble("cameraUseYaw", cameraUseYaw);
		tag.putDouble("cameraUsePitch", cameraUsePitch);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag){
		id = tag.getInt("CameraID");
		cameraUseX = tag.getDouble("cameraUseX");
		cameraUseY = tag.getDouble("cameraUseY");
		cameraUseZ = tag.getDouble("cameraUseZ");
		cameraUseYaw = tag.getFloat("cameraUseYaw");
		cameraUsePitch = tag.getFloat("cameraUsePitch");
	}

	@Override
	public Packet<?> getAddEntityPacket()
	{
		return new ClientboundAddEntityPacket(this);
	}
}
