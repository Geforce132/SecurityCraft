package net.geforcemods.securitycraft.misc;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.ItemStack;

public class ItemStackListSerializer implements EntityDataSerializer<NonNullList<ItemStack>> {
	public static final StreamCodec<? super RegistryFriendlyByteBuf, NonNullList<ItemStack>> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity));

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, NonNullList<ItemStack>> codec() {
		return STREAM_CODEC;
	}

	@Override
	public NonNullList<ItemStack> copy(NonNullList<ItemStack> value) {
		NonNullList<ItemStack> copy = NonNullList.withSize(value.size(), ItemStack.EMPTY);

		for (int i = 0; i < value.size(); i++) {
			copy.set(i, value.get(i));
		}

		return copy;
	}
}
