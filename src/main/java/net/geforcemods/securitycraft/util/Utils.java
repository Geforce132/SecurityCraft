package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class Utils {

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String string){
		if(string == null || string.isEmpty())
			return "";

		return string.substring(0, string.length() - 1);
	}

	public static String getFormattedCoordinates(BlockPos pos){
		return "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
	}

	public static void setISinTEAppropriately(World world, BlockPos pos, ItemStack[] contents, String type)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		connectedScanner.setContents(contents);
		connectedScanner.setType(type);
	}
}
