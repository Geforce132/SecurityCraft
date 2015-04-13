package org.freeforums.geforce.securitycraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class EntitySecurityCamera extends Entity
{
	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;
	private EnumFacing dir = null;
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

	public EntitySecurityCamera(World world, double x, double y, double z, double y0ffset, int rotation, double rotationOffset)
	{
		this(world);
		this.blockPosX = (int) x;
		this.blockPosY = (int) y;
		this.blockPosZ = (int) z;
		setPostionConsideringRotation(x + 0.5D, y + y0ffset, z + 0.5D, rotation, rotationOffset);
	}

	public void setPostionConsideringRotation(double x, double y, double z, int rotation, double rotationOffset)
	{
		switch (rotation)
		{
		case 2:
			z += rotationOffset;
			break;
		case 0:
			z -= rotationOffset;
			break;
		case 3:
			x -= rotationOffset;
			break;
		case 1:
			x += rotationOffset;
			break;
		}
		setPosition(x, y, z);
	}

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
		if(dir == null && this.worldObj.getBlockState(new BlockPos(Math.floor(posX), posY - 1D, Math.floor(posZ))).getBlock() == mod_SecurityCraft.securityCamera){
			this.dir = (EnumFacing) this.worldObj.getBlockState(new BlockPos(Math.floor(posX), posY - 1D, Math.floor(posZ))).getValue(BlockSecurityCamera.FACING);
		}
		
		System.out.println((ticks += 1) + " | " + dir);
				
		if(this.riddenByEntity != null){
			if(dir == EnumFacing.EAST && !reverseRotation){
				this.setRotation(this.rotationYaw += 0.5F, this.rotationPitch);
				
				((EntityPlayer) this.riddenByEntity).setPositionAndRotation(posX, posY, posZ, rotationYaw, 30F);
				

				if(ticks > 360){
					ticks = 0;
					reverseRotation = true;
					System.out.println("Setting rr to " + reverseRotation + " on the " + FMLCommonHandler.instance().getEffectiveSide() + " side");
				}
			}else if(dir == EnumFacing.EAST && reverseRotation){
				this.setRotation(this.rotationYaw -= 0.5F, this.rotationPitch);
				
				((EntityPlayer) this.riddenByEntity).setPositionAndRotation(posX, posY, posZ, rotationYaw, 30F);
				
				if(ticks > 360){
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
			if (this.riddenByEntity == null | this.worldObj.isAirBlock(new BlockPos(blockPosX, blockPosY, blockPosZ)))
			{
				this.setDead();
				return;
			}	
		}
	}

	protected void entityInit(){}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound){}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound){}

}
