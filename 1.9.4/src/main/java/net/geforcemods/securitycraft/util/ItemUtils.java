package net.geforcemods.securitycraft.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUtils {
	
	/**
	 * Converts an Item into a ItemStack.
	 * 
	 * @param item Item to convert
	 * @return The new ItemStack
	 */
	public static ItemStack toItemStack(Item item) {
		return new ItemStack(item, 1);
	}
	
	/**
	 * Converts an Item into a ItemStack.
	 * 
	 * @param item Item to convert
	 * @param meta Item metadata
	 * @return The new ItemStack
	 */
	public static ItemStack toItemStack(Item item, int meta) {
		return new ItemStack(item, 1, meta);
	}
}
