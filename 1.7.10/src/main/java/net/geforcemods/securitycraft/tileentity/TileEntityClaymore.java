package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityClaymore extends TileEntitySCTE{

	private double entityX = -1D;
	private double entityY = -1D;
	private double entityZ = -1D;
	private int cooldown = -1;
	
	public void updateEntity() {
		if(getWorldObj().isRemote){
			return;
		}else{		
			if(getWorldObj().getBlock(xCoord, yCoord, zCoord) == mod_SecurityCraft.claymoreDefused){
				return;
			}
			
			if(cooldown > 0){
				cooldown--;
				return;
			}
			
			if(cooldown == 0){
				BlockUtils.destroyBlock(getWorldObj(), xCoord, yCoord, zCoord, false);
				getWorldObj().createExplosion((Entity) null, entityX, entityY + 0.5F, entityZ, 3.5F, true);
				return;
			}
			
			int meta = getWorldObj().getBlockMetadata(xCoord, yCoord, zCoord);
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double) xCoord, (double) yCoord, (double) zCoord, (double)(xCoord + 1), (double)(yCoord + 1), (double)(zCoord + 1));
			
			if(meta == 3){
				axisalignedbb = axisalignedbb.addCoord(0, 0, -mod_SecurityCraft.configHandler.claymoreRange);
			}else if(meta == 1){
				axisalignedbb = axisalignedbb.addCoord(0, 0, mod_SecurityCraft.configHandler.claymoreRange);
			}else if(meta == 2){
				axisalignedbb = axisalignedbb.addCoord(mod_SecurityCraft.configHandler.claymoreRange, 0, 0);
			}else if(meta == 4){
				axisalignedbb = axisalignedbb.addCoord(-mod_SecurityCraft.configHandler.claymoreRange, 0, 0);
			}
			
			List list = getWorldObj().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
			Iterator iterator = list.iterator();
			EntityLivingBase entityliving;
			
			while(iterator.hasNext()){
				entityliving = (EntityLivingBase) iterator.next();
				
				entityX = entityliving.posX;
				entityY = entityliving.posY;
				entityZ = entityliving.posZ;
				cooldown = 20;
				
				getWorldObj().playSoundEffect((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D, "random.click", 0.3F, 0.6F);
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
