package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.item.ItemStack;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;

public class TileEntityLaserBlock extends CustomizableSCTE {
	
	public void onModuleInserted(ItemStack stack, EnumCustomModules module){
		HelpfulMethods.checkInAllDirsAndInsertModule(worldObj, pos, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, module, true);
	}
	
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module){
		HelpfulMethods.checkInAllDirsAndRemoveModule(worldObj, pos, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, module, true);
	}

	protected EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.HARMING, EnumCustomModules.WHITELIST};
	}

}
