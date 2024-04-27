package net.geforcemods.securitycraft.components;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.IndexedPositions.Entry;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record IndexedPositions(List<Entry> positions) implements PositionComponent<Entry, Void> {
	public static final int MAX_CAMERAS = 30;
	public static final int MAX_MINES = 6;
	public static final int MAX_SSS_LINKED_BLOCKS = 30;
	public static final IndexedPositions EMPTY = new IndexedPositions(List.of());
	//@formatter:off
	public static final Codec<IndexedPositions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Entry.CODEC.sizeLimitedListOf(256).fieldOf("positions").forGetter(IndexedPositions::positions))
			.apply(instance, IndexedPositions::new));
	public static final StreamCodec<ByteBuf, IndexedPositions> STREAM_CODEC = StreamCodec.composite(
			Entry.STREAM_CODEC.apply(ByteBufCodecs.list(256)), IndexedPositions::positions,
			IndexedPositions::new);
	//@formatter:on

	@Override
	public Entry createEntry(int index, GlobalPos globalPos, Void extra) {
		return new Entry(index, globalPos);
	}

	@Override
	public void setOnStack(ItemStack stack, List<Entry> newPositionList) {
		stack.set(SCContent.INDEXED_POSITIONS, new IndexedPositions(newPositionList));
	}

	public boolean add(ItemStack stack, GlobalPos globalPos, int maximum) {
		return add(stack, globalPos, maximum, null);
	}

	public record Entry(int index, GlobalPos globalPos) implements PositionEntry {
		//@formatter:off
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.INT.fieldOf("index").forGetter(Entry::index),
						GlobalPos.CODEC.fieldOf("global_pos").forGetter(Entry::globalPos))
				.apply(instance, Entry::new));
		public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Entry::index,
				GlobalPos.STREAM_CODEC, Entry::globalPos,
				Entry::new);
		//@formatter:on
	}
}
