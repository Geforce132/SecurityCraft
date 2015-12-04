package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityProtecto extends CustomizableSCTE {
	
	public boolean attackEntity(Entity entity){			
		if(entity instanceof EntityLivingBase) 
		{
			if((entity instanceof EntityPlayer && getOwner().isOwner((EntityPlayer) entity)) || entity instanceof EntityPigZombie || (entity instanceof EntityCreeper && ((EntityCreeper) entity).getPowered())) return false;
	    	
	    	WorldUtils.spawnLightning(worldObj, entity.posX, entity.posY, entity.posZ);
	    	
	    	return true;
		}
		
		return false;
	}
	
	public boolean canAttack() {		
		return getAttackCooldown() == 200 && worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord) && worldObj.isRaining();
	}
	
	public boolean shouldRefreshAttackCooldown(){
		return false;
	}
	
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("item.whitelistModule.name") + ":" + EnumChatFormatting.RESET + StatCollector.translateToLocal("module.description.protecto.whitelist")};
	}

}
