package net.geforcemods.securitycraft.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CodebreakerData(long lastUsedTime, boolean wasSuccessful) {
	public static final CodebreakerData DEFAULT = new CodebreakerData(0L, true);
	//@formatter:off
	public static final Codec<CodebreakerData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.LONG.fieldOf("last_used_time").forGetter(CodebreakerData::lastUsedTime),
					Codec.BOOL.fieldOf("was_successful").forGetter(CodebreakerData::wasSuccessful))
			.apply(instance, CodebreakerData::new));
	public static final StreamCodec<ByteBuf, CodebreakerData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, CodebreakerData::lastUsedTime,
			ByteBufCodecs.BOOL, CodebreakerData::wasSuccessful,
			CodebreakerData::new);
	//@formatter:on

	public boolean wasRecentlyUsed() {
		return lastUsedTime != 0 && System.currentTimeMillis() - lastUsedTime < 3000L;
	}
}
