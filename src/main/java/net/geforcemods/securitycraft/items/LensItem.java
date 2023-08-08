package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class LensItem extends Item implements IDyeableArmorItem {
	public static final ResourceLocation COLOR_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "colored");

	public LensItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public void fillItemCategory(ItemGroup category, NonNullList<ItemStack> items) {
		if (allowdedIn(category)) {
			int colorAmount = SecurityCraft.RANDOM.nextInt(3) + 1;
			List<DyeItem> list = new ArrayList<>();

			for (int i = 0; i < colorAmount; i++) {
				list.add(DyeItem.byColor(DyeColor.byId(SecurityCraft.RANDOM.nextInt(16))));
			}

			items.add(new ItemStack(this));
			items.add(IDyeableArmorItem.dyeArmor(new ItemStack(this), list));
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