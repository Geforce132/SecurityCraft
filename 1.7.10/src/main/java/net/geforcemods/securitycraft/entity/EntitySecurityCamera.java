package net.geforcemods.securitycraft.entity;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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

	private final float CAMERA_SPEED = mod_SecurityCraft.configHandler.cameraSpeed;

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

	public EntitySecurityCamera(World world){
		super(world);
		this.noClip = true;
		this.height = 1.0E-004F;
		this.width = 1.0E-004F;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id, EntityPlayer player){
		this(world);
		this.blockPosX = (int) x;
		this.blockPosY = (int) y;
		this.blockPosZ = (int) z;
		this.cameraUseX = player.posX;
		this.cameraUseY = player.posY;
		this.cameraUseZ = player.posZ;
		this.cameraUseYaw = player.rotationYaw;
		this.cameraUsePitch = player.rotationPitch;
		this.id = id;
		this.playerViewingName = player.getCommandSenderName();
		setPosition(x + 0.5D, y + 1.0D, z + 0.5D);

		this.rotationPitch = 30.0F;

		int meta = this.worldObj.getBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ));

		if(meta == 4 || meta == 8){ 
			this.rotationYaw = 180.0F;
		}else if(meta == 2 || meta == 6){
			this.rotationYaw = 90.0F;
		}else if(meta == 3 || meta == 7){
			this.rotationYaw = 0.0F;
		}else if(meta == 1 || meta == 5){ 
			this.rotationYaw = 270.0F;
		}
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id, EntitySecurityCamera camera){
		this(world);
		this.blockPosX = (int) x;
		this.blockPosY = (int) y;
		this.blockPosZ = (int) z;
		this.cameraUseX = camera.cameraUseX;
		this.cameraUseY = camera.cameraUseY;
		this.cameraUseZ = camera.cameraUseZ;
		this.cameraUseYaw = camera.cameraUseYaw;
		this.cameraUsePitch = camera.cameraUsePitch;
		this.id = id;
		this.playerViewingName = camera.playerViewingName;
		setPosition(x + 0.5D, y + 1.0D, z + 0.5D);

		this.rotationPitch = 30.0F;

		int meta = this.worldObj.getBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ));

		if(meta == 4 || meta == 8){ 
			this.rotationYaw = 180.0F;
		}else if(meta == 2 || meta == 6){
			this.rotationYaw = 90.0F;
		}else if(meta == 3 || meta == 7){
			this.rotationYaw = 0.0F;
		}else if(meta == 1 || meta == 5){ 
			this.rotationYaw = 270.0F;
		}
	}

	public double getMountedYOffset(){
		return this.height * -7500.0D;
	}

	protected boolean shouldSetPosAfterLoading(){
		return false;
	}

	public boolean shouldDismountInWater(Entity rider){
		return false;
	}

	public void onUpdate() {		
		if(this.worldObj.isRemote && this.riddenByEntity != null){

			if(this.screenshotCooldown > 0){
				this.screenshotCooldown -= 1;
			}

			if(this.redstoneCooldown > 0){
				this.redstoneCooldown -= 1;
			}

			if(this.toggleNightVisionCooldown > 0){
				this.toggleNightVisionCooldown -= 1;
			}

			if(this.toggleLightCooldown > 0){
				this.toggleLightCooldown -= 1;
			}

			if((this.id == 0) && (((EntityPlayer)this.riddenByEntity).getCurrentEquippedItem() != null) && (((EntityPlayer)this.riddenByEntity).getCurrentEquippedItem().getItem() == mod_SecurityCraft.cameraMonitor)){
				this.id = ((ItemCameraMonitor)((EntityPlayer)this.riddenByEntity).getCurrentEquippedItem().getItem()).getSlotFromPosition(((EntityPlayer)this.riddenByEntity).getCurrentEquippedItem().stackTagCompound, new CameraView((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ), riddenByEntity.dimension));
			}

			if(((EntityPlayer)this.riddenByEntity).rotationYaw != this.rotationYaw){
				((EntityPlayer)this.riddenByEntity).rotationYaw = this.rotationYaw;
			}

			if(((EntityPlayer)this.riddenByEntity).rotationPitch != this.rotationPitch){
				((EntityPlayer)this.riddenByEntity).rotationPitch = this.rotationPitch;
			}

			this.checkKeysPressed();

			if((Mouse.hasWheel()) && (Mouse.isButtonDown(2)) && (this.screenshotCooldown == 0)){
				this.screenshotCooldown = 30;
				ClientUtils.takeScreenshot();
				Minecraft.getMinecraft().theWorld.playSound(this.posX, this.posY, this.posZ, SCSounds.CAMERASNAP.path, 1.0F, 1.0F, true);
			}

			if((this.riddenByEntity != null) && (this.shouldProvideNightVision)){
				mod_SecurityCraft.network.sendToServer(new PacketGivePotionEffect(Potion.nightVision.id, 3, -1));
			}

		}

		if(!this.worldObj.isRemote){
			if(this.riddenByEntity == null | this.worldObj.getBlock(this.blockPosX, this.blockPosY, this.blockPosZ) != mod_SecurityCraft.securityCamera){
				setDead();
				return;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void checkKeysPressed(){
		if(Minecraft.getMinecraft().gameSettings.keyBindForward.getIsKeyPressed()){
			moveViewUp();
		}

		if(Minecraft.getMinecraft().gameSettings.keyBindBack.getIsKeyPressed()){
			moveViewDown();
		}

		if(Minecraft.getMinecraft().gameSettings.keyBindLeft.getIsKeyPressed()){
			moveViewLeft();
		}

		if(Minecraft.getMinecraft().gameSettings.keyBindRight.getIsKeyPressed()){
			moveViewRight();
		}

		if(this.worldObj.getTileEntity((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ)) instanceof CustomizableSCTE)
		{
			if(KeyBindings.cameraEmitRedstone.getIsKeyPressed() && this.worldObj.getTileEntity((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ)) != null && ((CustomizableSCTE)this.worldObj.getTileEntity((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ))).hasModule(EnumCustomModules.REDSTONE) && this.redstoneCooldown == 0){
				int meta = this.worldObj.getBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ));
				setRedstonePower(meta);
				this.redstoneCooldown = 30;
			}
		}

		if(KeyBindings.cameraActivateNightVision.getIsKeyPressed() && this.toggleNightVisionCooldown == 0){
			enableNightVision();
		}

		if(KeyBindings.cameraZoomIn.isPressed()){
			zoomCameraView(-1);
		}

		if(KeyBindings.cameraZoomOut.isPressed()){
			zoomCameraView(1);
		}
	}

	public void moveViewUp(){
		if(this.rotationPitch > -25.0F){
			setRotation(this.rotationYaw, this.rotationPitch -= CAMERA_SPEED);
		}

		this.updateServerRotation();
	}

	public void moveViewDown(){
		if(this.rotationPitch < 60.0F){
			setRotation(this.rotationYaw, this.rotationPitch += CAMERA_SPEED);
		}

		this.updateServerRotation();
	}

	public void moveViewLeft(){
		int meta = this.worldObj.getBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ));

		if(meta == 4 || meta == 8){
			if(this.rotationYaw < 0){
				if(this.rotationYaw - CAMERA_SPEED < 180F){
					setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
				}
			}else{
				if(this.rotationYaw - CAMERA_SPEED > 90F){
					setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
				}
			}
		}else if(meta == 2 || meta == 6){
			if((this.rotationYaw - CAMERA_SPEED) > 0.0F){
				setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 3 || meta == 7){
			if((this.rotationYaw - CAMERA_SPEED) > -90.0F){
				setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 1 || meta == 5){
			if((this.rotationYaw - CAMERA_SPEED) > -180.0F){
				setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}

		this.updateServerRotation();
	}

	public void moveViewRight(){
		int meta = this.worldObj.getBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ));

		if(meta == 4 || meta == 8){
			if(this.rotationYaw < 0){
				if((Math.abs(this.rotationYaw) + CAMERA_SPEED) > 90F){
					setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
				}
			}else{
				if(this.rotationYaw + CAMERA_SPEED < 185F){
					setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
				}
			}
		}else if(meta == 2 || meta == 6){
			if((this.rotationYaw + CAMERA_SPEED) < 180.0F){
				setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 3 || meta == 7){
			if((this.rotationYaw + CAMERA_SPEED) < 90.0F){
				setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 1 || meta == 5){
			if((this.rotationYaw + CAMERA_SPEED) < 0.0F){
				setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}

		this.updateServerRotation();
	}

	public void zoomCameraView(int zoom) {
		if(zoom > 0){
			if(zoomAmount == -0.5F){
				zoomAmount = 1F;
				Minecraft.getMinecraft().theWorld.playSound(this.posX,this.posY,this.posZ, SCSounds.CAMERAZOOMIN.path, 1.0F, 1.0F, true);	
			}else if(zoomAmount == 1F){
				zoomAmount = 2F;
				Minecraft.getMinecraft().theWorld.playSound(this.posX,this.posY,this.posZ, SCSounds.CAMERAZOOMIN.path, 1.0F, 1.0F, true);	
			}
		}else if(zoom < 0){
			if(zoomAmount == 2F){
				zoomAmount = 1F;
				Minecraft.getMinecraft().theWorld.playSound(this.posX,this.posY,this.posZ, SCSounds.CAMERAZOOMIN.path, 1.0F, 1.0F, true);	
			}else if(zoomAmount == 1F){
				zoomAmount = -0.5F;
				Minecraft.getMinecraft().theWorld.playSound(this.posX,this.posY,this.posZ, SCSounds.CAMERAZOOMIN.path, 1.0F, 1.0F, true);	
			}
		}
	}

	public void setRedstonePower(int meta) {
		if(meta == 5 || meta == 6 || meta == 7 || meta == 8){
			mod_SecurityCraft.network.sendToServer(new PacketSetBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ), this.worldObj.getBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ)) - 4, true, 1, "", ""));
		}else if(meta == 1 || meta == 2 || meta == 3 || meta == 4){
			mod_SecurityCraft.network.sendToServer(new PacketSetBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ), this.worldObj.getBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ)) + 4, true, 1, "", ""));
		}
	}

	public void enableNightVision(){
		this.toggleNightVisionCooldown = 30;
		this.shouldProvideNightVision = !this.shouldProvideNightVision;
	}

	public void emitLight() {
		Block block = this.worldObj.getBlock((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ));

		if(block instanceof BlockSecurityCamera){
			this.toggleLightCooldown = 30;

			int meta = this.worldObj.getBlockMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ));
			ItemStack[] modules = null;

			if(!((CustomizableSCTE)this.worldObj.getTileEntity((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ))).getModules().isEmpty()){
				modules = ((CustomizableSCTE)this.worldObj.getTileEntity((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ))).itemStacks;
			}

			if(block == mod_SecurityCraft.securityCamera){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlockAndMetadata((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ), "securitycraft:securityCameraLit", meta));
				mod_SecurityCraft.network.sendToServer(new PacketSSetOwner((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ), ((IOwnable)this.worldObj.getTileEntity((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ))).getOwner().getUUID(), ((IOwnable)this.worldObj.getTileEntity((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ))).getOwner().getName()));

				if(modules != null){
					mod_SecurityCraft.network.sendToServer(new PacketSAddModules((int)Math.floor(this.posX), (int)(this.posY - 1.0D), (int)Math.floor(this.posZ), modules));
				}
			}
		}
	}

	public String getCameraInfo(){
		String nowViewing = EnumChatFormatting.UNDERLINE + "Now viewing camera #" + this.id + "\n\n";
		String pos = EnumChatFormatting.YELLOW + "Pos: " + EnumChatFormatting.RESET + "X: " + (int)Math.floor(this.posX) + " Y: " + (int)(this.posY - 1.0D) + " Z: " + (int)Math.floor(this.posZ) + "\n";
		String viewingFrom = (this.riddenByEntity != null) ? EnumChatFormatting.YELLOW + "Viewing from: " + EnumChatFormatting.RESET + " X: " + (int)Math.floor(cameraUseX) + " Y: " + (int)Math.floor(cameraUseY) + " Z: " + (int)Math.floor(cameraUseZ) : "";
		return nowViewing + pos + viewingFrom;
	}

	@SideOnly(Side.CLIENT)
	private void updateServerRotation(){
		mod_SecurityCraft.network.sendToServer(new PacketSSetCameraRotation(this.rotationYaw, this.rotationPitch));
	}

	public float getZoomAmount(){
		return zoomAmount;
	}

	public void setDead(){
		super.setDead();

		if(playerViewingName != null && PlayerUtils.isPlayerOnline(playerViewingName)){
			EntityPlayer player = PlayerUtils.getPlayerFromName(playerViewingName);
			player.setPositionAndUpdate(cameraUseX, cameraUseY, cameraUseZ);
			mod_SecurityCraft.network.sendTo(new PacketCSetPlayerPositionAndRotation(cameraUseX, cameraUseY, cameraUseZ, cameraUseYaw, cameraUsePitch), (EntityPlayerMP) player);
		}
	}

	protected void entityInit() {}

	public void writeEntityToNBT(NBTTagCompound tagCompound){
		tagCompound.setInteger("CameraID", id);

		if(playerViewingName != null){
			tagCompound.setString("playerName", playerViewingName);
		}

		if(cameraUseX != 0.0D){
			tagCompound.setDouble("cameraUseX", cameraUseX);
		}

		if(cameraUseY != 0.0D){
			tagCompound.setDouble("cameraUseY", cameraUseY);
		}

		if(cameraUseZ != 0.0D){
			tagCompound.setDouble("cameraUseZ", cameraUseZ);
		}

		if(cameraUseYaw != 0.0D){
			tagCompound.setDouble("cameraUseYaw", cameraUseYaw);
		}

		if(cameraUsePitch != 0.0D){
			tagCompound.setDouble("cameraUsePitch", cameraUsePitch);
		}
	}

	public void readEntityFromNBT(NBTTagCompound tagCompound){
		this.id = tagCompound.getInteger("CameraID");

		if(tagCompound.hasKey("playerName")){
			this.playerViewingName = tagCompound.getString("playerName");
		}

		if(tagCompound.hasKey("cameraUseX")){
			this.cameraUseX = tagCompound.getDouble("cameraUseX");
		}

		if(tagCompound.hasKey("cameraUseY")){
			this.cameraUseY = tagCompound.getDouble("cameraUseY");
		}

		if(tagCompound.hasKey("cameraUseZ")){
			this.cameraUseZ = tagCompound.getDouble("cameraUseZ");
		}

		if(tagCompound.hasKey("cameraUseYaw")){
			this.cameraUseYaw = tagCompound.getFloat("cameraUseYaw");
		}

		if(tagCompound.hasKey("cameraUsePitch")){
			this.cameraUsePitch = tagCompound.getFloat("cameraUsePitch");
		}
	}

}