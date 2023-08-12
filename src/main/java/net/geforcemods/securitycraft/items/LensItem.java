package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.DyeItemRecipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class LensItem extends ColorableItem {
	public LensItem() {
		addPropertyOverride(new ResourceLocation(SecurityCraft.MODID, "colored"), (stack, world, entity) -> ((ColorableItem) stack.getItem()).hasColor(stack) ? 1.0F : 0.0F);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			int colorAmount = SecurityCraft.RANDOM.nextInt(3) + 1;
			List<ItemStack> list = new ArrayList<>();

			for (int i = 0; i < colorAmount; i++) {
				list.add(new ItemStack(Items.DYE, 1, SecurityCraft.RANDOM.nextInt(16)));
			}

			items.add(new ItemStack(this));
			items.add(DyeItemRecipe.dyeItem(new ItemStack(this), list));
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (hasColor(stack))
			return "item.securitycraft:colored_lens";
		else
			return super.getTranslationKey(stack);
	}
}