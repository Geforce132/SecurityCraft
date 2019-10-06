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
	public static String removeLastChar(String s){
		if(s == null || s.isEmpty())
			return "";

		return s.substring(0, s.length() - 1);
	}

	public static String getFormattedCoordinates(BlockPos pos)
	{
		return getFormattedCoordinates(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Returns the given X, Y, and Z coordinates in a nice String, useful for chat messages. <p>
	 *
	 * Args: x, y, z.
	 */
	public static String getFormattedCoordinates(int x, int y, int z){
		return "X: " + x + " Y: " + y + " Z: " + z;
	}

	public static void setISinTEAppropriately(World world, int x, int y, int z, ItemStack[] contents, String type) {
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, x, y, z);

		if(connectedScanner == null)
			return;

		connectedScanner.setContents(contents);
		connectedScanner.setType(type);
	}
}
