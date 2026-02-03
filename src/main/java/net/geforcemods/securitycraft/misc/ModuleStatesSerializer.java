package net.geforcemods.securitycraft.misc;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class ModuleStatesSerializer implements EntityDataSerializer<Map<ModuleType, Boolean>> {
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, Map<ModuleType, Boolean>> STREAM_CODEC = ByteBufCodecs.map(
			size -> new EnumMap<>(ModuleType.class),
			NeoForgeStreamCodecs.enumCodec(ModuleType.class),
			ByteBufCodecs.BOOL);
	//@formatter:on

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, Map<ModuleType, Boolean>> codec() {
		return STREAM_CODEC;
	}

	@Override
	public Map<ModuleType, Boolean> copy(Map<ModuleType, Boolean> value) {
		return new EnumMap<>(value);
	}
}
