package net.geforcemods.securitycraft.api;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Simple TileEntity that SecurityCraft uses to easily create blocks like
 * the retinal scanner (activated by looking at it) and attacking blocks
 * like the protecto. Everything can be overridden for easy customization
 * or use as an API.
 * 
 * @version 1.1.0
 * 
 * @author Geforce
 */
public class TileEntitySCTE extends TileEntity implements ITickable {

	protected boolean intersectsEntities = false;
	protected boolean viewActivated = false;
	private boolean attacks = false;
	
	private double attackRange = 0.0D;

	private int blockPlaceCooldown = 30;
	private int ticksBetweenAttacks = 0;
	private int attackCooldown = 0;
	
	private Class<?> typeToAttack = Entity.class;
	
	public void update() {
		if(intersectsEntities){
			int i = this.pos.getX();
	        int j = this.pos.getY();
	        int k = this.pos.getZ();
	        AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1)));
	        List<?> list = this.worldObj.getEntitiesWithinAABB(Entity.class, axisalignedbb);
	        Iterator<?> iterator = list.iterator();
	        Entity entity;
	
	        while (iterator.hasNext())
	        {
	        	entity = (Entity)iterator.next();
	        	entityIntersecting(entity);
	        }	        	   
		}
		
		if(viewActivated){
			if(blockPlaceCooldown > 0){ 
				blockPlaceCooldown--; 
				return;
			}
			
			int i = this.pos.getX();
	        int j = this.pos.getY();
	        int k = this.pos.getZ();
	        AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)i, (double)j, (double)k, (double)(i), (double)(j), (double)(k)).expand(5, 5, 5));
	        List<?> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
	        Iterator<?> iterator = list.iterator();
	        EntityLivingBase entity;
	
	        while (iterator.hasNext())
	        {
	        	entity = (EntityLivingBase)iterator.next();
	        	double eyeHeight = (double) entity.getEyeHeight();
	        	
	        	Vec3 lookVec = new Vec3((entity.posX + (entity.getLookVec().xCoord * 5)), ((eyeHeight + entity.posY) + (entity.getLookVec().yCoord * 5)), (entity.posZ + (entity.getLookVec().zCoord * 5)));
	        	
	        	MovingObjectPosition mop = getWorld().rayTraceBlocks(new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), lookVec);
	        	if(mop != null && mop.typeOfHit == MovingObjectType.BLOCK){
	        		if(mop.getBlockPos().getX() == getPos().getX() && mop.getBlockPos().getY() == getPos().getY() && mop.getBlockPos().getZ() == getPos().getZ()){
	        			entityViewed(entity);
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
				int i = pos.getX();
		        int j = pos.getY();
		        int k = pos.getZ();
		        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1)).expand(attackRange, attackRange, attackRange);
		        List<?> list = this.worldObj.getEntitiesWithinAABB(entityTypeToAttack(), axisalignedbb);
		        Iterator<?> iterator = list.iterator();

		        if(!worldObj.isRemote){
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
			        
			        if (attacked || shouldRefreshAttackCooldown()) {
			        	attackCooldown = 0;
				    	MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(getDescriptionPacket());
			        }
		        }
			}
		}
	}
	
	public void entityIntersecting(Entity entity) {
		((IIntersectable) this.worldObj.getBlockState(getPos()).getBlock()).onEntityIntersected(getWorld(), getPos(), entity);
	}
	
	/**
	 * Called when {@link TileEntitySCTE}.isViewActivated(), and when an entity looks directly at this block.
	 */
    public void entityViewed(EntityLivingBase entity) {}
	
    /**
	 * Handle your TileEntity's attack to entities here. 
	 * ONLY RUNS ON THE SERVER SIDE (to keep the TE's client cooldown in-sync)! If you need something done on the client,
	 * use packets.<p>
	 * 
	 * @return True if it successfully attacked, false otherwise.
	 */
	public boolean attackEntity(Entity entity) {
		return false;
	}
	
	/**
	 * Check if your TileEntity is ready to attack. (i.e: block conditions, metadata, etc.) <p>
	 * Different from {@link TileEntitySCTE}.doesAttack(), which simply returns if your TileEntity <i>does</i> attack.
	 */
	public boolean canAttack() {
		return false;
	}
	
	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	
        par1NBTTagCompound.setBoolean("intersectsEntities", intersectsEntities);
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

        if (par1NBTTagCompound.hasKey("intersectsEntities"))
        {
            this.intersectsEntities = par1NBTTagCompound.getBoolean("intersectsEntities");
        }
        
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
    
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
    	return (oldState.getBlock() != newState.getBlock());
    }
    
    public Packet<?> getDescriptionPacket() {                
    	NBTTagCompound tag = new NBTTagCompound();                
    	this.writeToNBT(tag);                
    	return new S35PacketUpdateTileEntity(pos, 1, tag);        
    }        
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {                
    	readFromNBT(packet.getNbtCompound());        
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
    
    public boolean doesIntersectsEntities(){
        return intersectsEntities;
    }
    
    /**
     * Sets this TileEntity able to be activated when a player looks at the block. 
     * <p>
     * Calls {@link TileEntitySCTE}.entityViewed(EntityLivingBase) when an {@link EntityLivingBase} looks at this block.
     * <p>
     * Implement IViewActivated in your Block class in order to do stuff with that event.
     */
    public TileEntitySCTE activatedByView(){
        viewActivated = true;
        return this;
    }
    
    /**
     * @return If this TileEntity can be activated when an Entity looking at it.
     */
    public boolean isActivatedByView(){
        return viewActivated;
    }
    
    /**
     * Sets this TileEntity able to attack.
     * <p>
     * Calls {@link TileEntitySCTE}.attackEntity(Entity) when this TE's cooldown equals 0.
     */ 
    public TileEntitySCTE attacks(Class<?> type, int range, int cooldown) {
    	attacks = true;
    	typeToAttack = type;
    	attackRange = range;
    	ticksBetweenAttacks = cooldown;
    	return this;
    }
    
    /**
     * @return The class of the entity that this TileEntity should attack.
     */
    public Class entityTypeToAttack(){
    	return typeToAttack;
    }
    
    /**
     * @return The range that this TileEntity checks for attackable entities.
     */
    public double getAttackRange() {
    	return attackRange;
    }
    
    /**
     * @return The number of ticks between attacks.
     */
    public int getTicksBetweenAttacks() {
    	return ticksBetweenAttacks;
    }
    
    /**
     * @return Gets the number of ticks before {@link TileEntitySCTE}.attackEntity(Entity) is called.
     */ 
    public int getAttackCooldown() {
    	return attackCooldown;
    }
    
    /**
     *  Set this TileEntity's attack cooldown.
     */ 
    public void setAttackCooldown(int cooldown) {
    	attackCooldown = cooldown;
    }
    
    /**
     *  Maxes out this TileEntity's attack cooldown, so it'll attempt to attack next tick.
     */ 
    public void attackNextTick() {
    	attackCooldown = ticksBetweenAttacks;
    }
    
    /**
     * @return If, once this TileEntity's attack cooldown gets to the set maximum,
     *         it should start again automatically from 0.
     */
    public boolean shouldRefreshAttackCooldown() {
    	return true;
    }
    
    /**
     * @return If this TileEntity can attack.
     */  
    public boolean doesAttack() {
    	return attacks;
    }

}
