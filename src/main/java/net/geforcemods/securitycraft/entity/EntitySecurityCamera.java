package net.geforcemods.securitycraft.entity;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCSetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.packets.PacketGivePotionEffect;
import net.geforcemods.securitycraft.network.packets.PacketSAddModules;
import net.geforcemods.securitycraft.network.packets.PacketSSetCameraRotation;
import net.geforcemods.securitycraft.network.packets.PacketSSetOwner;
import net.geforcemods.securitycraft.network.packets.PacketSetBlockAndMetadata;
import net.geforcemods.securitycraft.network.packets.PacketSetBlockMetadata;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class EntitySecurityCamera extends Entity {

	private final float CAMERA_SPEED = SecurityCraft.config.cameraSpeed;

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

	public EntitySecurityCamera(World world){
		super(world);
		noClip = true;
		height = 1.0E-004F;
		width = 1.0E-004F;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id, EntityPlayer player){
		this(world);
		dataWatcher.updateObject(16, (int)x);
		dataWatcher.updateObject(17, (int)y);
		dataWatcher.updateObject(18, (int)z);
		cameraUseX = player.posX;
		cameraUseY = player.posY;
		cameraUseZ = player.posZ;
		cameraUseYaw = player.rotationYaw;
		cameraUsePitch = player.rotationPitch;
		this.id = id;
		playerViewingName = player.getCommandSenderName();
		setPosition(x + 0.5D, y, z + 0.5D);

		rotationPitch = 30.0F;

		int meta = worldObj.getBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ));

		if(meta == 4 || meta == 8)
			rotationYaw = 180.0F;
		else if(meta == 2 || meta == 6)
			rotationYaw = 90.0F;
		else if(meta == 3 || meta == 7)
			rotationYaw = 0.0F;
		else if(meta == 1 || meta == 5)
			rotationYaw = 270.0F;
		else if(meta == 0 || meta == 9)
			rotationPitch = 75;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id, EntitySecurityCamera camera){
		this(world);
		dataWatcher.updateObject(16, (int)x);
		dataWatcher.updateObject(17, (int)y);
		dataWatcher.updateObject(18, (int)z);
		cameraUseX = camera.cameraUseX;
		cameraUseY = camera.cameraUseY;
		cameraUseZ = camera.cameraUseZ;
		cameraUseYaw = camera.cameraUseYaw;
		cameraUsePitch = camera.cameraUsePitch;
		this.id = id;
		playerViewingName = camera.playerViewingName;
		setPosition(x + 0.5D, y, z + 0.5D);

		rotationPitch = 30.0F;

		int meta = worldObj.getBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ));

		if(meta == 4 || meta == 8)
			rotationYaw = 180.0F;
		else if(meta == 2 || meta == 6)
			rotationYaw = 90.0F;
		else if(meta == 3 || meta == 7)
			rotationYaw = 0.0F;
		else if(meta == 1 || meta == 5)
			rotationYaw = 270.0F;
		else if(meta == 0 || meta == 9)
			rotationPitch = 75;
	}

	@Override
	public double getMountedYOffset(){
		return height * -7500.0D;
	}

	@Override
	protected boolean shouldSetPosAfterLoading(){
		return false;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider){
		return false;
	}

	@Override
	public void onUpdate() {
		if(worldObj.isRemote && riddenByEntity != null){

			if(screenshotCooldown > 0)
				screenshotCooldown -= 1;

			if(redstoneCooldown > 0)
				redstoneCooldown -= 1;

			if(toggleNightVisionCooldown > 0)
				toggleNightVisionCooldown -= 1;

			if(toggleLightCooldown > 0)
				toggleLightCooldown -= 1;

			if((id == 0) && (((EntityPlayer)riddenByEntity).getCurrentEquippedItem() != null) && (((EntityPlayer)riddenByEntity).getCurrentEquippedItem().getItem() == SCContent.cameraMonitor))
				id = ((ItemCameraMonitor)((EntityPlayer)riddenByEntity).getCurrentEquippedItem().getItem()).getSlotFromPosition(((EntityPlayer)riddenByEntity).getCurrentEquippedItem().stackTagCompound, new CameraView((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ), riddenByEntity.dimension));

			if(((EntityPlayer)riddenByEntity).rotationYaw != rotationYaw)
				((EntityPlayer)riddenByEntity).rotationYaw = rotationYaw;

			if(((EntityPlayer)riddenByEntity).rotationPitch != rotationPitch)
				((EntityPlayer)riddenByEntity).rotationPitch = rotationPitch;

			checkKeysPressed();

			if((Mouse.hasWheel()) && (Mouse.isButtonDown(2)) && (screenshotCooldown == 0)){
				screenshotCooldown = 30;
				ClientUtils.takeScreenshot();
				Minecraft.getMinecraft().theWorld.playSound(posX, posY, posZ, SCSounds.CAMERASNAP.path, 1.0F, 1.0F, true);
			}

			if((riddenByEntity != null) && (shouldProvideNightVision))
				SecurityCraft.network.sendToServer(new PacketGivePotionEffect(Potion.nightVision.id, 3, -1));

		}

		if(!worldObj.isRemote)
			if(riddenByEntity == null | worldObj.getBlock(dataWatcher.getWatchableObjectInt(16), dataWatcher.getWatchableObjectInt(17), dataWatcher.getWatchableObjectInt(18)) != SCContent.securityCamera){
				setDead();
				return;
			}
	}

	@SideOnly(Side.CLIENT)
	private void checkKeysPressed(){
		if(Minecraft.getMinecraft().gameSettings.keyBindForward.getIsKeyPressed())
			moveViewUp();

		if(Minecraft.getMinecraft().gameSettings.keyBindBack.getIsKeyPressed())
			moveViewDown();

		if(Minecraft.getMinecraft().gameSettings.keyBindLeft.getIsKeyPressed())
			moveViewLeft();

		if(Minecraft.getMinecraft().gameSettings.keyBindRight.getIsKeyPressed())
			moveViewRight();

		if(worldObj.getTileEntity((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ)) instanceof CustomizableSCTE)
			if(KeyBindings.cameraEmitRedstone.getIsKeyPressed() && worldObj.getTileEntity((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ)) != null && ((CustomizableSCTE)worldObj.getTileEntity((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ))).hasModule(EnumCustomModules.REDSTONE) && redstoneCooldown == 0){
				int meta = worldObj.getBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ));
				setRedstonePower(meta);
				redstoneCooldown = 30;
			}

		if(KeyBindings.cameraActivateNightVision.getIsKeyPressed() && toggleNightVisionCooldown == 0)
			enableNightVision();

		if(KeyBindings.cameraZoomIn.isPressed())
			zoomCameraView(-1);

		if(KeyBindings.cameraZoomOut.isPressed())
			zoomCameraView(1);
	}

	public void moveViewUp(){
		if(isCameraDown())
		{
			if(rotationPitch > 40F)
				setRotation(rotationYaw, rotationPitch -= CAMERA_SPEED);
		}
		else if(rotationPitch > -25.0F)
			setRotation(rotationYaw, rotationPitch -= CAMERA_SPEED);

		updateServerRotation();
	}

	public void moveViewDown(){
		if(isCameraDown())
		{
			if(rotationPitch < 100F)
				setRotation(rotationYaw, rotationPitch += CAMERA_SPEED);
		}
		else if(rotationPitch < 60.0F)
			setRotation(rotationYaw, rotationPitch += CAMERA_SPEED);

		updateServerRotation();
	}

	public void moveViewLeft(){
		int meta = worldObj.getBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ));

		if(meta == 4 || meta == 8)
		{
			if(rotationYaw < 0)
			{
				if(rotationYaw - CAMERA_SPEED < 180F)
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(rotationYaw - CAMERA_SPEED > 90F)
				setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
		}
		else if(meta == 2 || meta == 6)
		{
			if((rotationYaw - CAMERA_SPEED) > 0.0F)
				setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
		}
		else if(meta == 3 || meta == 7)
		{
			if((rotationYaw - CAMERA_SPEED) > -90.0F)
				setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
		}
		else if(meta == 1 || meta == 5)
		{
			if((rotationYaw - CAMERA_SPEED) > -180.0F)
				setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
		}
		else if(meta == 0 || meta == 9)
			setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);

		updateServerRotation();
	}

	public void moveViewRight(){
		int meta = worldObj.getBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ));

		if(meta == 4 || meta == 8)
		{
			if(rotationYaw < 0)
			{
				if((Math.abs(rotationYaw) + CAMERA_SPEED) > 90F)
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(rotationYaw + CAMERA_SPEED < 185F)
				setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
		}
		else if(meta == 2 || meta == 6)
		{
			if((rotationYaw + CAMERA_SPEED) < 180.0F)
				setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
		}
		else if(meta == 3 || meta == 7)
		{
			if((rotationYaw + CAMERA_SPEED) < 90.0F)
				setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
		}
		else if(meta == 1 || meta == 5)
		{
			if((rotationYaw + CAMERA_SPEED) < 0.0F)
				setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
		}
		else if(meta == 0 || meta == 9)
			setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);

		updateServerRotation();
	}

	public void zoomCameraView(int zoom) {
		if(zoom > 0){
			if(zoomAmount == -0.5F){
				zoomAmount = 1F;
				Minecraft.getMinecraft().theWorld.playSound(posX,posY,posZ, SCSounds.CAMERAZOOMIN.path, 1.0F, 1.0F, true);
			}else if(zoomAmount == 1F){
				zoomAmount = 2F;
				Minecraft.getMinecraft().theWorld.playSound(posX,posY,posZ, SCSounds.CAMERAZOOMIN.path, 1.0F, 1.0F, true);
			}
		}else if(zoom < 0)
			if(zoomAmount == 2F){
				zoomAmount = 1F;
				Minecraft.getMinecraft().theWorld.playSound(posX,posY,posZ, SCSounds.CAMERAZOOMIN.path, 1.0F, 1.0F, true);
			}else if(zoomAmount == 1F){
				zoomAmount = -0.5F;
				Minecraft.getMinecraft().theWorld.playSound(posX,posY,posZ, SCSounds.CAMERAZOOMIN.path, 1.0F, 1.0F, true);
			}
	}

	public void setRedstonePower(int meta) {
		if(meta == 0)
			SecurityCraft.network.sendToServer(new PacketSetBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ), 9, true, 1, "", ""));
		else if(meta == 5 || meta == 6 || meta == 7 || meta == 8)
			SecurityCraft.network.sendToServer(new PacketSetBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ), worldObj.getBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ)) - 4, true, 1, "", ""));
		else if(meta == 1 || meta == 2 || meta == 3 || meta == 4)
			SecurityCraft.network.sendToServer(new PacketSetBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ), worldObj.getBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ)) + 4, true, 1, "", ""));
		else if(meta == 9)
			SecurityCraft.network.sendToServer(new PacketSetBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ), 0, true, 1, "", ""));
	}

	public void enableNightVision(){
		toggleNightVisionCooldown = 30;
		shouldProvideNightVision = !shouldProvideNightVision;
	}

	public void emitLight() {
		Block block = worldObj.getBlock((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ));

		if(block instanceof BlockSecurityCamera){
			toggleLightCooldown = 30;

			int meta = worldObj.getBlockMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ));
			ItemStack[] modules = null;

			if(!((CustomizableSCTE)worldObj.getTileEntity((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ))).getModules().isEmpty())
				modules = ((CustomizableSCTE)worldObj.getTileEntity((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ))).itemStacks;

			if(block == SCContent.securityCamera){
				SecurityCraft.network.sendToServer(new PacketSetBlockAndMetadata((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ), "securitycraft:securityCameraLit", meta));
				SecurityCraft.network.sendToServer(new PacketSSetOwner((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ), ((IOwnable)worldObj.getTileEntity((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ))).getOwner().getUUID(), ((IOwnable)worldObj.getTileEntity((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ))).getOwner().getName()));

				if(modules != null)
					SecurityCraft.network.sendToServer(new PacketSAddModules((int)Math.floor(posX), (int)posY, (int)Math.floor(posZ), modules));
			}
		}
	}

	/**
	 * For debug purposes
	 */
	public String getCameraInfo(){
		String nowViewing = EnumChatFormatting.UNDERLINE + "Now viewing camera #" + id + "\n\n";
		String pos = EnumChatFormatting.YELLOW + "Pos: " + EnumChatFormatting.RESET + "X: " + (int)Math.floor(posX) + " Y: " + (int)posY + " Z: " + (int)Math.floor(posZ) + "\n";
		String viewingFrom = (riddenByEntity != null) ? EnumChatFormatting.YELLOW + "Viewing from: " + EnumChatFormatting.RESET + " X: " + (int)Math.floor(cameraUseX) + " Y: " + (int)Math.floor(cameraUseY) + " Z: " + (int)Math.floor(cameraUseZ) : "";
		return nowViewing + pos + viewingFrom;
	}

	@SideOnly(Side.CLIENT)
	private void updateServerRotation(){
		SecurityCraft.network.sendToServer(new PacketSSetCameraRotation(rotationYaw, rotationPitch));
	}

	private boolean isCameraDown()
	{
		int blockPosX = dataWatcher.getWatchableObjectInt(16);
		int blockPosY = dataWatcher.getWatchableObjectInt(17);
		int blockPosZ = dataWatcher.getWatchableObjectInt(18);

		return worldObj.getBlockMetadata(blockPosX, blockPosY, blockPosZ) == 0 || worldObj.getBlockMetadata(blockPosX, blockPosY, blockPosZ) == 9;
	}

	public float getZoomAmount(){
		return zoomAmount;
	}

	@Override
	public void setDead(){
		super.setDead();

		if(playerViewingName != null && PlayerUtils.isPlayerOnline(playerViewingName)){
			EntityPlayer player = PlayerUtils.getPlayerFromName(playerViewingName);
			player.setPositionAndUpdate(cameraUseX, cameraUseY, cameraUseZ);
			SecurityCraft.network.sendTo(new PacketCSetPlayerPositionAndRotation(cameraUseX, cameraUseY, cameraUseZ, cameraUseYaw, cameraUsePitch), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(16, 0); //blockPosX
		dataWatcher.addObject(17, -1); //blockPosY
		dataWatcher.addObject(18, 0); //blockPosZ
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag){
		tag.setInteger("CameraID", id);

		if(playerViewingName != null)
			tag.setString("playerName", playerViewingName);

		if(cameraUseX != 0.0D)
			tag.setDouble("cameraUseX", cameraUseX);

		if(cameraUseY != 0.0D)
			tag.setDouble("cameraUseY", cameraUseY);

		if(cameraUseZ != 0.0D)
			tag.setDouble("cameraUseZ", cameraUseZ);

		if(cameraUseYaw != 0.0D)
			tag.setDouble("cameraUseYaw", cameraUseYaw);

		if(cameraUsePitch != 0.0D)
			tag.setDouble("cameraUsePitch", cameraUsePitch);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag){
		id = tag.getInteger("CameraID");

		if(tag.hasKey("playerName"))
			playerViewingName = tag.getString("playerName");

		if(tag.hasKey("cameraUseX"))
			cameraUseX = tag.getDouble("cameraUseX");

		if(tag.hasKey("cameraUseY"))
			cameraUseY = tag.getDouble("cameraUseY");

		if(tag.hasKey("cameraUseZ"))
			cameraUseZ = tag.getDouble("cameraUseZ");

		if(tag.hasKey("cameraUseYaw"))
			cameraUseYaw = tag.getFloat("cameraUseYaw");

		if(tag.hasKey("cameraUsePitch"))
			cameraUsePitch = tag.getFloat("cameraUsePitch");
	}

}