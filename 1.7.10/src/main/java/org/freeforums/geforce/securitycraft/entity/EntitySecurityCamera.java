package org.freeforums.geforce.securitycraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.misc.KeyBindings;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class EntitySecurityCamera extends Entity{
	
	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;

	public EntitySecurityCamera(World world)
	{
		super(world);
		this.noClip = true;
		this.height = 0.0001F;
		this.width = 0.0001F;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, double y0ffset)
	{
		this(world);
		this.blockPosX = (int) x;
		this.blockPosY = (int) y;
		this.blockPosZ = (int) z;
		setPosition(x + 0.5D, y + y0ffset, z + 0.5D);
		
		this.rotationPitch = 30F;
			
		int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));
		
		if(meta == 1){
			this.rotationYaw = 180F;
		}else if(meta == 2){
			this.rotationYaw = 90F;
		}else if(meta == 3){
			this.rotationYaw = 0F;
		}else if(meta == 4){
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
			if(KeyBindings.cameraRotateUp.getIsKeyPressed()){
				this.moveViewUp();
			}
			
			if(KeyBindings.cameraRotateDown.getIsKeyPressed()){
				this.moveViewDown();
			}
			
			if(KeyBindings.cameraRotateLeft.getIsKeyPressed()){
				this.moveViewLeft();
			}
			
			if(KeyBindings.cameraRotateRight.getIsKeyPressed()){
				this.moveViewRight();
			}
			
			if(KeyBindings.cameraZoomIn.getIsKeyPressed()){
				this.zoomCameraView(1);
			}
			
			if(KeyBindings.cameraZoomOut.getIsKeyPressed()){
				this.zoomCameraView(-1);
			}
			
//			int mouseMovement = (Mouse.getDWheel() / 120);
//			
//			if(mouseMovement != 0){
//				this.zoomCameraView(mouseMovement);
//			}
						
			//System.out.println(this.rotationYaw + " | " + this.rotationPitch);
			((EntityPlayer) this.riddenByEntity).rotationYaw = this.rotationYaw;
			((EntityPlayer) this.riddenByEntity).rotationPitch = this.rotationPitch;
		}
		
		//System.out.println(this.rotationYaw + " | " + this.reverseRotation);

		
//		if(this.riddenByEntity != null){
//			((EntityPlayer) this.riddenByEntity).setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
//		
//			if(((EntityPlayer) this.riddenByEntity).rotationPitch != 40F){
//				((EntityPlayer) this.riddenByEntity).setPositionAndRotation(posX, posY, posZ, rotationYaw, 30F);
//			}
//		}
		
		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity == null | this.worldObj.isAirBlock(blockPosX, blockPosY, blockPosZ))
			{
				this.setDead();
				return;
			}	
		}
	}

	public void moveViewUp() {
		if(this.rotationPitch > -25F){
			this.setRotation(this.rotationYaw, this.rotationPitch -= 1.25F);
		}
	}
	
	public void moveViewDown(){
		if(this.rotationPitch < 60F){
			this.setRotation(this.rotationYaw, this.rotationPitch += 1.25F);
		}
	}
	
	public void moveViewLeft() {
		int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));

		if(meta == 1){
			if(this.rotationYaw > -270F){
				this.setRotation(this.rotationYaw -= 1.25F, this.rotationPitch);
			}
		}else if(meta == 2){
			if(this.rotationYaw > 0F){
				this.setRotation(this.rotationYaw -= 1.25F, this.rotationPitch);
			}
		}else if(meta == 3){
			if(this.rotationYaw > -90F){
				this.setRotation(this.rotationYaw -= 1.25F, this.rotationPitch);
			}
		}else if(meta == 4){
			if(this.rotationYaw > -180F){
				this.setRotation(this.rotationYaw -= 1.25F, this.rotationPitch);
			}
		}
	}
	
	public void moveViewRight(){
		int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));

		if(meta == 1){
			if(this.rotationYaw < -90F){
				this.setRotation(this.rotationYaw += 1.25F, this.rotationPitch);
			}
		}else if(meta == 2){
			if(this.rotationYaw < 180F){
				this.setRotation(this.rotationYaw += 1.25F, this.rotationPitch);
			}
		}else if(meta == 3){
			if(this.rotationYaw < 90F){
				this.setRotation(this.rotationYaw += 1.25F, this.rotationPitch);
			}
		}else if(meta == 4){
			if(this.rotationYaw < 0F){
				this.setRotation(this.rotationYaw += 1.25F, this.rotationPitch);
			}
		}
	}
	
	public void zoomCameraView(int mouseWheelMovement) {
		if(mouseWheelMovement > 0){
			HelpfulMethods.setCameraZoom(0.1D);
		}else if(mouseWheelMovement < 0 && HelpfulMethods.getCameraZoom() > 1.0D){
			HelpfulMethods.setCameraZoom(-0.1D);
		}
	}
	
    public void setDead(){
        super.setDead();
        
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
        	HelpfulMethods.setCameraZoom(0.0D);
        }
    }
	
	protected void entityInit(){}

	public void writeEntityToNBT(NBTTagCompound tagCompound){}

    public void readEntityFromNBT(NBTTagCompound tagCompound){}

}
