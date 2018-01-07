package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * SecurityCraft's utility methods are found here. Frequently used or simplified vanilla code can be found here or in other util classes in this package.
 *
 * @author Geforce
 */
public class Utils {

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String par1){
		if(par1 == null || par1.isEmpty())
			return "";

		return par1.substring(0, par1.length() - 1);
	}

	/**
	 * Returns the given X, Y, and Z coordinates in a nice String, useful for chat messages. <p>
	 *
	 * Args: x, y, z.
	 */
	public static String getFormattedCoordinates(int par1, int par2, int par3){
		return "X: " + par1 + " Y: " + par2 + " Z: " + par3;
	}

	public static void setISinTEAppropriately(World par1World, int par2, int par3, int par4, ItemStack[] contents, String type) {
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(par1World, par2, par3, par4);

		connectedScanner.setContents(contents);
		connectedScanner.setType(type);
	}
}
