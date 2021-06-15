package net.geforcemods.securitycraft.items;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemKeycard extends Item
{
	private static final ITextComponent LINK_INFO = Utils.localize("tooltip.securitycraft:keycard.link_info");
	private static final ITextComponent LIMITED_INFO = Utils.localize("tooltip.securitycraft:keycard.limited_info");
	private final int level; //0-indexed

	public ItemKeycard(int level)
	{
		this.level = level;
		setCreativeTab(SecurityCraft.tabSCTechnical);
	}

	/**
	 * @return 0-indexed level of this keycard. Example: The level 1 keycard will return 0, and the level 5 keycard will return 4
	 */
	public int getLevel()
	{
		return level;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		if(this == SCContent.limitedUseKeycard)
			return;

		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		NBTTagCompound tag = stack.getTagCompound();

		if(tag.getBoolean("linked"))
		{
			list.add(Utils.localize("tooltip.securitycraft:keycard.signature", StringUtils.leftPad("" + tag.getInteger("signature"), 5, "0")).setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText());
			list.add(Utils.localize("tooltip.securitycraft:keycard.reader_owner", tag.getString("ownerName")).setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText());
		}
		else
			list.add(LINK_INFO.getFormattedText());

		if(tag.getBoolean("limited"))
			list.add(Utils.localize("tooltip.securitycraft:keycard.uses", tag.getInteger("uses")).setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText());
		else
			list.add(LIMITED_INFO.getFormattedText());
	}
}
