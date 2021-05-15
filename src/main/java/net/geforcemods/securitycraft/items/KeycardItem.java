package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeycardItem extends Item
{
	private static final Style GRAY_STYLE = Style.EMPTY.setFormatting(TextFormatting.GRAY);
	private static final ITextComponent LINK_INFO = new TranslationTextComponent("tooltip.securitycraft:keycard.link_info").setStyle(GRAY_STYLE);
	public static final ITextComponent LIMITED_INFO = new TranslationTextComponent("tooltip.securitycraft:keycard.limited_info").setStyle(GRAY_STYLE);
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
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag)
	{
		if(this == SCContent.LIMITED_USE_KEYCARD.get())
			return;

		CompoundNBT tag = stack.getOrCreateTag();

		if(tag.getBoolean("linked"))
		{
			list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.signature", tag.getInt("signature")).setStyle(GRAY_STYLE));
			list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.reader_owner", tag.getString("ownerName")).setStyle(GRAY_STYLE));
		}
		else
			list.add(LINK_INFO);

		if(tag.getBoolean("limited"))
			list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.uses", tag.getInt("uses")).setStyle(GRAY_STYLE));
		else
			list.add(LIMITED_INFO);
	}
}
