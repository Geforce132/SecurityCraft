package net.geforcemods.securitycraft.components;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.components.SentryPositions.Entry;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record SentryPositions(List<Entry> positions) implements GlobalPositionComponent<SentryPositions, Entry, Optional<String>> {
	public static final int MAX_SENTRIES = 12;
	//@formatter:off
	public static final Codec<SentryPositions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(GlobalPositionComponent.nullableSizedCodec(Entry.CODEC, MAX_SENTRIES).fieldOf("positions").forGetter(SentryPositions::positions))
			.apply(instance, SentryPositions::new));
	public static final StreamCodec<ByteBuf, SentryPositions> STREAM_CODEC = StreamCodec.composite(
			GlobalPositionComponent.nullableSizedStreamCodec(Entry.STREAM_CODEC, MAX_SENTRIES, new Entry(DUMMY_GLOBAL_POS, Optional.empty())), SentryPositions::positions,
			SentryPositions::new);
	//@formatter:on
	public static final SentryPositions DEFAULT = sized(MAX_SENTRIES);

	public static SentryPositions sized(int size) {
		return new SentryPositions(Arrays.asList(new Entry[size]));
	}

	@Override
	public boolean isPositionAdded(GlobalPos pos) {
		return positions().stream().filter(Objects::nonNull).map(Entry::globalPos).anyMatch(pos::equals);
	}

	@Override
	public GlobalPos getGlobalPos(Entry entry) {
		return entry == null ? null : entry.globalPos;
	}

	@Override
	public Entry createEntry(GlobalPos globalPos, Optional<String> sentryName) {
		return new Entry(globalPos, sentryName);
	}

	@Override
	public void setOnStack(Supplier<DataComponentType<SentryPositions>> dataComponentType, ItemStack stack, List<Entry> newPositionList) {
		stack.set(dataComponentType, new SentryPositions(newPositionList));
	}

	public record Entry(GlobalPos globalPos, Optional<String> name) {
		//@formatter:off
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						GlobalPos.CODEC.fieldOf("global_pos").forGetter(Entry::globalPos),
						Codec.STRING.optionalFieldOf("name").forGetter(Entry::name))
				.apply(instance, Entry::new));
		public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
				GlobalPos.STREAM_CODEC, Entry::globalPos,
				ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), Entry::name,
				Entry::new);
		//@formatter:on
	}
}
