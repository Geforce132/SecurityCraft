package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;

public class OwnerDataSerializer implements EntityDataSerializer<Owner> {
	@Override
	public void write(FriendlyByteBuf buf, Owner value) {
		buf.writeUtf(value.getName());
		buf.writeUtf(value.getUUID());
	}

	@Override
	public Owner read(FriendlyByteBuf buf) {
		String name = buf.readUtf(Integer.MAX_VALUE / 4);
		String uuid = buf.readUtf(Integer.MAX_VALUE / 4);

		return new Owner(name, uuid);
	}

	@Override
	public EntityDataAccessor<Owner> createAccessor(int id) {
		return new EntityDataAccessor<>(id, this);
	}

	@Override
	public Owner copy(Owner value) {
		return new Owner(value.getName(), value.getUUID());
	}
}
