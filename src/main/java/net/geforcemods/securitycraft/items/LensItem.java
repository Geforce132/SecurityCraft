package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LensItem extends Item {
	public static final ResourceLocation COLOR_PROPERTY = SecurityCraft.resLoc("colored");

	public LensItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		if (stack.has(DataComponents.DYED_COLOR))
			return "item.securitycraft.colored_lens";
		else
			return super.getDescriptionId(stack);
	}
}