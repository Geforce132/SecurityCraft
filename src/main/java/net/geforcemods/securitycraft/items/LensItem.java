package net.geforcemods.securitycraft.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LensItem extends Item {
	public LensItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		if (stack.has(DataComponents.DYED_COLOR))
			return Component.translatable("item.securitycraft.colored_lens");
		else
			return super.getName(stack);
	}
}