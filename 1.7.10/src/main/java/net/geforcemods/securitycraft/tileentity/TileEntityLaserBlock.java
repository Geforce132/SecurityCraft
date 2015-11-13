package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.main.Utils.ModuleUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityLaserBlock extends CustomizableSCTE {
	
	public void onModuleInserted(ItemStack stack, EnumCustomModules module){
		ModuleUtils.checkInAllDirsAndInsertModule(worldObj, xCoord, yCoord, zCoord, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, stack, true);
	}
	
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module){
		ModuleUtils.checkInAllDirsAndRemoveModule(worldObj, xCoord, yCoord, zCoord, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, module, true);
	}

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.HARMING, EnumCustomModules.WHITELIST};
	}
	
	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("item.harmingModule.name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal("module.description.laser.harming"), 
				EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("item.whitelistModule.name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal("module.description.laser.whitelist")};
	}
}
