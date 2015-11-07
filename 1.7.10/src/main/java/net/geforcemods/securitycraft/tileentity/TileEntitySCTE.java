package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.api.IViewActivated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class TileEntitySCTE extends TileEntity {

	private boolean viewActivated = false;
	private boolean attacks = false;
	
	private double attackRange = 0.0D;

	private int blockPlaceCooldown = 30;
	private int ticksBetweenAttacks = 0;
	private int attackCooldown = 0;
	
	private Class typeToAttack = Entity.class;

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
	        List<?> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
	        Iterator<?> iterator = list.iterator();
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

		if (attacks) {	
			if (attackCooldown < ticksBetweenAttacks) {
				attackCooldown++;
				return;
			}
			
			if (canAttack()) {
				int i = xCoord;
		        int j = yCoord;
		        int k = zCoord;
		        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1)).expand(attackRange, attackRange, attackRange);
		        List<?> list = this.worldObj.getEntitiesWithinAABB(typeToAttack, axisalignedbb);
		        Iterator<?> iterator = list.iterator();
		        
		        boolean attacked = false;
	
		        while (iterator.hasNext()) {
					Entity mobToAttack = (Entity) iterator.next();
					
					if (mobToAttack == null || mobToAttack instanceof EntityItem) {
						continue;
					}
		        	
					if (attackEntity(mobToAttack)) {
						attacked = true;
					}
		        }
		        
		        if (attacked) {
		        	attackCooldown = 0;
		        }
			}
		}
	}
	
	public void activatedByView(EntityLivingBase entity) {
		((IViewActivated) this.worldObj.getBlock(xCoord, yCoord, zCoord)).onEntityLookedAtBlock(worldObj, xCoord, yCoord, zCoord, entity);
	}
	
	public boolean attackEntity(Entity entity) {
		return false;
	}
	
	public boolean canAttack() {
		return false;
	}
	
	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	
        par1NBTTagCompound.setBoolean("viewActivated", viewActivated);
        par1NBTTagCompound.setBoolean("attacks", attacks);
        par1NBTTagCompound.setDouble("attackRange", attackRange);
        par1NBTTagCompound.setInteger("attackCooldown", attackCooldown);
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
        
        if (par1NBTTagCompound.hasKey("attacks"))
        {
            this.attacks = par1NBTTagCompound.getBoolean("attacks");
        }

        if (par1NBTTagCompound.hasKey("attackRange"))
        {
            this.attackRange = par1NBTTagCompound.getDouble("attackRange");
        }
        
        if (par1NBTTagCompound.hasKey("attackCooldown"))
        {
            this.attackCooldown = par1NBTTagCompound.getInteger("attackCooldown");
        }   
    }
    
    public Packet getDescriptionPacket() {                
    	NBTTagCompound tag = new NBTTagCompound();                
    	this.writeToNBT(tag);                
    	return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);        
    }        
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {                
    	readFromNBT(packet.func_148857_g());        
    }
    
    /**
     * Sets this TileEntity able to be activated when a player looks at the block. 
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
    
    /**
     * Sets this TileEntity able to attack.
     * <p>
     * Calls {@link TileEntitySCTE}.attackEntity(Entity) when this TE's cooldown equals 0.
     * 
     * @param entityType
     * @param range
     * @param cooldown
     * @return
     */
    
    public TileEntitySCTE attacks(Class entityType, int range, int cooldown) {
    	attacks = true;
    	typeToAttack = entityType;
    	attackRange = range;
    	ticksBetweenAttacks = cooldown;
    	return this;
    }
    
    /**
     * @return The range that this TileEntity checks for attackable entities.
     */
    public double getAttackRange() {
    	return attackRange;
    }
    
    /**
     *  Set the number of ticks before {@link TileEntitySCTE}.attackEntity(Entity) is called.
     */ 
    public int getAttackCooldown() {
    	return attackCooldown;
    }
    
    /**
     * @return If this TileEntity can attack.
     */  
    public boolean doesAttack() {
    	return attacks;
    }
    
}
