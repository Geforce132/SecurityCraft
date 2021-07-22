package net.geforcemods.securitycraft.items;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeycardItem extends Item
{
	private static final Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
	private static final Component LINK_INFO = new TranslatableComponent("tooltip.securitycraft:keycard.link_info").setStyle(GRAY_STYLE);
	public static final Component LIMITED_INFO = new TranslatableComponent("tooltip.securitycraft:keycard.limited_info").setStyle(GRAY_STYLE);
	private final int level; //0-indexed

	public KeycardItem(Item.Properties properties, int level)
	{
		super(properties);
		this.level = level;
	}

	/**
	 * @return 0-indexed level of this keycard. Example: The level 1 keycard will return 0, and the level 5 keycard will return 4
	 */
	public int getLevel()
	{
		return level;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flag)
	{
		if(this == SCContent.LIMITED_USE_KEYCARD.get())
			return;

		CompoundTag tag = stack.getOrCreateTag();

		if(tag.getBoolean("linked"))
		{
			list.add(new TranslatableComponent("tooltip.securitycraft:keycard.signature", StringUtils.leftPad("" + tag.getInt("signature"), 5, "0")).setStyle(GRAY_STYLE));
			list.add(new TranslatableComponent("tooltip.securitycraft:keycard.reader_owner", tag.getString("ownerName")).setStyle(GRAY_STYLE));
		}
		else
			list.add(LINK_INFO);

		if(tag.getBoolean("limited"))
			list.add(new TranslatableComponent("tooltip.securitycraft:keycard.uses", tag.getInt("uses")).setStyle(GRAY_STYLE));
		else
			list.add(LIMITED_INFO);
	}
}
