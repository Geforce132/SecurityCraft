package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.util.EnumChatFormatting;


public class TileEntityRetinalScanner extends CustomizableSCTE {

	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + "Whitelist module:" + EnumChatFormatting.RESET + "\n\nAdding the whitelist module to a retinal scanner will allow players to use the scanner even if they aren't the player who placed down the block."};
	}

}
