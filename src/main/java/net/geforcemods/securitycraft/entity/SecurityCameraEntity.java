package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetCameraRotation;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SecurityCameraEntity extends Entity{

	private final double CAMERA_SPEED = ConfigHandler.CLIENT.cameraSpeed.get();
	private double cameraUseX;
	private double cameraUseY;
	private double cameraUseZ;
	private float cameraUseYaw;
	private float cameraUsePitch;
	private int id;
	public int screenshotSoundCooldown = 0;
	private int redstoneCooldown = 0;
	private int toggleNightVisionCooldown = 0;
	private int toggleLightCooldown = 0;
	private boolean shouldProvideNightVision = false;
	private float zoomAmount = 1F;
	private boolean zooming = false;

	public SecurityCameraEntity(EntityType<SecurityCameraEntity> type, Level world){
		super(SCContent.eTypeSecurityCamera, world);
		noPhysics = true;
	}

	public SecurityCameraEntity(Level world, double x, double y, double z, int id, Player player){
		this(SCContent.eTypeSecurityCamera, world);
		cameraUseX = player.getX();
		cameraUseY = player.getY();
		cameraUseZ = player.getZ();
		cameraUseYaw = player.getYRot();
		cameraUsePitch = player.getXRot();
		this.id = id;
		setPos(x + 0.5D, y, z + 0.5D);

		BlockEntity te = world.getBlockEntity(blockPosition());

		if(te instanceof SecurityCameraTileEntity cam)
			setInitialPitchYaw(cam);
	}

	public SecurityCameraEntity(Level world, double x, double y, double z, int id, SecurityCameraEntity camera){
		this(SCContent.eTypeSecurityCamera, world);
		cameraUseX = camera.cameraUseX;
		cameraUseY = camera.cameraUseY;
		cameraUseZ = camera.cameraUseZ;
		cameraUseYaw = camera.cameraUseYaw;
		cameraUsePitch = camera.cameraUsePitch;
		this.id = id;
		setPos(x + 0.5D, y, z + 0.5D);

		BlockEntity te = world.getBlockEntity(blockPosition());

		if(te instanceof SecurityCameraTileEntity cam)
			setInitialPitchYaw(cam);
	}

	private void setInitialPitchYaw(SecurityCameraTileEntity te)
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
		if(level.isClientSide && isVehicle()){
			Player lowestEntity = (Player)getPassengers().get(0);

			if(lowestEntity != Minecraft.getInstance().player)
				return;

			if(screenshotSoundCooldown > 0)
				screenshotSoundCooldown -= 1;

			if(redstoneCooldown > 0)
				redstoneCooldown -= 1;

			if(toggleNightVisionCooldown > 0)
				toggleNightVisionCooldown -= 1;

			if(toggleLightCooldown > 0)
				toggleLightCooldown -= 1;

			if(lowestEntity.getYRot() != getYRot()){
				lowestEntity.absMoveTo(lowestEntity.getX(), lowestEntity.getY(), lowestEntity.getZ(), getYRot(), getXRot());
				lowestEntity.setYRot(getYRot());
			}

			if(lowestEntity.getXRot() != getXRot())
				lowestEntity.absMoveTo(lowestEntity.getX(), lowestEntity.getY(), lowestEntity.getZ(), getYRot(), getXRot());

			checkKeysPressed();

			if(getPassengers().size() != 0 && shouldProvideNightVision)
				SecurityCraft.channel.sendToServer(new GiveNightVision());
		}

		if(!level.isClientSide)
			if(getPassengers().size() == 0 || level.getBlockState(blockPosition()).getBlock() != SCContent.SECURITY_CAMERA.get()){
				discard();
				return;
			}
	}

	private void checkKeysPressed() {
		if(Minecraft.getInstance().options.keyUp.isDown())
			moveViewUp();

		if(Minecraft.getInstance().options.keyDown.isDown())
			moveViewDown();

		if(Minecraft.getInstance().options.keyLeft.isDown())
			moveViewLeft();

		if(Minecraft.getInstance().options.keyRight.isDown())
			moveViewRight();

		if(KeyBindings.cameraEmitRedstone.consumeClick() && redstoneCooldown == 0){
			setRedstonePower();
			redstoneCooldown = 30;
		}

		if(KeyBindings.cameraActivateNightVision.consumeClick() && toggleNightVisionCooldown == 0)
			enableNightVision();

		if(KeyBindings.cameraZoomIn.consumeClick())
		{
			zoomIn();
			zooming = true;
		}
		else if(KeyBindings.cameraZoomOut.consumeClick())
		{
			zoomOut();
			zooming = true;
		}
		else
			zooming = false;
	}

	public void moveViewUp() {
		if(isCameraDown())
		{
			if(getXRot() > 40F)
				setRot(getYRot(), (float)(getXRot() - CAMERA_SPEED));
		}
		else if(getXRot() > -25F)
			setRot(getYRot(), (float)(getXRot() - CAMERA_SPEED));

		updateServerRotation();
	}

	public void moveViewDown(){
		if(isCameraDown())
		{
			if(getXRot() < 100F)
				setRot(getYRot(), (float)(getXRot() + CAMERA_SPEED));
		}
		else if(getXRot() < 60F)
			setRot(getYRot(), (float)(getXRot() + CAMERA_SPEED));

		updateServerRotation();
	}

	public void moveViewLeft() {
		BlockState state = level.getBlockState(blockPosition());

		if(state.hasProperty(SecurityCameraBlock.FACING)) {
			Direction facing = state.getValue(SecurityCameraBlock.FACING);
			float xRot = getXRot();
			float yRot = getYRot();

			if(facing == Direction.EAST)
			{
				if((yRot - CAMERA_SPEED) > -180F)
					setRot((float)(yRot - CAMERA_SPEED), xRot);
			}
			else if(facing == Direction.WEST)
			{
				if((yRot - CAMERA_SPEED) > 0F)
					setRot((float)(yRot - CAMERA_SPEED), xRot);
			}
			else if(facing == Direction.NORTH)
			{
				// Handles some problems the occurs from the way the rotationYaw value works in MC
				if((((yRot - CAMERA_SPEED) > 90F) && ((yRot - CAMERA_SPEED) < 185F)) || (((yRot - CAMERA_SPEED) > -190F) && ((yRot - CAMERA_SPEED) < -90F)))
					setRot((float)(yRot - CAMERA_SPEED), xRot);
			}
			else if(facing == Direction.SOUTH)
			{
				if((yRot - CAMERA_SPEED) > -90F)
					setRot((float)(yRot - CAMERA_SPEED), xRot);
			}
			else if(facing == Direction.DOWN)
				setRot((float)(yRot - CAMERA_SPEED), xRot);

			updateServerRotation();
		}
	}

	public void moveViewRight(){
		BlockState state = level.getBlockState(blockPosition());

		if(state.hasProperty(SecurityCameraBlock.FACING)) {
			Direction facing = state.getValue(SecurityCameraBlock.FACING);
			float xRot = getXRot();
			float yRot = getYRot();

			if(facing == Direction.EAST)
			{
				if((yRot + CAMERA_SPEED) < 0F)
					setRot((float)(yRot + CAMERA_SPEED), xRot);
			}
			else if(facing == Direction.WEST)
			{
				if((yRot + CAMERA_SPEED) < 180F)
					setRot((float)(yRot + CAMERA_SPEED), xRot);
			}
			else if(facing == Direction.NORTH)
			{
				if((((yRot + CAMERA_SPEED) > 85F) && ((yRot + CAMERA_SPEED) < 185F)) || ((yRot + CAMERA_SPEED) < -95F) && ((yRot + CAMERA_SPEED) > -180F))
					setRot((float)(yRot + CAMERA_SPEED), xRot);
			}
			else if(facing == Direction.SOUTH)
			{
				if((yRot + CAMERA_SPEED) < 90F)
					setRot((float)(yRot + CAMERA_SPEED), xRot);
			}
			else if(facing == Direction.DOWN)
				setRot((float)(yRot + CAMERA_SPEED), xRot);

			updateServerRotation();
		}
	}

	public void zoomIn()
	{
		zoomAmount = Math.max(zoomAmount - 0.1F, 0.1F);

		if(!zooming)
			Minecraft.getInstance().level.playLocalSound(blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);
	}

	public void zoomOut()
	{
		zoomAmount = Math.min(zoomAmount + 0.1F, 1.5F);

		if(!zooming)
			Minecraft.getInstance().level.playLocalSound(blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);
	}

	public void setRedstonePower() {
		BlockPos pos = blockPosition();

		if(((IModuleInventory) level.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			SecurityCraft.channel.sendToServer(new SetCameraPowered(pos, !level.getBlockState(pos).getValue(SecurityCameraBlock.POWERED)));
	}

	public void enableNightVision() {
		toggleNightVisionCooldown = 30;
		shouldProvideNightVision = !shouldProvideNightVision;
	}

	public float getZoomAmount(){
		return zoomAmount;
	}

	private void updateServerRotation(){
		SecurityCraft.channel.sendToServer(new SetCameraRotation(getYRot(), getXRot()));
	}

	private boolean isCameraDown()
	{
		return level.getBlockEntity(blockPosition()) instanceof SecurityCameraTileEntity cam && cam.down;
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
