package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;

public class TileEntityLaserBlock extends CustomizableSCTE {
	
	public void onModuleInserted(EnumCustomModules module){
		HelpfulMethods.checkInAllDirsAndInsertModule(worldObj, xCoord, yCoord, zCoord, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, module, true);
	}
	
	public void onModuleRemoved(EnumCustomModules module){
		HelpfulMethods.checkInAllDirsAndInsertModule(worldObj, xCoord, yCoord, zCoord, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, null, true);
	}

	protected EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.HARMING, EnumCustomModules.WHITELIST};
	}

}
