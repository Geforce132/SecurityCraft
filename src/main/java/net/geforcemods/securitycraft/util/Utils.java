package net.geforcemods.securitycraft.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class Utils {
	private Utils() {}

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String line) {
		if (line == null || line.isEmpty())
			return "";

		return line.substring(0, line.length() - 1);
	}

	public static TextComponentTranslation getFormattedCoordinates(BlockPos pos) {
		return new TextComponentTranslation("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Localizes the translation key of a block with the given format
	 *
	 * @param block The block to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String à la String.format
	 * @return The localized String
	 */
	public static TextComponentTranslation localize(Block block, Object... params) {
		return localize(block.getTranslationKey() + ".name", params);
	}

	/**
	 * Localizes the translation key of an item with the given format
	 *
	 * @param item The item to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String à la String.format
	 * @return The localized String
	 */
	public static TextComponentTranslation localize(Item item, Object... params) {
		return localize(item.getTranslationKey() + ".name", params);
	}

	/**
	 * Localizes a String with the given format
	 *
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static TextComponentTranslation localize(String key, Object... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof BlockPos)
				params[i] = getFormattedCoordinates((BlockPos) params[i]);
			else if (params[i] instanceof Block)
				params[i] = new TextComponentTranslation(((Block) params[i]).getTranslationKey() + ".name");
		}

		return new TextComponentTranslation(key, params);
	}
}
