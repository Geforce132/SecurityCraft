package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LensItem extends Item implements DyeableLeatherItem {
	public static final ResourceLocation COLOR_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "colored");

	public LensItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (allowdedIn(category)) {
			int colorAmount = SecurityCraft.RANDOM.nextInt(1, 4);
			List<DyeItem> list = new ArrayList<>();

			for (int i = 0; i < colorAmount; i++) {
				list.add(DyeItem.byColor(DyeColor.byId(SecurityCraft.RANDOM.nextInt(16))));
			}

			items.add(new ItemStack(this));
			items.add(DyeableLeatherItem.dyeArmor(new ItemStack(this), list));
		}
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		if (hasCustomColor(stack))
			return "item.securitycraft.colored_lens";
		else
			return super.getDescriptionId(stack);
	}
}