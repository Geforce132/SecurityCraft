package net.breakinbad.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.breakinbad.securitycraft.api.IViewActivated;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class TileEntitySCTE extends TileEntity{

	protected boolean viewActivated = false;
	
	private int blockPlaceCooldown = 30;
	
	public void updateEntity() {		
		if(viewActivated){
			if(blockPlaceCooldown > 0){ 
				blockPlaceCooldown--; 
				return;
			}
			
			int i = xCoord;
	        int j = yCoord;
	        int k = zCoord;
	        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double)i, (double)j, (double)k, (double)(i), (double)(j), (double)(k)).expand(5, 5, 5);
	        List list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
	        Iterator iterator = list.iterator();
	        EntityLivingBase entity;

	        while (iterator.hasNext())
	        {
	        	entity = (EntityLivingBase)iterator.next();
	        	double eyeHeight = (double) entity.getEyeHeight();
	        	
	        	Vec3 lookVec = Vec3.createVectorHelper((entity.posX + (entity.getLookVec().xCoord * 5)), ((eyeHeight + entity.posY) + (entity.getLookVec().yCoord * 5)), (entity.posZ + (entity.getLookVec().zCoord * 5)));
	        	
	        	MovingObjectPosition mop = worldObj.rayTraceBlocks(Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), lookVec);
	        	if(mop != null && mop.typeOfHit == MovingObjectType.BLOCK){
	        		if(mop.blockX == xCoord && mop.blockY == yCoord && mop.blockZ == zCoord){
	        			activatedByView(entity);
	        		}
	        	}
	        }
		}
	}
	
	public void activatedByView(EntityLivingBase entity) {
		((IViewActivated) this.worldObj.getBlock(xCoord, yCoord, zCoord)).onEntityLookedAtBlock(worldObj, xCoord, yCoord, zCoord, entity);
	}
	
	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	
        par1NBTTagCompound.setBoolean("viewActivated", viewActivated);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("viewActivated"))
        {
            this.viewActivated = par1NBTTagCompound.getBoolean("viewActivated");
        }
    }
    
    /**
     * Sets the TileEntity able to be activated when a player looks at the block. 
     * <p>
     * Calls {@link IViewActivated}.onEntityLookedAtBlock(World, BlockPos, EntityLivingBase) when a {@link EntityPlayer} looks at this block.
     * <p>
     * Implement IViewActivated in your Block class in order to do stuff with that event.
     */
    public TileEntitySCTE activatedByView(){
        viewActivated = true;
        return this;
    }
    
    public boolean isActivatedByView(){
        return viewActivated;
    }
    
}
