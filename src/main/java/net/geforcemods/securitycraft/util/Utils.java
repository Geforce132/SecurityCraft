package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class Utils {

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String line){
		if(line == null || line.isEmpty())
			return "";

		return line.substring(0, line.length() - 1);
	}

	public static TextComponentTranslation getFormattedCoordinates(BlockPos pos){
		return new TextComponentTranslation("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	public static void setISinTEAppropriately(World world, BlockPos pos, NonNullList<ItemStack> contents)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		connectedScanner.setContents(contents);
	}
}
