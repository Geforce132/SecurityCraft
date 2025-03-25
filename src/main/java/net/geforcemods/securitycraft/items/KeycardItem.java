package net.geforcemods.securitycraft.items;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.KeycardData;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class KeycardItem extends Item {
	private static final Component LINK_INFO = Component.translatable("tooltip.securitycraft:keycard.link_info").setStyle(Utils.GRAY_STYLE);
	public static final Component LIMITED_INFO = Component.translatable("tooltip.securitycraft:keycard.limited_info").setStyle(Utils.GRAY_STYLE);
	private final int level; //0-indexed

	public KeycardItem(Item.Properties properties, int level) {
		super(properties);
		this.level = level;
	}

	/**
	 * @return 0-indexed level of this keycard. Example: The level 1 keycard will return 0, and the level 5 keycard will return 4
	 */
	public int getLevel() {
		return level;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		if (this != SCContent.LIMITED_USE_KEYCARD.get()) {
			DataComponentType<KeycardData> type = SCContent.KEYCARD_DATA.get();
			KeycardData keycardData = stack.get(type);

			if (keycardData != null) {
				if (display.shows(type))
					keycardData.addToTooltip(ctx, tooltipAdder, flag, stack.getComponents());
			}
			else {
				tooltipAdder.accept(LINK_INFO);
				tooltipAdder.accept(LIMITED_INFO);
			}
		}
	}
}
