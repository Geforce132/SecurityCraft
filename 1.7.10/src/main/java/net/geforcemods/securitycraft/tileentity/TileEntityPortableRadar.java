package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockPortableRadar;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityPortableRadar extends CustomizableSCTE {
	
	private OptionInt searchRadiusOption = new OptionInt("searchRadius", mod_SecurityCraft.configHandler.portableRadarSearchRadius, 5, 50, 5);
	private OptionInt searchDelayOption = new OptionInt("searchDelay", mod_SecurityCraft.configHandler.portableRadarDelay, 4, 10, 1);
		
	//Using TileEntitySCTE.attacks() and the attackEntity() method to check for players. :3
    public boolean attackEntity(Entity entity) {
    	if (entity instanceof EntityPlayer) {
    		BlockPortableRadar.searchForPlayers(worldObj, xCoord, yCoord, zCoord, searchRadiusOption.asInteger());
    		return false;
    	} else {
    		return false;
    	}
    }
    
    public boolean canAttack() {
    	return true;
    }
    
    public boolean shouldSyncToClient() {
    	return false;
    }
    
    public double getAttackRange() {
    	return (double) searchRadiusOption.asInteger();
    }
    
    public int getTicksBetweenAttacks() {
    	return searchDelayOption.asInteger() * 20;
    }
    
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.REDSTONE, EnumCustomModules.WHITELIST};
	}

	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption };
	}
	
}
