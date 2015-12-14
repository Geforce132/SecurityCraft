package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.item.ItemStack;

public class TileEntityLaserBlock extends CustomizableSCTE {
	
	public void onModuleInserted(ItemStack stack, EnumCustomModules module){
		ModuleUtils.checkInAllDirsAndInsertModule(worldObj, pos, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, stack, true);
	}
	
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module){
		ModuleUtils.checkInAllDirsAndRemoveModule(worldObj, pos, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, module, true);
	}

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.HARMING, EnumCustomModules.WHITELIST};
	}

	public Option<?>[] customOptions() {
		return null;
	}

}
