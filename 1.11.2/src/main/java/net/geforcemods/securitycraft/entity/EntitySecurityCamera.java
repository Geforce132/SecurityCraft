package net.geforcemods.securitycraft.entity;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCSetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.packets.PacketGivePotionEffect;
import net.geforcemods.securitycraft.network.packets.PacketSSetCameraRotation;
import net.geforcemods.securitycraft.network.packets.PacketSetBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySecurityCamera extends Entity{

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
			
		EnumFacing facing = BlockUtils.getBlockPropertyAsEnum(world, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);
		
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

		EnumFacing facing = BlockUtils.getBlockPropertyAsEnum(world, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);
		
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
	
	@Override
	public double getMountedYOffset(){
		return this.height * -7500D;
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
		if(this.world.isRemote && isBeingRidden()){
			EntityPlayer lowestEntity = (EntityPlayer)getPassengers().get(0);
			
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
			
			if(lowestEntity.rotationYaw != this.rotationYaw){
				lowestEntity.setPositionAndRotation(lowestEntity.posX, lowestEntity.posY, lowestEntity.posZ, this.rotationYaw, this.rotationPitch);
				lowestEntity.rotationYaw = this.rotationYaw;
			}
			
			if(lowestEntity.rotationPitch != this.rotationPitch){
				lowestEntity.setPositionAndRotation(lowestEntity.posX, lowestEntity.posY, lowestEntity.posZ, this.rotationYaw, this.rotationPitch);
			}
		
			this.checkKeysPressed();

			if(Mouse.hasWheel() && Mouse.isButtonDown(2) && this.screenshotCooldown == 0){	
				this.screenshotCooldown = 30;
				ClientUtils.takeScreenshot();
				Minecraft.getMinecraft().world.playSound(new BlockPos(posX, posY, posZ), SoundEvent.REGISTRY.getObject(SCSounds.CAMERASNAP.location), SoundCategory.BLOCKS, 1.0F, 1.0F, true);
			}
					
			if(getPassengers().size() != 0 && this.shouldProvideNightVision){
				mod_SecurityCraft.network.sendToServer(new PacketGivePotionEffect(Potion.getIdFromPotion(Potion.getPotionFromResourceLocation("night_vision")), 3, -1));
			}
		}
		
		if(!this.world.isRemote){
			if(getPassengers().size() == 0 | BlockUtils.getBlock(world, blockPosX, blockPosY, blockPosZ) != mod_SecurityCraft.securityCamera){
				this.setDead();
				return;
			}	
		}
	}

	@SideOnly(Side.CLIENT)
	private void checkKeysPressed() {
		if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isPressed()) {
			this.dismountRidingEntity();
		}
		
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
		if(BlockUtils.hasBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING)) {
			EnumFacing facing = BlockUtils.getBlockPropertyAsEnum(world, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);
	
			if(facing == EnumFacing.EAST){
				if((this.rotationYaw - CAMERA_SPEED) > -180F){
					this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
				}
			}else if(facing == EnumFacing.WEST){
				if((this.rotationYaw - CAMERA_SPEED) > 0F){
					this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
				}
			}else if(facing == EnumFacing.NORTH){
				// Handles some problems the occurs from the way the rotationYaw value works in MC
				if((((this.rotationYaw - CAMERA_SPEED) > 90F) && ((this.rotationYaw - CAMERA_SPEED) < 185F)) || (((this.rotationYaw - CAMERA_SPEED) > -190F) && ((this.rotationYaw - CAMERA_SPEED) < -90F))){
					this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
				}
			}else if(facing == EnumFacing.SOUTH){
				if((this.rotationYaw - CAMERA_SPEED) > -90F){
					this.setRotation(this.rotationYaw -= CAMERA_SPEED, this.rotationPitch);
				}
			}
			
			this.updateServerRotation();
		}
	}
	
	public void moveViewRight(){
		if(BlockUtils.hasBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING)) {
			EnumFacing facing = BlockUtils.getBlockPropertyAsEnum(world, BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ)), BlockSecurityCamera.FACING);
	
			if(facing == EnumFacing.EAST){
				if((this.rotationYaw + CAMERA_SPEED) < 0F){
					this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
				}
			}else if(facing == EnumFacing.WEST){
				if((this.rotationYaw + CAMERA_SPEED) < 180F){
					this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
				}
			}else if(facing == EnumFacing.NORTH){
				if((((this.rotationYaw + CAMERA_SPEED) > 85F) && ((this.rotationYaw + CAMERA_SPEED) < 185F)) || ((this.rotationYaw + CAMERA_SPEED) < -95F) && ((this.rotationYaw + CAMERA_SPEED) > -180F)){
					this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
				}
			}else if(facing == EnumFacing.SOUTH){
				if((this.rotationYaw + CAMERA_SPEED) < 90F){
					this.setRotation(this.rotationYaw += CAMERA_SPEED, this.rotationPitch);
				}
			}
			
			this.updateServerRotation();
		}
	}
	
	public void zoomCameraView(int zoom) {
		if(zoom > 0){
			if(zoomAmount == -0.5F){
				zoomAmount = 1F;
			}else if(zoomAmount == 1F){
				zoomAmount = 2F;
			}
		}else if(zoom < 0){
			if(zoomAmount == 2F){
				zoomAmount = 1F;
			}else if(zoomAmount == 1F){
				zoomAmount = -0.5F;
			}
		}
		
		Minecraft.getMinecraft().world.playSound(new BlockPos(posX, posY, posZ), SoundEvent.REGISTRY.getObject(SCSounds.CAMERAZOOMIN.location), SoundCategory.BLOCKS, 1.0F, 1.0F, true);	
	}
	
	public void setRedstonePower() {
		BlockPos pos = BlockUtils.toPos((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));

		if(((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE)){
			if(BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockSecurityCamera.POWERED)){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlock(pos.getX(), pos.getY(), pos.getZ(), "securitycraft:securityCamera", BlockUtils.getBlockMeta(world, pos) - 6));
			}else if(!BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockSecurityCamera.POWERED)){
				mod_SecurityCraft.network.sendToServer(new PacketSetBlock(pos.getX(), pos.getY(), pos.getZ(), "securitycraft:securityCamera", BlockUtils.getBlockMeta(world, pos) + 6));
			}
		}
	}
	
	public void enableNightVision() {
		this.toggleNightVisionCooldown = 30;
		this.shouldProvideNightVision = !this.shouldProvideNightVision;		
	}
	
	public String getCameraInfo(){
		String nowViewing = TextFormatting.UNDERLINE + "Now viewing camera #" + id + "\n\n";
		String pos = TextFormatting.YELLOW + "Pos: " + TextFormatting.RESET + "X: " + (int) Math.floor(posX) + " Y: " + (int) (posY - 1D) + " Z: " + (int) Math.floor(posZ) + "\n";
		String viewingFrom = (getPassengers().size() != 0 && mod_SecurityCraft.instance.hasUsePosition(getPassengers().get(0).getName())) ? TextFormatting.YELLOW + "Viewing from: " + TextFormatting.RESET + " X: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(getPassengers().get(0).getName())[0]) + " Y: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(getPassengers().get(0).getName())[1]) + " Z: " + (int) Math.floor((Double) mod_SecurityCraft.instance.getUsePosition(getPassengers().get(0).getName())[2]) : "";
		return nowViewing + pos + viewingFrom;
	}
	
	public float getZoomAmount(){
		return zoomAmount;
	}
	
	@SideOnly(Side.CLIENT)
	private void updateServerRotation(){
		mod_SecurityCraft.network.sendToServer(new PacketSSetCameraRotation(this.rotationYaw, this.rotationPitch));
	}
	
    @Override
	public void setDead(){
        super.setDead();
        
        if(playerViewingName != null && PlayerUtils.isPlayerOnline(playerViewingName)){
        	EntityPlayer player = PlayerUtils.getPlayerFromName(playerViewingName);
        	player.setPositionAndUpdate(cameraUseX, cameraUseY, cameraUseZ);
        	mod_SecurityCraft.network.sendTo(new PacketCSetPlayerPositionAndRotation(cameraUseX, cameraUseY, cameraUseZ, cameraUseYaw, cameraUsePitch), (EntityPlayerMP) player);
        }
    }
	
	@Override
	protected void entityInit(){}

	@Override
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

    @Override
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
