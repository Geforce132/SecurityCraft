package net.geforcemods.securitycraft.entity;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.main.Utils;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.ClientUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCSetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.packets.PacketGivePotionEffect;
import net.geforcemods.securitycraft.network.packets.PacketSSetCameraRotation;
import net.geforcemods.securitycraft.network.packets.PacketSetBlock;
import net.minecraft.block.BlockLever;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySecurityCamera extends Entity{
	
	private final float CAMERA_SPEED = 2F;
	
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
		this.height = 0.0001F;
		this.width = 0.0001F;
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
		this.playerViewingName = player.getName();
		setPosition(x + 0.5D, y + 1, z + 0.5D);
		
		this.rotationPitch = 30F;
			
		BlockLever.EnumOrientation facing = BlockUtils.getBlockPropertyAsOrientation(worldObj, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);
		
		if(facing == BlockLever.EnumOrientation.NORTH){
			this.rotationYaw = 180F;
		}else if(facing == BlockLever.EnumOrientation.WEST){
			this.rotationYaw = 90F;
		}else if(facing == BlockLever.EnumOrientation.SOUTH){
			this.rotationYaw = 0F;
		}else if(facing == BlockLever.EnumOrientation.EAST){
			this.rotationYaw = 270F;
		}
	}

	public double getMountedYOffset(){
		return this.height * -7500D;
	}

	protected boolean shouldSetPosAfterLoading(){
		return false;
	}
	
	public boolean shouldDismountInWater(Entity rider){
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
				
		if(KeyBindings.cameraZoomIn.isPressed()){
			this.zoomCameraView(-1);
		}
		
		if(KeyBindings.cameraZoomOut.isPressed()){
			this.zoomCameraView(1);
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
		BlockLever.EnumOrientation facing = BlockUtils.getBlockPropertyAsOrientation(worldObj, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

		if(facing == BlockLever.EnumOrientation.EAST){
			if((this.rotationYaw - CAMERA_SPEED) > -180F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == BlockLever.EnumOrientation.WEST){
			if((this.rotationYaw - CAMERA_SPEED) > 0F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == BlockLever.EnumOrientation.NORTH){
			if((this.rotationYaw - CAMERA_SPEED) > -270F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == BlockLever.EnumOrientation.SOUTH){
			if((this.rotationYaw - CAMERA_SPEED) > -90F){
				this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
			}
		}
		
		this.updateServerRotation();
	}
	
	public void moveViewRight(){
		BlockLever.EnumOrientation facing = BlockUtils.getBlockPropertyAsOrientation(worldObj, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

		if(facing == BlockLever.EnumOrientation.EAST){
			if((this.rotationYaw + CAMERA_SPEED) < 0F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == BlockLever.EnumOrientation.WEST){
			if((this.rotationYaw + CAMERA_SPEED) < 180F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == BlockLever.EnumOrientation.NORTH){
			if((this.rotationYaw + CAMERA_SPEED) < -90F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}else if(facing == BlockLever.EnumOrientation.SOUTH){
			if((this.rotationYaw + CAMERA_SPEED) < 90F){
				this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
			}
		}
		
		this.updateServerRotation();
	}
	
	public void zoomCameraView(int zoom) {
		if(zoom > 0){
			if(zoomAmount == -0.5F){
				zoomAmount = 1F;
				Minecraft.getMinecraft().theWorld.playSound((double) this.posX,(double) this.posY,(double) this.posZ, SCSounds.CAMERAZOOMIN.path, (float) 1.0F, 1.0F, true);	
			}else if(zoomAmount == 1F){
				zoomAmount = 2F;
				Minecraft.getMinecraft().theWorld.playSound((double) this.posX,(double) this.posY,(double) this.posZ, SCSounds.CAMERAZOOMIN.path, (float) 1.0F, 1.0F, true);	
			}
		}else if(zoom < 0){
			if(zoomAmount == 2F){
				zoomAmount = 1F;
				Minecraft.getMinecraft().theWorld.playSound((double) this.posX,(double) this.posY,(double) this.posZ, SCSounds.CAMERAZOOMIN.path, (float) 1.0F, 1.0F, true);	
			}else if(zoomAmount == 1F){
				zoomAmount = -0.5F;
				Minecraft.getMinecraft().theWorld.playSound((double) this.posX,(double) this.posY,(double) this.posZ, SCSounds.CAMERAZOOMIN.path, (float) 1.0F, 1.0F, true);	
			}
		}
	}
	
	public void setRedstonePower() {
		BlockPos pos = BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));

		if(((CustomizableSCTE) worldObj.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE)){
			if(BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockSecurityCamera.POWERED)){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlock(pos.getX(), pos.getY(), pos.getZ(), "securitycraft:securityCamera", BlockUtils.getBlockMeta(worldObj, pos) - 6));
			}else if(!BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockSecurityCamera.POWERED)){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlock(pos.getX(), pos.getY(), pos.getZ(), "securitycraft:securityCamera", BlockUtils.getBlockMeta(worldObj, pos) + 6));
			}
		}
	}
	
	public void enableNightVision() {
		this.toggleNightVisionCooldown = 30;
		this.shouldProvideNightVision = Utils.toggleBoolean(shouldProvideNightVision);		
	}
	
	public String getCameraInfo(){
		String nowViewing = EnumChatFormatting.UNDERLINE + "Now viewing camera #" + id + "\n\n";
		String pos = EnumChatFormatting.YELLOW + "Pos: " + EnumChatFormatting.RESET + "X: " + (int) Math.floor(posX) + " Y: " + (int) (posY - 1D) + " Z: " + (int) Math.floor(posZ) + "\n";
		String viewingFrom = (this.riddenByEntity != null && mod_SecurityCraft.instance.hasUsePosition(this.riddenByEntity.getName())) ? EnumChatFormatting.YELLOW + "Viewing from: " + EnumChatFormatting.RESET + " X: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getName())[0]) + " Y: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getName())[1]) + " Z: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(this.riddenByEntity.getName())[2]) : "";
		return nowViewing + pos + viewingFrom;
	}
	
	public float getZoomAmount(){
		return zoomAmount;
	}
	
	@SideOnly(Side.CLIENT)
	private void updateServerRotation(){
		mod_SecurityCraft.network.sendToServer(new PacketSSetCameraRotation(this.rotationYaw, this.rotationPitch));
	}
	
    public void setDead(){
        super.setDead();
        
        if(playerViewingName != null && PlayerUtils.isPlayerOnline(playerViewingName)){
        	EntityPlayer player = PlayerUtils.getPlayerFromName(playerViewingName);
        	player.setPosition(cameraUseX, cameraUseY, cameraUseZ);
        	mod_SecurityCraft.network.sendTo(new PacketCSetPlayerPositionAndRotation(cameraUseX, cameraUseY, cameraUseZ, cameraUseYaw, cameraUsePitch), (EntityPlayerMP) player);
        	System.out.println("Setting pos to " + cameraUseX + " | " + cameraUseY + " | " + cameraUseZ + " | " + FMLCommonHandler.instance().getEffectiveSide());
        }
    }
	
	protected void entityInit(){}

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
