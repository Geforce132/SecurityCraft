package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

public class OwnerDataSerializer implements EntityDataSerializer<Owner> {
	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, Owner> codec() {
		return Owner.STREAM_CODEC;
	}

	@Override
	public Owner copy(Owner value) {
		return new Owner(value.getName(), value.getUUID());
	}
}
