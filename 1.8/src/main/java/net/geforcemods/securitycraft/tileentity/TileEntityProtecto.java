package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockProtecto;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;

public class TileEntityProtecto extends CustomizableSCTE {
	
	private int cooldown = 0;
	
	public void update(){				
		if(cooldown < 200){
			cooldown++;
		}
			
		if(canAttack()){		
		    attack();
		}
		
		if(canAttack() && !isActive()){
			BlockUtils.setBlockProperty(worldObj, pos, BlockProtecto.ACTIVATED, true);
		}else if(!canAttack() && isActive()){
			BlockUtils.setBlockProperty(worldObj, pos, BlockProtecto.ACTIVATED, false);
		}
	}
	
	public void attack(){
		AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).expand(10, 10, 10); 
		List list1 = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
	    Iterator iterator = list1.iterator();
	    
	    boolean attacked = false;
	    
	    while(iterator.hasNext()){		    	
	    	EntityLivingBase entityToBeAttacked = (EntityLivingBase) iterator.next();
	    	
	    	if((entityToBeAttacked instanceof EntityPlayer && BlockUtils.isOwnerOfBlock(this, (EntityPlayer) entityToBeAttacked)) || entityToBeAttacked instanceof EntityPigZombie || (entityToBeAttacked instanceof EntityCreeper && ((EntityCreeper) entityToBeAttacked).getPowered())){
	    		continue;
	    	}
	    	
	    	EntityLightningBolt lightning = new EntityLightningBolt(worldObj, entityToBeAttacked.posX, entityToBeAttacked.posY, entityToBeAttacked.posZ);
	    	worldObj.addWeatherEffect(lightning);
	 
	    	attacked = true;
	    }    
	    
	    if(attacked){
	    	cooldown = 0;
	    }	
	}
	
    public void writeToNBT(NBTTagCompound par1NBTTagCompound){
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setInteger("cooldown", this.cooldown);     
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound){
        super.readFromNBT(par1NBTTagCompound);

        if(par1NBTTagCompound.hasKey("cooldown"))
        {
            this.cooldown = par1NBTTagCompound.getInteger("cooldown");
        }  
    }
	
	public boolean canAttack(){
		return cooldown == 200 && worldObj.canBlockSeeSky(pos) && worldObj.isRaining();
	}
	
	public boolean isActive(){
		return BlockUtils.getBlock(worldObj, pos) == mod_SecurityCraft.protecto && BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockProtecto.ACTIVATED);
	}

	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + "Whitelist module:" + EnumChatFormatting.RESET + "\n\nAdding a whitelist module to a protecto will allow the whitelisted players to walk into the range of the protecto without being targeted."};
	}

}
