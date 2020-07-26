package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BaseKeycardItem extends Item{

	private static final Style GRAY_STYLE = Style.EMPTY.setFormatting(TextFormatting.GRAY);
	private final int level;

	public BaseKeycardItem(int level) {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical));
		this.level = level;
	}

	public int getKeycardLvl(){
		if(level == 0)
			return 1;
		else if(level == 1)
			return 2;
		else if(level == 2)
			return 3;
		else if(level == 3)
			return 6;
		else if(level == 4)
			return 4;
		else if(level == 5)
			return 5;
		else
			return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if(level == 3){
			if(stack.getTag() == null){
				stack.setTag(new CompoundNBT());
				stack.getTag().putInt("Uses", 5);
			}

			list.add(ClientUtils.localize("tooltip.securitycraft:keycard.uses").append(new StringTextComponent(" " + stack.getTag().getInt("Uses"))).setStyle(GRAY_STYLE));

		}
	}

}
