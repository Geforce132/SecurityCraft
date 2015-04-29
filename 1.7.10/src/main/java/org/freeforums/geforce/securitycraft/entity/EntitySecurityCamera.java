package org.freeforums.geforce.securitycraft.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.interfaces.IOwnable;
import org.freeforums.geforce.securitycraft.items.ItemCameraMonitor;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.Utils.ClientUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.misc.KeyBindings;
import org.freeforums.geforce.securitycraft.misc.SCSounds;
import org.freeforums.geforce.securitycraft.network.packets.PacketGivePotionEffect;
import org.freeforums.geforce.securitycraft.network.packets.PacketSAddModules;
import org.freeforums.geforce.securitycraft.network.packets.PacketSSetOwner;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetBlockAndMetadata;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetBlockMetadata;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitySecurityCamera extends Entity{
	
	private final float CAMERA_SPEED = 1.75F;
	
	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;
	
	private int id;
	private int screenshotCooldown = 0;
	private int redstoneCooldown = 0;
	private int toggleNightVisionCooldown = 0;
	private int toggleLightCooldown = 0;
	private boolean shouldProvideNightVision = false;

	public EntitySecurityCamera(World world)
	{
		super(world);
		this.noClip = true;
		this.height = 0.0001F;
		this.width = 0.0001F;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id)
	{
		this(world);
		this.blockPosX = (int) x;
		this.blockPosY = (int) y;
		this.blockPosZ = (int) z;
		this.id = id;
		setPosition(x + 0.5D, y + 1, z + 0.5D);
		
		this.rotationPitch = 30F;
			
		int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));
		
		if(meta == 1 || meta == 5){
			this.rotationYaw = 180F;
		}else if(meta == 2 || meta == 6){
			this.rotationYaw = 90F;
		}else if(meta == 3 || meta == 7){
			this.rotationYaw = 0F;
		}else if(meta == 4 || meta == 8){
			this.rotationYaw = 270F;
		}
	}

	public double getMountedYOffset()
	{
		return this.height * -7500D;
	}

	protected boolean shouldSetPosAfterLoading()
	{
		return false;
	}
	
	public boolean shouldDismountInWater(Entity rider)
    {
        return false;
    }

	public void onUpdate(){		
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
			
			if(id == 0 && ((EntityPlayer) this.riddenByEntity).getCurrentEquippedItem() != null && ((EntityPlayer) this.riddenByEntity).getCurrentEquippedItem().getItem() == mod_SecurityCraft.cameraMonitor){
				id = ((ItemCameraMonitor) ((EntityPlayer) this.riddenByEntity).getCurrentEquippedItem().getItem()).getSlotFromPosition(((EntityPlayer) this.riddenByEntity).getCurrentEquippedItem().stackTagCompound, (int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));
			}
			
//			if(KeyBindings.cameraRotateUp.getIsKeyPressed()){
//				this.moveViewUp();
//			}
//			
//			if(KeyBindings.cameraRotateDown.getIsKeyPressed()){
//				this.moveViewDown();
//			}
//			
//			if(KeyBindings.cameraRotateLeft.getIsKeyPressed()){
//				this.moveViewLeft();
//			}
//			
//			if(KeyBindings.cameraRotateRight.getIsKeyPressed()){
//				this.moveViewRight();
//			}
//			
//			if(KeyBindings.cameraZoomIn.getIsKeyPressed()){
//				this.zoomCameraView(1);
//			}
//			
//			if(KeyBindings.cameraZoomOut.getIsKeyPressed()){
//				this.zoomCameraView(-1);
//			}
			this.checkKeysPressed();

			if(Mouse.hasWheel() && Mouse.isButtonDown(2) && this.screenshotCooldown == 0){	
				this.screenshotCooldown = 30;
				ClientUtils.takeScreenshot();
				Minecraft.getMinecraft().theWorld.playSound(posX, posY, posZ, SCSounds.CAMERASNAP.path, 1.0F, 1.0F, true);
			}
					
			if(((EntityPlayer) this.riddenByEntity).rotationYaw != this.rotationYaw){
				((EntityPlayer) this.riddenByEntity).rotationYaw = this.rotationYaw;
			}
			
			if(((EntityPlayer) this.riddenByEntity).rotationPitch != this.rotationPitch){
				((EntityPlayer) this.riddenByEntity).rotationPitch = this.rotationPitch;
			}
			
			if(this.riddenByEntity != null && this.shouldProvideNightVision){
				mod_SecurityCraft.network.sendToServer(new PacketGivePotionEffect(Potion.nightVision.id, 3, -1));
				//((EntityPlayer) this.riddenByEntity).addPotionEffect(new PotionEffect(Potion.nightVision.id, 3, -1, false));
			}
		}
		
		if(!this.worldObj.isRemote){
			if(this.riddenByEntity == null | this.worldObj.isAirBlock(blockPosX, blockPosY, blockPosZ)){
				this.setDead();
				return;
			}	
		}
	}

	@SideOnly(Side.CLIENT)
	private void checkKeysPressed() {
		if(Minecraft.getMinecraft().gameSettings.keyBindForward.getIsKeyPressed()){
			this.moveViewUp();
		}
		
		if(Minecraft.getMinecraft().gameSettings.keyBindBack.getIsKeyPressed()){
			this.moveViewDown();
		}
		
		if(Minecraft.getMinecraft().gameSettings.keyBindLeft.getIsKeyPressed()){
			this.moveViewLeft();
		}
		
		if(Minecraft.getMinecraft().gameSettings.keyBindRight.getIsKeyPressed()){
			this.moveViewRight();
		}
		
		if(KeyBindings.cameraEmitRedstone.getIsKeyPressed() && this.worldObj.getTileEntity((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)) != null && ((CustomizableSCTE) this.worldObj.getTileEntity((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ))).hasModule(EnumCustomModules.REDSTONE) && this.redstoneCooldown == 0){
			int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));
			this.setRedstonePower(meta);
			this.redstoneCooldown = 30;
		}
		
		if(KeyBindings.cameraActivateNightVision.getIsKeyPressed() && this.toggleNightVisionCooldown == 0){
			this.enableNightVision();
		}
		
		if(KeyBindings.cameraEmitLight.getIsKeyPressed() && this.toggleLightCooldown == 0){
			this.emitLight();
		}
		
		//int wheelMovement = (Mouse.getDWheel() / 120);
		
		if(KeyBindings.cameraZoomIn.getIsKeyPressed()){
			this.zoomCameraView(1);
		}
		
		if(KeyBindings.cameraZoomOut.getIsKeyPressed()){
			this.zoomCameraView(-1);
		}	
		
	}

	public void moveViewUp() {
		if(this.rotationPitch > -25F){
			this.setRotation(this.rotationYaw, this.rotationPitch -= CAMERA_SPEED);
		}
	}
	
	public void moveViewDown(){
		if(this.rotationPitch < 60F){
			this.setRotation(this.rotationYaw, this.rotationPitch += CAMERA_SPEED);
		}
	}
	
	public void moveViewLeft() {
		int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));

		if(meta == 1 || meta == 5){
			if(this.rotationYaw > -270F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 2 || meta == 6){
			if(this.rotationYaw > 0F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 3 || meta == 7){
			if(this.rotationYaw > -90F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 4 || meta == 8){
			if(this.rotationYaw > -180F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}
	}
	
	public void moveViewRight(){
		int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));

		if(meta == 1 || meta == 5){
			if(this.rotationYaw < -90F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 2 || meta == 6){
			if(this.rotationYaw < 180F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 3 || meta == 7){
			if(this.rotationYaw < 90F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(meta == 4 || meta == 8){
			if(this.rotationYaw < 0F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}
	}
	
	public void zoomCameraView(int mouseWheelMovement) {
		if(mouseWheelMovement > 0 && ClientUtils.getCameraZoom() < 8.0D){
			ClientUtils.setCameraZoom(0.1D);
			Minecraft.getMinecraft().theWorld.playSound((double) this.posX,(double) this.posY,(double) this.posZ, SCSounds.CAMERAZOOMIN.path, (float) 1.0F, 1.0F, true);
		}else if(mouseWheelMovement < 0 && ClientUtils.getCameraZoom() > 1.1D){
			ClientUtils.setCameraZoom(-0.1D);
			Minecraft.getMinecraft().theWorld.playSound((double) this.posX,(double) this.posY,(double) this.posZ, SCSounds.CAMERAZOOMIN.path, (float) 1.0F, 1.0F, true);
		}	
	}
	
	public void setRedstonePower(int meta) {
		if(meta == 5 || meta == 6 || meta == 7 || meta == 8){
			mod_SecurityCraft.network.sendToServer(new PacketSetBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ), this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)) - 4, true, 1, "", ""));
		}else if(meta == 1 || meta == 2 || meta == 3 || meta == 4){
			mod_SecurityCraft.network.sendToServer(new PacketSetBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ), this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)) + 4, true, 1, "", ""));
		}
	}
	
	public void enableNightVision() {
		this.toggleNightVisionCooldown = 30;
		this.shouldProvideNightVision = Utils.toggleBoolean(shouldProvideNightVision);		
	}
	
	public void emitLight() {
		Block block = this.worldObj.getBlock((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));

		if(block instanceof BlockSecurityCamera){
			this.toggleLightCooldown = 30;
			
			int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));
			ItemStack[] modules = null;
			
			if(!((CustomizableSCTE) this.worldObj.getTileEntity((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ))).getModules().isEmpty()){
				modules = (ItemStack[]) ((CustomizableSCTE) this.worldObj.getTileEntity((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ))).itemStacks;
			}
			
			if(block == mod_SecurityCraft.securityCamera){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlockAndMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ), "securitycraft:securityCameraLit", meta));
				mod_SecurityCraft.network.sendToServer(new PacketSSetOwner((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ), ((IOwnable) this.worldObj.getTileEntity((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ))).getOwnerUUID(), ((IOwnable) this.worldObj.getTileEntity((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ))).getOwnerName()));
				
				if(modules != null){
					mod_SecurityCraft.network.sendToServer(new PacketSAddModules((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ), modules));
				}
			}else if(block == mod_SecurityCraft.securityCameraLit){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlockAndMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ), "securitycraft:securityCamera", meta));
				mod_SecurityCraft.network.sendToServer(new PacketSSetOwner((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ), ((IOwnable) this.worldObj.getTileEntity((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ))).getOwnerUUID(), ((IOwnable) this.worldObj.getTileEntity((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ))).getOwnerName()));
				
				if(modules != null){
					mod_SecurityCraft.network.sendToServer(new PacketSAddModules((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ), modules));
				}
			}
		}
	}
	
	public String getCameraInfo(){
		String nowViewing = EnumChatFormatting.UNDERLINE + "Now viewing camera #" + id + "\n\n";
		String pos = EnumChatFormatting.YELLOW + "Pos: " + EnumChatFormatting.RESET + "X: " + (int) Math.floor(posX) + " Y: " + (int) (posY - 1D) + " Z: " + (int) Math.floor(posZ) + "\n";
		String viewingFrom = (this.riddenByEntity != null && mod_SecurityCraft.instance.hasUsePosition(this.riddenByEntity.getCommandSenderName())) ? EnumChatFormatting.YELLOW + "Viewing from: " + EnumChatFormatting.RESET + " X: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getCommandSenderName())[0]) + " Y: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getCommandSenderName())[1]) + " Z: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getCommandSenderName())[2]) : "";
		return nowViewing + pos + viewingFrom;
	}
	
    public void setDead(){
        super.setDead();
        
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
        	ClientUtils.setCameraZoom(0.0D);
        }
    }
	
	protected void entityInit(){}

	public void writeEntityToNBT(NBTTagCompound tagCompound){
		tagCompound.setInteger("CameraID", id);
	}

    public void readEntityFromNBT(NBTTagCompound tagCompound){
    	this.id = tagCompound.getInteger("CameraID");
    }

}
