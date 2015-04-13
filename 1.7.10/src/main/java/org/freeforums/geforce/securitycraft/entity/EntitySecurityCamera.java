package org.freeforums.geforce.securitycraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

import cpw.mods.fml.common.FMLCommonHandler;

public class EntitySecurityCamera extends Entity{
	
	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;
	private boolean reverseRotation = false;
	private int ticks = 0;

	public EntitySecurityCamera(World world)
	{
		super(world);
		this.noClip = true;
		this.height = 0.01F;
		this.width = 0.01F;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, double y0ffset)
	{
		this(world);
		this.blockPosX = (int) x;
		this.blockPosY = (int) y;
		this.blockPosZ = (int) z;
		setPosition(x + 0.5D, y + y0ffset, z + 0.5D);
	}

//	public EntitySecurityCamera(World world, double x, double y, double z, double y0ffset, int rotation, double rotationOffset)
//	{
//		this(world);
//		this.blockPosX = (int) x;
//		this.blockPosY = (int) y;
//		this.blockPosZ = (int) z;
//		setPostionConsideringRotation(x + 0.5D, y + y0ffset, z + 0.5D, rotation, rotationOffset);
//	}
//
//	public void setPostionConsideringRotation(double x, double y, double z, int rotation, double rotationOffset)
//	{
//		switch (rotation)
//		{
//		case 2:
//			z += rotationOffset;
//			break;
//		case 0:
//			z -= rotationOffset;
//			break;
//		case 3:
//			x -= rotationOffset;
//			break;
//		case 1:
//			x += rotationOffset;
//			break;
//		}
//		setPosition(x, y, z);
//	}

	public double getMountedYOffset()
	{
		return this.height * 0.0D;
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
		if(this.riddenByEntity != null){		
			int meta = this.worldObj.getBlockMetadata((int) Math.floor(posX), (int) (posY - 1D), (int) Math.floor(posZ));
			//System.out.println(ticks);

			if(meta == 2 && !reverseRotation){
				this.setRotation(this.rotationYaw += 0.5F, this.rotationPitch);

				((EntityPlayer) this.riddenByEntity).rotationYaw = (this.rotationYaw % 360.0F); //.setPositionAndRotation(posX, posY, posZ, rotationYaw, 30F);
				((EntityPlayer) this.riddenByEntity).rotationPitch = (30F % 360.0F); //.setPositionAndRotation(posX, posY, posZ, rotationYaw, 30F);

				//System.out.println(this.rotationYaw + " | " + FMLCommonHandler.instance().getEffectiveSide());

				if(ticks > 360){ //this.rotationYaw > 180F
					ticks = 0;
					reverseRotation = true;
					System.out.println("Setting rr to " + reverseRotation + " on the " + FMLCommonHandler.instance().getEffectiveSide() + " side");
				}
			}else if(meta == 2 && reverseRotation){
				this.setRotation(this.rotationYaw -= 0.5F, this.rotationPitch);
				
				((EntityPlayer) this.riddenByEntity).rotationYaw = (this.rotationYaw % 360.0F); //.setPositionAndRotation(posX, posY, posZ, rotationYaw, 30F);
				((EntityPlayer) this.riddenByEntity).rotationPitch = (30F % 360.0F);
				
				if(ticks > 360){ //this.rotationYaw < 0F
					ticks = 0;
					reverseRotation = false;
					System.out.println("Setting rr to " + reverseRotation + " on the " + FMLCommonHandler.instance().getEffectiveSide() + " side");
				}
			}
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

	protected void entityInit(){}

	public void writeEntityToNBT(NBTTagCompound tagCompound){
        tagCompound.setInteger("ticks", ticks);
        tagCompound.setBoolean("reverseRotation", reverseRotation);
    }

    public void readEntityFromNBT(NBTTagCompound tagCompound){
        this.ticks = tagCompound.getInteger("ticks");
        this.reverseRotation = tagCompound.getBoolean("reverseRotation");
    }

}
