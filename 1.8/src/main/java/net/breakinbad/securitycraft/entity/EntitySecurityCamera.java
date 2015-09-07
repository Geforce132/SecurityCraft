package net.breakinbad.securitycraft.entity;

import org.lwjgl.input.Mouse;

import net.breakinbad.securitycraft.api.CustomizableSCTE;
import net.breakinbad.securitycraft.api.IOwnable;
import net.breakinbad.securitycraft.blocks.BlockSecurityCamera;
import net.breakinbad.securitycraft.main.Utils;
import net.breakinbad.securitycraft.main.Utils.BlockUtils;
import net.breakinbad.securitycraft.main.Utils.ClientUtils;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.misc.KeyBindings;
import net.breakinbad.securitycraft.misc.SCSounds;
import net.breakinbad.securitycraft.network.packets.PacketGivePotionEffect;
import net.breakinbad.securitycraft.network.packets.PacketSAddModules;
import net.breakinbad.securitycraft.network.packets.PacketSSetCameraRotation;
import net.breakinbad.securitycraft.network.packets.PacketSSetOwner;
import net.breakinbad.securitycraft.network.packets.PacketSetBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySecurityCamera extends Entity{
	
	private final float CAMERA_SPEED = 2F;
	
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
			
		EnumFacing facing = BlockUtils.getBlockPropertyAsEnum(worldObj, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);
		
		if(facing == EnumFacing.NORTH){
			this.rotationYaw = 180F;
		}else if(facing == EnumFacing.WEST){
			this.rotationYaw = 90F;
		}else if(facing == EnumFacing.SOUTH){
			this.rotationYaw = 0F;
		}else if(facing == EnumFacing.EAST){
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
			
			if(((EntityPlayer) this.riddenByEntity).rotationYaw != this.rotationYaw){
				((EntityPlayer) this.riddenByEntity).setPositionAndRotation(this.riddenByEntity.posX, this.riddenByEntity.posY, this.riddenByEntity.posZ, this.rotationYaw, this.rotationPitch);
				((EntityPlayer) this.riddenByEntity).rotationYaw = this.rotationYaw;
			}
			
			if(((EntityPlayer) this.riddenByEntity).rotationPitch != this.rotationPitch){
				((EntityPlayer) this.riddenByEntity).setPositionAndRotation(this.riddenByEntity.posX, this.riddenByEntity.posY, this.riddenByEntity.posZ, this.rotationYaw, this.rotationPitch);
			}
		
			this.checkKeysPressed();

			if(Mouse.hasWheel() && Mouse.isButtonDown(2) && this.screenshotCooldown == 0){	
				this.screenshotCooldown = 30;
				ClientUtils.takeScreenshot();
				Minecraft.getMinecraft().theWorld.playSound(posX, posY, posZ, SCSounds.CAMERASNAP.path, 1.0F, 1.0F, true);
			}
					
			if(this.riddenByEntity != null && this.shouldProvideNightVision){
				mod_SecurityCraft.network.sendToServer(new PacketGivePotionEffect(Potion.nightVision.id, 3, -1));
			}
		}
		
		if(!this.worldObj.isRemote){
			if(this.riddenByEntity == null | BlockUtils.isAirBlock(worldObj, blockPosX, blockPosY, blockPosZ)){
				this.setDead();
				return;
			}	
		}
	}

	@SideOnly(Side.CLIENT)
	private void checkKeysPressed() {
		if(Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()){
			this.moveViewUp();
		}
		
		if(Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()){
			this.moveViewDown();
		}
		
		if(Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()){
			this.moveViewLeft();
		}
		
		if(Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()){
			this.moveViewRight();
		}
		
		if(KeyBindings.cameraEmitRedstone.isPressed() && this.redstoneCooldown == 0){
			this.setRedstonePower();
			this.redstoneCooldown = 30;
		}
		
		if(KeyBindings.cameraActivateNightVision.isPressed() && this.toggleNightVisionCooldown == 0){
			this.enableNightVision();
		}
		
		if(KeyBindings.cameraEmitLight.isPressed() && this.toggleLightCooldown == 0){
			this.emitLight();
		}
				
		if(KeyBindings.cameraZoomIn.isKeyDown()){
			this.zoomCameraView(1);
		}
		
		if(KeyBindings.cameraZoomOut.isKeyDown()){
			this.zoomCameraView(-1);
		}	
		
	}

	public void moveViewUp() {
		if(this.rotationPitch > -25F){
			this.setRotation(this.rotationYaw, this.rotationPitch -= CAMERA_SPEED);
		}
		
		this.updateServerRotation();
	}
	
	public void moveViewDown(){
		if(this.rotationPitch < 60F){
			this.setRotation(this.rotationYaw, this.rotationPitch += CAMERA_SPEED);
		}
		
		this.updateServerRotation();
	}
	
	public void moveViewLeft() {
		EnumFacing facing = BlockUtils.getBlockPropertyAsEnum(worldObj, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

		if(facing == EnumFacing.EAST){
			if((this.rotationYaw - CAMERA_SPEED) > -180F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == EnumFacing.WEST){
			if((this.rotationYaw - CAMERA_SPEED) > 0F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == EnumFacing.NORTH){
			if((this.rotationYaw - CAMERA_SPEED) > -270F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == EnumFacing.SOUTH){
			if((this.rotationYaw - CAMERA_SPEED) > -90F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}
		
		this.updateServerRotation();
	}
	
	public void moveViewRight(){
		EnumFacing facing = BlockUtils.getBlockPropertyAsEnum(worldObj, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

		if(facing == EnumFacing.EAST){
			if((this.rotationYaw + CAMERA_SPEED) < 0F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == EnumFacing.WEST){
			if((this.rotationYaw + CAMERA_SPEED) < 180F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == EnumFacing.NORTH){
			if((this.rotationYaw + CAMERA_SPEED) < -90F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == EnumFacing.SOUTH){
			if((this.rotationYaw + CAMERA_SPEED) < 90F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}
		
		this.updateServerRotation();
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
	
	public void setRedstonePower() {
		BlockPos pos = BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));

		System.out.println("Setting power to " + Utils.toggleBoolean(BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockSecurityCamera.POWERED)));
		if(BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockSecurityCamera.POWERED)){
			mod_SecurityCraft.network.sendToServer(new PacketSetBlock(pos.getX(), pos.getY(), pos.getZ(), "securitycraft:securityCamera", BlockUtils.getBlockMeta(worldObj, pos) - 6));
		}else if(!BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockSecurityCamera.POWERED)){
			mod_SecurityCraft.network.sendToServer(new PacketSetBlock(pos.getX(), pos.getY(), pos.getZ(), "securitycraft:securityCamera", BlockUtils.getBlockMeta(worldObj, pos) + 6));
		}
	}
	
	public void enableNightVision() {
		this.toggleNightVisionCooldown = 30;
		this.shouldProvideNightVision = Utils.toggleBoolean(shouldProvideNightVision);		
	}
	
	public void emitLight() {
		Block block = BlockUtils.getBlock(worldObj, (int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));
		BlockPos pos = BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));
		
		if(block instanceof BlockSecurityCamera){
			this.toggleLightCooldown = 30;
			
			int meta = BlockUtils.getBlockMeta(worldObj, pos);
			ItemStack[] modules = null;
			
			if(!((CustomizableSCTE) this.worldObj.getTileEntity(pos)).getModules().isEmpty()){
				modules = (ItemStack[]) ((CustomizableSCTE) this.worldObj.getTileEntity(pos)).itemStacks;
			}
			
			if(block == mod_SecurityCraft.securityCamera){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlock(pos.getX(), pos.getY(), pos.getZ(), "securitycraft:securityCameraLit", meta));
				mod_SecurityCraft.network.sendToServer(new PacketSSetOwner(pos.getX(), pos.getY(), pos.getZ(), ((IOwnable) this.worldObj.getTileEntity(pos)).getOwnerUUID(), ((IOwnable) this.worldObj.getTileEntity(pos)).getOwnerName()));
				
				if(modules != null){
					mod_SecurityCraft.network.sendToServer(new PacketSAddModules(pos.getX(), pos.getY(), pos.getZ(), modules));
				}
			}else if(block == mod_SecurityCraft.securityCameraLit){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlock(pos.getX(), pos.getY(), pos.getZ(), "securitycraft:securityCamera", meta));
				mod_SecurityCraft.network.sendToServer(new PacketSSetOwner(pos.getX(), pos.getY(), pos.getZ(), ((IOwnable) this.worldObj.getTileEntity(pos)).getOwnerUUID(), ((IOwnable) this.worldObj.getTileEntity(pos)).getOwnerName()));
				
				if(modules != null){
					mod_SecurityCraft.network.sendToServer(new PacketSAddModules(pos.getX(), pos.getY(), pos.getZ(), modules));
				}
			}
		}
	}
	
	public String getCameraInfo(){
		String nowViewing = EnumChatFormatting.UNDERLINE + "Now viewing camera #" + id + "\n\n";
		String pos = EnumChatFormatting.YELLOW + "Pos: " + EnumChatFormatting.RESET + "X: " + (int) Math.floor(posX) + " Y: " + (int) (posY - 1D) + " Z: " + (int) Math.floor(posZ) + "\n";
		String viewingFrom = (this.riddenByEntity != null && mod_SecurityCraft.instance.hasUsePosition(this.riddenByEntity.getName())) ? EnumChatFormatting.YELLOW + "Viewing from: " + EnumChatFormatting.RESET + " X: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getName())[0]) + " Y: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getName())[1]) + " Z: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getName())[2]) : "";
		return nowViewing + pos + viewingFrom;
	}
	
	@SideOnly(Side.CLIENT)
	private void updateServerRotation(){
		mod_SecurityCraft.network.sendToServer(new PacketSSetCameraRotation(this.rotationYaw, this.rotationPitch));
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
