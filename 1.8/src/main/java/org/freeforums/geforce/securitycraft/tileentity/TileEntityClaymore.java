package org.freeforums.geforce.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import org.freeforums.geforce.securitycraft.blocks.mines.BlockClaymore;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class TileEntityClaymore extends TileEntity implements IUpdatePlayerListBox{

	private double entityX = -1D;
	private double entityY = -1D;
	private double entityZ = -1D;
	private int cooldown = -1;
	
	public void update() {
		if(getWorld().isRemote){
			return;
		}else{		
			if(cooldown > 0){
				cooldown--;
				return;
			}
			
			if(cooldown == 0){
				Utils.destroyBlock(getWorld(), getPos(), false);
				getWorld().createExplosion((Entity) null, entityX, entityY, entityZ, 3.5F, true);
				return;
			}
			
			EnumFacing dir = Utils.getBlockProperty(getWorld(), getPos(), BlockClaymore.FACING);
			AxisAlignedBB axisalignedbb = AxisAlignedBB.fromBounds((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
			
			if(dir == EnumFacing.NORTH){
				axisalignedbb = axisalignedbb.addCoord(0, 0, -mod_SecurityCraft.configHandler.claymoreRange);
			}else if(dir == EnumFacing.SOUTH){
				axisalignedbb = axisalignedbb.addCoord(0, 0, mod_SecurityCraft.configHandler.claymoreRange);
			}if(dir == EnumFacing.EAST){
				axisalignedbb = axisalignedbb.addCoord(mod_SecurityCraft.configHandler.claymoreRange, 0, 0);
			}else if(dir == EnumFacing.WEST){
				axisalignedbb = axisalignedbb.addCoord(-mod_SecurityCraft.configHandler.claymoreRange, 0, 0);
			}
			
			List list = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
			Iterator iterator = list.iterator();
			EntityLivingBase entityliving;
			
			while(iterator.hasNext()){
				entityliving = (EntityLivingBase) iterator.next();
				
				entityX = entityliving.posX;
				entityY = entityliving.posY;
				entityZ = entityliving.posZ;
				cooldown = 20;
				
				getWorld().playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);
				break;
			}
		}

	}
	
	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("cooldown", cooldown);
        par1NBTTagCompound.setDouble("entityX", entityX);
        par1NBTTagCompound.setDouble("entityY", entityY);
        par1NBTTagCompound.setDouble("entityZ", entityZ);

    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("cooldown"))
        {
            this.cooldown = par1NBTTagCompound.getInteger("cooldown");
        }
        
        if (par1NBTTagCompound.hasKey("entityX"))
        {
            this.entityX = par1NBTTagCompound.getDouble("entityX");
        }
        
        if (par1NBTTagCompound.hasKey("entityY"))
        {
            this.entityY = par1NBTTagCompound.getDouble("entityY");
        }
        
        if (par1NBTTagCompound.hasKey("entityZ"))
        {
            this.entityZ = par1NBTTagCompound.getDouble("entityZ");
        }
    }

}
