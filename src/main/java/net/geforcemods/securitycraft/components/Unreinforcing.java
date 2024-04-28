package net.geforcemods.securitycraft.components;

import com.mojang.serialization.Codec;

public record Unreinforcing() {
	public static final Codec<Unreinforcing> CODEC = Codec.unit(Unreinforcing::new);
}
