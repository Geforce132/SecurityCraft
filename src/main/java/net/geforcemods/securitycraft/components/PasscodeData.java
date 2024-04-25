package net.geforcemods.securitycraft.components;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;

public record PasscodeData(String passcode, UUID saltKey) {
	//@formatter:off
	public static final Codec<PasscodeData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.STRING.fieldOf("passcode").forGetter(PasscodeData::passcode),
					UUIDUtil.CODEC.fieldOf("salt_key").forGetter(PasscodeData::saltKey))
			.apply(instance, PasscodeData::new));
	//@formatter:on
}
