package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LensItem extends Item implements DyeableLeatherItem {
	public static final ResourceLocation COLOR_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "colored");

	public LensItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		if (hasCustomColor(stack))
			return "item.securitycraft.colored_lens";
		else
			return super.getDescriptionId(stack);
	}
}