package net.geforcemods.securitycraft.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.components.NamedPositions.Entry;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;

public record NamedPositions(List<Entry> positions) implements GlobalPositionComponent<NamedPositions, Entry, Optional<String>> {
	public static final Codec<NamedPositions> codec(int size) {
		//@formatter:off
		return Codec.withAlternative(
			RecordCodecBuilder.create(
					instance -> instance.group(
							GlobalPositionComponent.nullableSizedCodec(Entry.CODEC, size)
							.fieldOf("positions")
							.forGetter(NamedPositions::positions))
					.apply(instance, NamedPositions::new)),
			RecordCodecBuilder.create(
					instance -> instance.group(
							GlobalPositionComponent.nullableSizedCodec(GlobalPos.CODEC, size)
							.fieldOf("positions")
							.forGetter(namedPositions -> namedPositions.positions().stream().map(Entry::oGlobalPos).filter(Optional::isPresent).map(Optional::get).toList()))
					.apply(instance, list -> new NamedPositions(list.stream().map(gp -> gp == null ? null : new Entry(Optional.of(gp), Optional.empty())).toList()))));
		//@formatter:on
	}

	public static final StreamCodec<ByteBuf, NamedPositions> streamCodec(int size) {
		//@formatter:off
		return StreamCodec.composite(
			GlobalPositionComponent.nullableSizedStreamCodec(Entry.STREAM_CODEC, size, new Entry(Optional.of(DUMMY_GLOBAL_POS), Optional.empty())), NamedPositions::positions,
			NamedPositions::new);
		//@formatter:on
	}

	public static NamedPositions sized(int size) {
		return new NamedPositions(Arrays.asList(new Entry[size]));
	}

	@Override
	public boolean isPositionAdded(GlobalPos pos) {
		return positions().stream().filter(Objects::nonNull).map(Entry::globalPos).anyMatch(pos::equals);
	}

	@Override
	public GlobalPos getGlobalPos(Entry entry) {
		return entry == null ? null : entry.oGlobalPos.orElse(null);
	}

	@Override
	public Entry createEntry(GlobalPos globalPos, Optional<String> name) {
		return new Entry(Optional.of(globalPos), name);
	}

	@Override
	public void setOnStack(Supplier<DataComponentType<NamedPositions>> dataComponentType, ItemStack stack, List<Entry> newPositionList) {
		stack.set(dataComponentType, new NamedPositions(newPositionList));
	}

	public static void updateComponentWithNames(Supplier<DataComponentType<NamedPositions>> dataComponentType, ItemStack stack, Function<NamedPositions.Entry, Nameable> nameableGetter) {
		NamedPositions positions = stack.get(dataComponentType);

		if (positions != null && !positions.isEmpty()) {
			List<NamedPositions.Entry> newEntries = new ArrayList<>(positions.positions());
			boolean changed = false;

			for (int i = 0; i < newEntries.size(); i++) {
				NamedPositions.Entry entry = newEntries.get(i);

				if (entry != null) {
					Nameable nameable = nameableGetter.apply(entry);

					if (nameable != null) {
						String name = null;
						Optional<String> optional;

						if (nameable.hasCustomName())
							name = nameable.getCustomName().getString();

						optional = Optional.ofNullable(name);

						if (!optional.equals(entry.name)) {
							newEntries.set(i, new NamedPositions.Entry(entry.oGlobalPos, optional));
							changed = true;
						}
					}
				}
			}

			if (changed)
				stack.set(dataComponentType, new NamedPositions(newEntries));
		}
	}

	public record Entry(Optional<GlobalPos> oGlobalPos, Optional<String> name) {
		public static final Entry EMPTY = new NamedPositions.Entry(Optional.empty(), Optional.empty());
		//@formatter:off
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						GlobalPos.CODEC.optionalFieldOf("global_pos").forGetter(Entry::oGlobalPos),
						Codec.STRING.optionalFieldOf("name").forGetter(Entry::name))
				.apply(instance, Entry::new));
		public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
				GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), Entry::oGlobalPos,
				ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), Entry::name,
				Entry::new);
		//@formatter:on

		public GlobalPos globalPos() {
			return oGlobalPos.orElse(DUMMY_GLOBAL_POS);
		}
	}
}
