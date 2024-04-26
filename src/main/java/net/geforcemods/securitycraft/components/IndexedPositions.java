package net.geforcemods.securitycraft.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record IndexedPositions(List<Entry> positions) {
	public static final int MAX_CAMERAS = 30;
	public static final IndexedPositions EMPTY = new IndexedPositions(List.of());
	//@formatter:off
	public static final Codec<IndexedPositions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Entry.CODEC.sizeLimitedListOf(256).fieldOf("positions").forGetter(IndexedPositions::positions))
			.apply(instance, IndexedPositions::new));
	public static final StreamCodec<ByteBuf, IndexedPositions> STREAM_CODEC = StreamCodec.composite(
			Entry.STREAM_CODEC.apply(ByteBufCodecs.list(256)), IndexedPositions::positions,
			IndexedPositions::new);
	//@formatter:on

	public int size() {
		return positions.size();
	}

	public boolean hasPositionAdded() {
		return !positions.isEmpty();
	}

	public boolean isPositionAdded(GlobalPos pos) {
		return positions.stream().map(Entry::globalPos).anyMatch(pos::equals);
	}

	public List<Entry> filledOrderedList() {
		List<Entry> sortedPositions = new ArrayList<>(positions);
		List<Entry> toReturn = new ArrayList<>();
		int indexToCheck = 0;

		sortedPositions.sort(Comparator.comparing(c -> c.index));

		for (int i = 1; i <= 30; i++) {
			if (indexToCheck >= sortedPositions.size())
				toReturn.add(null);
			else {
				Entry existingPosition = sortedPositions.get(indexToCheck);

				if (existingPosition.index() != i)
					toReturn.add(null);
				else {
					toReturn.add(existingPosition);
					indexToCheck++;
				}
			}
		}

		return toReturn;
	}

	public static boolean add(ItemStack stack, IndexedPositions positions, GlobalPos view, int maximum) {
		if (positions != null && positions.positions.size() < maximum) {
			List<Entry> sortedPositions = positions.positions.stream().sorted(Comparator.comparing(c -> c.index)).toList();
			int nextFreeIndex = 0;

			for (int i = 1; i <= maximum; i++) {
				if (i > sortedPositions.size() || sortedPositions.get(i - 1).index != i) {
					nextFreeIndex = i;
					break;
				}
			}

			if (nextFreeIndex > 0) {
				List<Entry> newPositionList = new ArrayList<>(positions.positions);
				IndexedPositions newPositions;

				newPositionList.add(new Entry(nextFreeIndex, view));
				newPositions = new IndexedPositions(newPositionList);
				stack.set(SCContent.INDEXED_POSITIONS, newPositions);
				return true;
			}
		}

		return false;
	}

	public static boolean remove(ItemStack stack, IndexedPositions positions, GlobalPos pos) {
		if (positions != null && positions.hasPositionAdded()) {
			List<Entry> newPositionList = new ArrayList<>(positions.positions);

			newPositionList.removeIf(position -> position.globalPos.equals(pos));

			if (newPositionList.size() != positions.positions.size()) {
				stack.set(SCContent.INDEXED_POSITIONS, new IndexedPositions(newPositionList));
				return true;
			}
		}

		return false;
	}

	public record Entry(int index, GlobalPos globalPos) {
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
