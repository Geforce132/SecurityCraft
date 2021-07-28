package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Utils {

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String line){
		if(line == null || line.isEmpty())
			return "";

		return line.substring(0, line.length() - 1);
	}

	public static TranslatableComponent getFormattedCoordinates(BlockPos pos){
		return new TranslatableComponent("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	public static void setISinTEAppropriately(Level world, BlockPos pos, NonNullList<ItemStack> contents)
	{
		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		connectedScanner.setContents(contents);
	}

	/**
	 * Localizes a String with the given format
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static TranslatableComponent localize(String key, Object... params)
	{
		for(int i = 0; i < params.length; i++)
		{
			if(params[i] instanceof TranslatableComponent component)
				params[i] = localize(component.getKey(), component.getArgs());
			else if(params[i] instanceof BlockPos pos)
				params[i] = getFormattedCoordinates(pos);
		}

		return new TranslatableComponent(key, params);
	}
}
