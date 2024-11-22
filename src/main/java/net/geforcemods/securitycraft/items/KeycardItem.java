package net.geforcemods.securitycraft.items;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeycardItem extends Item {
	private static final ITextComponent LINK_INFO = new TranslationTextComponent("tooltip.securitycraft:keycard.link_info").setStyle(Utils.GRAY_STYLE);
	public static final ITextComponent LIMITED_INFO = new TranslationTextComponent("tooltip.securitycraft:keycard.limited_info").setStyle(Utils.GRAY_STYLE);
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
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World level, List<ITextComponent> list, ITooltipFlag flag) {
		if (this == SCContent.LIMITED_USE_KEYCARD.get())
			return;

		CompoundNBT tag = stack.getOrCreateTag();

		if (tag.getBoolean("linked")) {
			String usableBy = tag.getString("usable_by");

			list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.signature", StringUtils.leftPad("" + tag.getInt("signature"), 5, "0")).setStyle(Utils.GRAY_STYLE));
			list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.reader_owner", tag.getString("ownerName")).setStyle(Utils.GRAY_STYLE));

			if (!usableBy.isEmpty())
				list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.usable_by", new StringTextComponent(usableBy)).setStyle(Utils.GRAY_STYLE));
			else
				list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.everyone").setStyle(Utils.GRAY_STYLE));
		}
		else
			list.add(LINK_INFO);

		if (tag.getBoolean("limited"))
			list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.uses", tag.getInt("uses")).setStyle(Utils.GRAY_STYLE));
		else
			list.add(LIMITED_INFO);
	}
}
