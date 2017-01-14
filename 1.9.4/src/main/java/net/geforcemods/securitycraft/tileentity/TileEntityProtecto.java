package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockProtecto;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityProtecto extends CustomizableSCTE {
	
	public boolean attackEntity(Entity entity){	
		if (entity instanceof EntityLivingBase) {
	    	if ((entity instanceof EntityPlayer && (getOwner().isOwner((EntityPlayer) entity) || (hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(worldObj, pos, EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getName().toLowerCase())))) ||
	    			entity instanceof EntityPigZombie ||
	    			(entity instanceof EntityCreeper && ((EntityCreeper) entity).getPowered())) {
	    		return false;
	    	}
	    	
	    	EntityLightningBolt lightning = new EntityLightningBolt(worldObj, entity.posX, entity.posY, entity.posZ, true);
	    	worldObj.addWeatherEffect(lightning);
	    	
	    	BlockUtils.setBlockProperty(worldObj, pos, BlockProtecto.ACTIVATED, false);
	    	return true;
		}
		
		return false;
	}
	
	public boolean canAttack() {	
		boolean canAttack = (getAttackCooldown() == 200 && worldObj.canBlockSeeSky(pos) && worldObj.isRaining());

        if(canAttack && !BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockProtecto.ACTIVATED))
        {
        	BlockUtils.setBlockProperty(worldObj, pos, BlockProtecto.ACTIVATED, true);
        }
        else if(!canAttack && BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockProtecto.ACTIVATED))
        {
        	BlockUtils.setBlockProperty(worldObj, pos, BlockProtecto.ACTIVATED, false);
        }
        
        return canAttack;
	}
	
	public boolean shouldRefreshAttackCooldown() {
    	return false;
    }

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	public Option<?>[] customOptions() {
		return null;
	}
}
