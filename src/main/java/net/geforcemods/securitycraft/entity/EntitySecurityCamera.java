package net.geforcemods.securitycraft.entity;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.SetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetCameraRotation;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySecurityCamera extends Entity{

	private final float CAMERA_SPEED = ConfigHandler.cameraSpeed;

	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;

	private double cameraUseX;
	private double cameraUseY;
	private double cameraUseZ;
	private float cameraUseYaw;
	private float cameraUsePitch;

	private int id;
	private int screenshotCooldown = 0;
	private int redstoneCooldown = 0;
	private int toggleNightVisionCooldown = 0;
	private int toggleLightCooldown = 0;
	private boolean shouldProvideNightVision = false;
	private float zoomAmount = 1F;

	private String playerViewingName = null;
	private boolean zooming = false;

	public EntitySecurityCamera(World world){
		super(world);
		noClip = true;
		height = 0.0001F;
		width = 0.0001F;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id, EntityPlayer player){
		this(world);
		blockPosX = (int) x;
		blockPosY = (int) y;
		blockPosZ = (int) z;
		cameraUseX = player.posX;
		cameraUseY = player.posY;
		cameraUseZ = player.posZ;
		cameraUseYaw = player.rotationYaw;
		cameraUsePitch = player.rotationPitch;
		this.id = id;
		playerViewingName = player.getName();
		setPosition(x + 0.5D, y, z + 0.5D);

		TileEntity te = world.getTileEntity(getPosition());

		if(te instanceof TileEntitySecurityCamera)
			setInitialPitchYaw((TileEntitySecurityCamera)te);
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id, EntitySecurityCamera camera){
		this(world);
		blockPosX = (int) x;
		blockPosY = (int) y;
		blockPosZ = (int) z;
		cameraUseX = camera.cameraUseX;
		cameraUseY = camera.cameraUseY;
		cameraUseZ = camera.cameraUseZ;
		cameraUseYaw = camera.cameraUseYaw;
		cameraUsePitch = camera.cameraUsePitch;
		this.id = id;
		playerViewingName = camera.playerViewingName;
		setPosition(x + 0.5D, y, z + 0.5D);

		TileEntity te = world.getTileEntity(getPosition());

		if(te instanceof TileEntitySecurityCamera)
			setInitialPitchYaw((TileEntitySecurityCamera)te);
	}

	private void setInitialPitchYaw(TileEntitySecurityCamera te)
	{
		if(te != null && te.hasModule(EnumModuleType.SMART) && te.lastPitch != Float.MAX_VALUE && te.lastYaw != Float.MAX_VALUE)
		{
			rotationPitch = te.lastPitch;
			rotationYaw = te.lastYaw;
		}
		else
		{
			rotationPitch = 30F;

			EnumFacing facing = BlockUtils.getBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

			if(facing == EnumFacing.NORTH)
				rotationYaw = 180F;
			else if(facing == EnumFacing.WEST)
				rotationYaw = 90F;
			else if(facing == EnumFacing.SOUTH)
				rotationYaw = 0F;
			else if(facing == EnumFacing.EAST)
				rotationYaw = 270F;
			else if(facing == EnumFacing.DOWN)
				rotationPitch = 75;
		}
	}

	@Override
	public double getMountedYOffset(){
		return height * -7500D;
	}

	@Override
	protected boolean shouldSetPosAfterLoading(){
		return false;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider){
		return true;
	}

	@Override
	public void onUpdate(){
		if(world.isRemote && isBeingRidden()){
			EntityPlayer lowestEntity = (EntityPlayer)getPassengers().get(0);

			if(lowestEntity != Minecraft.getMinecraft().player)
				return;

			if(screenshotCooldown > 0)
				screenshotCooldown -= 1;

			if(redstoneCooldown > 0)
				redstoneCooldown -= 1;

			if(toggleNightVisionCooldown > 0)
				toggleNightVisionCooldown -= 1;

			if(toggleLightCooldown > 0)
				toggleLightCooldown -= 1;

			if(lowestEntity.rotationYaw != rotationYaw){
				lowestEntity.setPositionAndRotation(lowestEntity.posX, lowestEntity.posY, lowestEntity.posZ, rotationYaw, rotationPitch);
				lowestEntity.rotationYaw = rotationYaw;
			}

			if(lowestEntity.rotationPitch != rotationPitch)
				lowestEntity.setPositionAndRotation(lowestEntity.posX, lowestEntity.posY, lowestEntity.posZ, rotationYaw, rotationPitch);

			checkKeysPressed();

			if(Mouse.hasWheel() && Mouse.isButtonDown(2) && screenshotCooldown == 0){
				screenshotCooldown = 30;
				ClientUtils.takeScreenshot();
				Minecraft.getMinecraft().world.playSound(new BlockPos(posX, posY, posZ), SoundEvent.REGISTRY.getObject(SCSounds.CAMERASNAP.location), SoundCategory.BLOCKS, 1.0F, 1.0F, true);
			}

			if(getPassengers().size() != 0 && shouldProvideNightVision)
				SecurityCraft.network.sendToServer(new GiveNightVision());
		}

		if(!world.isRemote)
			if(getPassengers().size() == 0 || BlockUtils.getBlock(world, blockPosX, blockPosY, blockPosZ) != SCContent.securityCamera){
				setDead();
				return;
			}
	}

	@SideOnly(Side.CLIENT)
	private void checkKeysPressed() {
		if(Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown())
			moveViewUp();

		if(Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown())
			moveViewDown();

		if(Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown())
			moveViewLeft();

		if(Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown())
			moveViewRight();

		if(KeyBindings.cameraEmitRedstone.isPressed() && redstoneCooldown == 0){
			setRedstonePower();
			redstoneCooldown = 30;
		}

		if(KeyBindings.cameraActivateNightVision.isPressed() && toggleNightVisionCooldown == 0)
			enableNightVision();

		if(KeyBindings.cameraZoomIn.isPressed())
		{
			zoomIn();
			zooming = true;
		}
		else if(KeyBindings.cameraZoomOut.isPressed())
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
			if(rotationPitch > 40F)
				setRotation(rotationYaw, rotationPitch -= CAMERA_SPEED);
		}
		else if(rotationPitch > -25F)
			setRotation(rotationYaw, rotationPitch -= CAMERA_SPEED);

		updateServerRotation();
	}

	public void moveViewDown(){
		if(isCameraDown())
		{
			if(rotationPitch < 100F)
				setRotation(rotationYaw, rotationPitch += CAMERA_SPEED);
		}
		else if(rotationPitch < 60F)
			setRotation(rotationYaw, rotationPitch += CAMERA_SPEED);

		updateServerRotation();
	}

	public void moveViewLeft() {
		if(BlockUtils.hasBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING)) {
			EnumFacing facing = BlockUtils.getBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

			if(facing == EnumFacing.EAST)
			{
				if((rotationYaw - CAMERA_SPEED) > -180F)
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(facing == EnumFacing.WEST)
			{
				if((rotationYaw - CAMERA_SPEED) > 0F)
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(facing == EnumFacing.NORTH)
			{
				// Handles some problems the occurs from the way the rotationYaw value works in MC
				if((((rotationYaw - CAMERA_SPEED) > 90F) && ((rotationYaw - CAMERA_SPEED) < 185F)) || (((rotationYaw - CAMERA_SPEED) > -190F) && ((rotationYaw - CAMERA_SPEED) < -90F)))
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(facing == EnumFacing.SOUTH)
			{
				if((rotationYaw - CAMERA_SPEED) > -90F)
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(facing == EnumFacing.DOWN)
				setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);

			updateServerRotation();
		}
	}

	public void moveViewRight(){
		if(BlockUtils.hasBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING)) {
			EnumFacing facing = BlockUtils.getBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

			if(facing == EnumFacing.EAST)
			{
				if((rotationYaw + CAMERA_SPEED) < 0F)
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(facing == EnumFacing.WEST)
			{
				if((rotationYaw + CAMERA_SPEED) < 180F)
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(facing == EnumFacing.NORTH)
			{
				if((((rotationYaw + CAMERA_SPEED) > 85F) && ((rotationYaw + CAMERA_SPEED) < 185F)) || ((rotationYaw + CAMERA_SPEED) < -95F) && ((rotationYaw + CAMERA_SPEED) > -180F))
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(facing == EnumFacing.SOUTH)
			{
				if((rotationYaw + CAMERA_SPEED) < 90F)
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(facing == EnumFacing.DOWN)
				setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);

			updateServerRotation();
		}
	}

	public void zoomIn()
	{
		zoomAmount = Math.max(zoomAmount - 0.1F, 0.1F);

		if(!zooming)
			Minecraft.getMinecraft().world.playSound(getPosition(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
	}

	public void zoomOut()
	{
		zoomAmount = Math.min(zoomAmount + 0.1F, 1.5F);

		if(!zooming)
			Minecraft.getMinecraft().world.playSound(getPosition(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
	}

	public void setRedstonePower() {
		BlockPos pos = BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ));

		if(((IModuleInventory) world.getTileEntity(pos)).hasModule(EnumModuleType.REDSTONE))
			if(BlockUtils.getBlockProperty(world, pos, BlockSecurityCamera.POWERED))
				SecurityCraft.network.sendToServer(new SetCameraPowered(pos, false));
			else if(!BlockUtils.getBlockProperty(world, pos, BlockSecurityCamera.POWERED))
				SecurityCraft.network.sendToServer(new SetCameraPowered(pos, true));
	}

	public void enableNightVision() {
		toggleNightVisionCooldown = 30;
		shouldProvideNightVision = !shouldProvideNightVision;
	}

	public float getZoomAmount(){
		return zoomAmount;
	}

	@SideOnly(Side.CLIENT)
	private void updateServerRotation(){
		SecurityCraft.network.sendToServer(new SetCameraRotation(rotationYaw, rotationPitch));
	}

	private boolean isCameraDown()
	{
		return world.getTileEntity(getPosition()) instanceof TileEntitySecurityCamera && ((TileEntitySecurityCamera)world.getTileEntity(getPosition())).down;
	}

	@Override
	public void setDead(){
		super.setDead();

		if(playerViewingName != null && PlayerUtils.isPlayerOnline(playerViewingName)){
			EntityPlayer player = PlayerUtils.getPlayerFromName(playerViewingName);
			player.setPositionAndUpdate(cameraUseX, cameraUseY, cameraUseZ);
			SecurityCraft.network.sendTo(new SetPlayerPositionAndRotation(cameraUseX, cameraUseY, cameraUseZ, cameraUseYaw, cameraUsePitch), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void entityInit(){}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag){
		tag.setInteger("CameraID", id);
		tag.setString("playerName", playerViewingName);
		tag.setDouble("cameraUseX", cameraUseX);
		tag.setDouble("cameraUseY", cameraUseY);
		tag.setDouble("cameraUseZ", cameraUseZ);
		tag.setDouble("cameraUseYaw", cameraUseYaw);
		tag.setDouble("cameraUsePitch", cameraUsePitch);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag){
		id = tag.getInteger("CameraID");
		playerViewingName = tag.getString("playerName");
		cameraUseX = tag.getDouble("cameraUseX");
		cameraUseY = tag.getDouble("cameraUseY");
		cameraUseZ = tag.getDouble("cameraUseZ");
		cameraUseYaw = tag.getFloat("cameraUseYaw");
		cameraUsePitch = tag.getFloat("cameraUsePitch");
	}

}
