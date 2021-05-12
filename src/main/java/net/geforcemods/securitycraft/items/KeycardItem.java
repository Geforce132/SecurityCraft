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
	private final int level; //0-indexed

	public KeycardItem(Item.Properties properties, int level)
	{
		super(properties);
		this.level = level;
	}

	public boolean isLimitedUseKeycard()
	{
		return this == SCContent.LIMITED_USE_KEYCARD.get();
	}

	public int getLevel()
	{
		return level;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag)
	{
		CompoundNBT tag = stack.getOrCreateTag();

		if(tag.getBoolean("linked"))
		{
			list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.signature", tag.getInt("signature")).setStyle(GRAY_STYLE));
			list.add(new TranslationTextComponent("tooltip.securitycraft:keycard.reader_owner", tag.getString("ownerName")).setStyle(GRAY_STYLE));
		}
	}
}
