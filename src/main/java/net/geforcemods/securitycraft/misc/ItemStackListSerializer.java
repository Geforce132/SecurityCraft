package net.geforcemods.securitycraft.misc;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.ItemStack;

public class ItemStackListSerializer implements EntityDataSerializer<NonNullList<ItemStack>> {
	@Override
	public void write(FriendlyByteBuf buf, NonNullList<ItemStack> value) {
		buf.writeVarInt(value.size());
		value.forEach(buf::writeItem);
	}

	@Override
	public NonNullList<ItemStack> read(FriendlyByteBuf buf) {
		int size = buf.readVarInt();
		NonNullList<ItemStack> modules = NonNullList.withSize(size, ItemStack.EMPTY);

		for (int i = 0; i < size; i++) {
			modules.set(i, buf.readItem());
		}

		return modules;
	}

	@Override
	public EntityDataAccessor<NonNullList<ItemStack>> createAccessor(int id) {
		return new EntityDataAccessor<>(id, this);
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
