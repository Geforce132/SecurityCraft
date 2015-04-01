package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.freeforums.geforce.securitycraft.enums.EnumCustomModules;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class TileEntityLaserBlock extends CustomizableSCTE {
	
	public void onModuleInserted(ItemStack stack, EnumCustomModules module){
		HelpfulMethods.checkInAllDirsAndInsertModule(worldObj, xCoord, yCoord, zCoord, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, stack, true);
	}
	
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module){
		HelpfulMethods.checkInAllDirsAndRemoveModule(worldObj, xCoord, yCoord, zCoord, mod_SecurityCraft.LaserBlock, mod_SecurityCraft.configHandler.laserBlockRange, module, true);
	}

	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.HARMING, EnumCustomModules.WHITELIST};
	}
	
	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + "Harming module:" + EnumChatFormatting.RESET + "\n\nAdding a harming module to a laser block will cause players to take damage if they step through the lasers.", EnumChatFormatting.UNDERLINE + "Whitelist module:" + EnumChatFormatting.RESET + "\n\nAdding a whitelist module to a laser block will allow players to walk through the lasers without emitting a redstone signal."};
	}

}
