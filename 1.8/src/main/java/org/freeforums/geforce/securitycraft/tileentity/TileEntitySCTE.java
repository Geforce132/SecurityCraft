package org.freeforums.geforce.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import org.freeforums.geforce.securitycraft.api.IIntersectable;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntitySCTE extends TileEntity implements IUpdatePlayerListBox{

	protected boolean intersectsEntities = false;
	
	public void update() {
		if(intersectsEntities){
			int i = this.pos.getX();
	        int j = this.pos.getY();
	        int k = this.pos.getZ();
	        AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1)));
	        List list = this.worldObj.getEntitiesWithinAABB(Entity.class, axisalignedbb);
	        Iterator iterator = list.iterator();
	        Entity entity;
	
	        while (iterator.hasNext())
	        {
	        	entity = (Entity)iterator.next();
	            run(entity);
	        }	        	   
		}
	}
	
	public void run(Entity entity) {
		((IIntersectable) this.worldObj.getBlockState(getPos()).getBlock()).onEntityIntersected(getWorld(), getPos(), entity);
	}
	
	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	
        par1NBTTagCompound.setBoolean("intersectsEntities", intersectsEntities);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("intersectsEntities"))
        {
            this.intersectsEntities = par1NBTTagCompound.getBoolean("intersectsEntities");
        }
    }
    
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
    	return (oldState.getBlock() != newState.getBlock());
    }
    
    /**
     * Sets the TileEntity able to be intersected with. 
     * <p>
     * Calls {@link IIntersectable}.onEntityIntersected(World, BlockPos, EntityLivingBase) when a {@link EntityLivingBase} comes into contact with this block.
     * <p>
     * Implement IIntersectable in your Block class in order to do stuff with that event.
     */
    public TileEntitySCTE intersectsEntities(){
        intersectsEntities = true;
        return this;
    }

}
