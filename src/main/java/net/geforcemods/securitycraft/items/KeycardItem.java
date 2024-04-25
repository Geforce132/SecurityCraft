package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.KeycardData;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

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
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
		if (this != SCContent.LIMITED_USE_KEYCARD.get()) {
			OwnerData ownerData = stack.getOrDefault(SCContent.OWNER_DATA, OwnerData.DEFAULT);
			KeycardData keycardData = stack.get(SCContent.KEYCARD_DATA);

			if (ownerData != null && !ownerData.showInTooltip())
				list.add(Component.translatable("tooltip.securitycraft:keycard.reader_owner", ownerData.name()).setStyle(Utils.GRAY_STYLE));

			if (keycardData != null)
				keycardData.addToTooltip(ctx, list::add, flag);
			else {
				list.add(LINK_INFO);
				list.add(LIMITED_INFO);
			}
		}
	}
}
