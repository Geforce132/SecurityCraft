package net.geforcemods.securitycraft.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;

public class Utils {
	public static final Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
	public static final Component INVENTORY_TEXT = Utils.localize("container.inventory");

	private Utils() {}

	public static TranslatableComponent getFormattedCoordinates(BlockPos pos) {
		return new TranslatableComponent("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Localizes a String with the given format
	 *
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static TranslatableComponent localize(String key, Object... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof TranslatableComponent component)
				params[i] = localize(component.getKey(), component.getArgs());
			else if (params[i] instanceof BlockPos pos)
				params[i] = getFormattedCoordinates(pos);
		}

		return new TranslatableComponent(key, params);
	}
}
