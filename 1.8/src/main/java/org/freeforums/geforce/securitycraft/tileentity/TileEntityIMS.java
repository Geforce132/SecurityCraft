package org.freeforums.geforce.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import org.freeforums.geforce.securitycraft.blocks.mines.BlockIMS;
import org.freeforums.geforce.securitycraft.entity.EntityIMSBomb;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.Utils.WorldUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketCPlaySoundAtPos;
import org.jsoup.helper.Validate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityIMS extends TileEntityOwnable {
	
	/** The targeting option currently selected for this IMS. 0 = players, 1 = hostile mobs & players.**/
	private int targetingOption = 1;
	
	public void update(){
		super.update();
		
		if(this.worldObj.getTotalWorldTime() % 80L == 0L){
            this.launchMine();
        }
	}

    /**
	 * Create a bounding box around the IMS, and fire a mine if a mob or player is found.
	 */	
	private void launchMine() {
		if(BlockUtils.getBlockPropertyAsInteger(getWorld(), getPos(), BlockIMS.MINES) > 0){
			double d0 = (double) mod_SecurityCraft.configHandler.imsRange;
			
			AxisAlignedBB axisalignedbb = AxisAlignedBB.fromBounds((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).expand(d0, d0, d0);
	        List list1 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
	        List list2 = this.worldObj.getEntitiesWithinAABB(EntityMob.class, axisalignedbb);
	        Iterator iterator1 = list1.iterator();
	        Iterator iterator2 = list2.iterator();	       
	        
	        while(targetingOption == 1 && iterator2.hasNext()){
	        	EntityLivingBase entity = (EntityLivingBase) iterator2.next();
				int launchHeight = this.getLaunchHeight();

				if(WorldUtils.isPathObstructed(worldObj, (double) pos.getX() + 0.5D, (double) pos.getY() + (((launchHeight - 1) / 3) + 0.5D), (double) pos.getZ() + 0.5D, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)){ continue; }

		        double d5 = entity.posX - ((double) pos.getX() + 0.5D);
		        Validate.notNull(entity);
		        Validate.notNull(entity.getEntityBoundingBox());
		        Validate.notNull(entity.height);
		        Validate.notNull(getPos());
		        double d6 = entity.getEntityBoundingBox().minY + (double)(entity.height / 2.0F) - ((double) getPos().getY() + 1.25D);
		        double d7 = entity.posZ - ((double) pos.getZ() + 0.5D);

		        this.spawnMine(entity, d5, d6, d7, launchHeight);
		            
		        if(worldObj.isRemote){
		        	mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(pos.getX(), pos.getY(), pos.getZ(), "random.bow", 1.0F));
		        }
		        
		        BlockUtils.setBlockProperty(getWorld(), getPos(), BlockIMS.MINES, BlockUtils.getBlockPropertyAsInteger(getWorld(), getPos(), BlockIMS.MINES) - 1);
		        
		        if(BlockUtils.getBlockPropertyAsInteger(getWorld(), getPos(), BlockIMS.MINES) == 0){
		        	worldObj.scheduleUpdate(BlockUtils.toPos(pos.getX(), pos.getY(), pos.getZ()), BlockUtils.getBlock(worldObj, pos.getX(), pos.getY(), pos.getZ()), 140);
		        }
		        
		        return;
	        }
	        
	        while(iterator1.hasNext()){
	        	EntityPlayer entity = (EntityPlayer) iterator1.next();
				int launchHeight = this.getLaunchHeight();

	        	if(entity instanceof EntityPlayer && ((EntityPlayer) entity).getName().matches(getOwnerName())){ continue; }
				if(WorldUtils.isPathObstructed(worldObj, (double) pos.getX() + 0.5D, (double) pos.getY() + (((launchHeight - 1) / 3) + 0.5D), (double) pos.getZ() + 0.5D, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)){ continue; }

		        double d5 = entity.posX - ((double) pos.getX() + 0.5D);
		        double d6 = entity.getBoundingBox().minY + (double)(entity.height / 2.0F) - ((double) pos.getY() + 1.25D);
		        double d7 = entity.posZ - ((double) pos.getZ() + 0.5D);
					
		        this.spawnMine((EntityPlayer) entity, d5, d6, d7, launchHeight);
		            
		        if(worldObj.isRemote){
		        	mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(pos.getX(), pos.getY(), pos.getZ(), "random.bow", 1.0F));
		        }
		        
		        BlockUtils.setBlockProperty(getWorld(), getPos(), BlockIMS.MINES, BlockUtils.getBlockPropertyAsInteger(getWorld(), getPos(), BlockIMS.MINES) - 1);
		        
		        if(BlockUtils.getBlockPropertyAsInteger(getWorld(), getPos(), BlockIMS.MINES) == 0){
		        	worldObj.scheduleUpdate(BlockUtils.toPos(pos.getX(), pos.getY(), pos.getZ()), BlockUtils.getBlock(worldObj, pos.getX(), pos.getY(), pos.getZ()), 140);
		        }
	        }
        }
	}
	
    /**
	 * Spawn a mine at the correct position on the IMS model.
	 */
	private void spawnMine(EntityPlayer target, double x, double y, double z, int launchHeight){
		int bombsRemaining = BlockUtils.getBlockPropertyAsInteger(getWorld(), getPos(), BlockIMS.MINES);
		
		if(bombsRemaining == 4){
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, pos.getX() + 1.2D, pos.getY(), pos.getZ() + 1.2D, x, y, z, launchHeight);
			worldObj.spawnEntityInWorld(entitylargefireball);
		}else if(bombsRemaining == 3){
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, pos.getX() + 1.2D, pos.getY(), pos.getZ() + 0.6D, x, y, z, launchHeight);
			worldObj.spawnEntityInWorld(entitylargefireball);
		}else if(bombsRemaining == 2){
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, pos.getX() + 0.55D, pos.getY(), pos.getZ() + 1.2D, x, y, z, launchHeight);
			worldObj.spawnEntityInWorld(entitylargefireball);
		}else if(bombsRemaining == 1){
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, pos.getX() + 0.55D, pos.getY(), pos.getZ() + 0.6D, x, y, z, launchHeight);
			worldObj.spawnEntityInWorld(entitylargefireball);
		}
	}
	
    /**
	 * Spawn a mine at the correct position on the IMS model.
	 */
	private void spawnMine(EntityLivingBase target, double x, double y, double z, int launchHeight){
		int bombsRemaining = BlockUtils.getBlockPropertyAsInteger(getWorld(), getPos(), BlockIMS.MINES);
		
		if(bombsRemaining == 4){
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, pos.getX() + 1.2D, pos.getY(), pos.getZ() + 1.2D, x, y, z, launchHeight);
			worldObj.spawnEntityInWorld(entitylargefireball);
		}else if(bombsRemaining == 3){
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, pos.getX() + 1.2D, pos.getY(), pos.getZ() + 0.6D, x, y, z, launchHeight);
			worldObj.spawnEntityInWorld(entitylargefireball);
		}else if(bombsRemaining == 2){
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, pos.getX() + 0.55D, pos.getY(), pos.getZ() + 1.2D, x, y, z, launchHeight);
			worldObj.spawnEntityInWorld(entitylargefireball);
		}else if(bombsRemaining == 1){
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, pos.getX() + 0.55D, pos.getY(), pos.getZ() + 0.6D, x, y, z, launchHeight);
			worldObj.spawnEntityInWorld(entitylargefireball);
		}
	}
	
	/**
	 * Returns the amount of ticks the {@link EntityIMSBomb} should float in the air before firing at an entity.
	 */
	private int getLaunchHeight() {
		int height;
		
		for(height = 1; height <= 9; height++){
			if(BlockUtils.getBlock(getWorld(), getPos().up(height)) == null || BlockUtils.getBlock(getWorld(), getPos().up(height)) == Blocks.air){
				continue;
			}else{
				break;
			}
		}
		
		return height * 3;
	}

	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound){
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setInteger("targetingOption", targetingOption);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound){
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("targetingOption"))
        {
            this.targetingOption = par1NBTTagCompound.getInteger("targetingOption");
        }
    }

	public int getTargetingOption() {
		return targetingOption;
	}

	public void setTargetingOption(int targetingOption) {
		this.targetingOption = targetingOption;
	}

}
