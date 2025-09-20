package net.geforcemods.securitycraft.items.properties;

import com.mojang.serialization.MapCodec;

import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public record KeycardCount() implements RangeSelectItemModelProperty {
	public static final MapCodec<KeycardCount> MAP_CODEC = MapCodec.unit(new KeycardCount());

	@Override
	public float get(ItemStack stack, ClientLevel level, ItemOwner entity, int seed) {
		ItemContainerContents containerContents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		int size = containerContents.getSlots();

		if (size == 0)
			return 0.0F;
		else {
			long cardCount = containerContents.stream().filter(item -> item.getItem() instanceof KeycardItem).count();

			return cardCount / (float) size;
		}
	}

	@Override
	public MapCodec<? extends RangeSelectItemModelProperty> type() {
		return MAP_CODEC;
	}
}
