package net.geforcemods.securitycraft.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class Utils {
	public static final Style GRAY_STYLE = Style.EMPTY.withColor(TextFormatting.GRAY);
	public static final ITextComponent INVENTORY_TEXT = Utils.localize("container.inventory");

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String line) {
		if (line == null || line.isEmpty())
			return "";

		return line.substring(0, line.length() - 1);
	}

	public static TranslationTextComponent getFormattedCoordinates(BlockPos pos) {
		return new TranslationTextComponent("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Localizes a String with the given format
	 *
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static TranslationTextComponent localize(String key, Object... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof TranslationTextComponent)
				params[i] = localize(((TranslationTextComponent) params[i]).getKey(), ((TranslationTextComponent) params[i]).getArgs());
			else if (params[i] instanceof BlockPos)
				params[i] = getFormattedCoordinates((BlockPos) params[i]);
		}

		return new TranslationTextComponent(key, params);
	}
}
