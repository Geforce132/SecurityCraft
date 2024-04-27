package net.geforcemods.securitycraft.components;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.SentryPositions.Entry;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record SentryPositions(List<Entry> positions) implements PositionComponent<Entry, String> {
	public static final int MAX_SENTRIES = 12;
	public static final SentryPositions EMPTY = new SentryPositions(List.of());
	//@formatter:off
	public static final Codec<SentryPositions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Entry.CODEC.sizeLimitedListOf(256).fieldOf("positions").forGetter(SentryPositions::positions))
			.apply(instance, SentryPositions::new));
	public static final StreamCodec<ByteBuf, SentryPositions> STREAM_CODEC = StreamCodec.composite(
			Entry.STREAM_CODEC.apply(ByteBufCodecs.list(256)), SentryPositions::positions,
			SentryPositions::new);
	//@formatter:on

	@Override
	public Entry createEntry(int index, GlobalPos globalPos, String sentryName) {
		return new Entry(index, globalPos, sentryName);
	}

	@Override
	public void setOnStack(ItemStack stack, List<Entry> newPositionList) {
		stack.set(SCContent.SENTRY_POSITIONS, new SentryPositions(newPositionList));
	}

	public record Entry(int index, GlobalPos globalPos, String name) implements PositionEntry {
		//@formatter:off
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.INT.fieldOf("index").forGetter(Entry::index),
						GlobalPos.CODEC.fieldOf("global_pos").forGetter(Entry::globalPos),
						Codec.STRING.fieldOf("name").forGetter(Entry::name))
				.apply(instance, Entry::new));
		public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Entry::index,
				GlobalPos.STREAM_CODEC, Entry::globalPos,
				ByteBufCodecs.STRING_UTF8, Entry::name,
				Entry::new);
		//@formatter:on
	}
}
