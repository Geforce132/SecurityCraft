package org.freeforums.geforce.securitycraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntitySecurityCamera extends Entity
{
	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;

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

	public void onEntityUpdate()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity == null | this.worldObj.isAirBlock(new BlockPos(blockPosX, blockPosY, blockPosZ)))
			{
				this.setDead();
			}
		}
	}

	protected void entityInit(){}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound){}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound){}

}
