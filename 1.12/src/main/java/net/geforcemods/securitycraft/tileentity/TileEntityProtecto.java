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
	
	@Override
	public boolean attackEntity(Entity entity){	
		System.out.println(1);
		if (entity instanceof EntityLivingBase) {	
			System.out.println(2);
	    	if ((entity instanceof EntityPlayer && (getOwner().isOwner((EntityPlayer) entity) || (hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getName().toLowerCase())))) ||
	    			entity instanceof EntityPigZombie ||
	    			(entity instanceof EntityCreeper && ((EntityCreeper) entity).getPowered())) {	
	    		System.out.println(3);
	    		return false;
	    	}
	    	
			System.out.println(4);
	    	EntityLightningBolt lightning = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, true);	
			System.out.println(5);
	    	world.addWeatherEffect(lightning);	
			System.out.println(6);
	    	
	    	BlockUtils.setBlockProperty(world, pos, BlockProtecto.ACTIVATED, false);	
			System.out.println(7);
	    	return true;
		}
		
		System.out.println(8);
		return false;
	}
	
	@Override
	public boolean canAttack() {	
		boolean canAttack = (getAttackCooldown() == 200 && world.canBlockSeeSky(pos) && world.isRaining());

        if(canAttack && !BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockProtecto.ACTIVATED))
        {
        	BlockUtils.setBlockProperty(world, pos, BlockProtecto.ACTIVATED, true);
        }
        else if(!canAttack && BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockProtecto.ACTIVATED))
        {
        	BlockUtils.setBlockProperty(world, pos, BlockProtecto.ACTIVATED, false);
        }
        
        return canAttack;
	}
	
	@Override
	public boolean shouldRefreshAttackCooldown() {
    	return false;
    }

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
