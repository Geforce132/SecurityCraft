package net.geforcemods.securitycraft.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.NullableListCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

public interface GlobalPositionComponent<C, T, E> {
	public static final GlobalPos DUMMY_GLOBAL_POS = new GlobalPos(ResourceKey.create(Registries.DIMENSION, SecurityCraft.resLoc("dummy")), BlockPos.ZERO);

	public static <A> Codec<List<A>> nullableSizedCodec(Codec<A> baseCodec, int size) {
		return new NullableListCodec<>(new Codec<>() {
			@Override
			public <R> DataResult<Pair<A, R>> decode(DynamicOps<R> ops, R input) {
				return input.equals(ops.emptyMap()) ? DataResult.success(Pair.of(null, input)) : baseCodec.decode(ops, input);
			}

			@Override
			public <R> DataResult<R> encode(A input, DynamicOps<R> ops, R prefix) {
				return input == null ? DataResult.success(ops.emptyMap()) : baseCodec.encode(input, ops, prefix);
			}
		}, size, size);
	}

	public static <A> StreamCodec<ByteBuf, List<A>> nullableSizedStreamCodec(StreamCodec<ByteBuf, A> baseStreamCodec, int size, A dummy) {
		//@formatter:off
		return baseStreamCodec.map(
				globalPos -> globalPos.equals(dummy) ? null : globalPos, //decode
				globalPos -> globalPos == null ? dummy : globalPos) //encode
				.apply(ByteBufCodecs.list(size));
		//@formatter:on
	}

	public List<T> positions();

	public boolean isPositionAdded(GlobalPos globalPos);

	public GlobalPos getGlobalPos(T t);

	public T createEntry(GlobalPos globalPos, E extra);

	public void setOnStack(Supplier<DataComponentType<C>> dataComponentType, ItemStack stack, List<T> newPositionList);

	public default int size() {
		return positions().size();
	}

	public default boolean isEmpty() {
		return positions().stream().allMatch(Objects::isNull);
	}

	public default boolean add(Supplier<DataComponentType<C>> dataComponentType, ItemStack stack, GlobalPos globalPos, E extra) {
		if (!isPositionAdded(globalPos)) {
			List<T> newPositionsList = new ArrayList<>(positions());

			for (int i = 0; i < newPositionsList.size(); i++) {
				T t = newPositionsList.get(i);

				if (t == null) {
					newPositionsList.set(i, createEntry(globalPos, extra));
					setOnStack(dataComponentType, stack, newPositionsList);
					return true;
				}
			}
		}

		return false;
	}

	public default boolean remove(Supplier<DataComponentType<C>> dataComponentType, ItemStack stack, GlobalPos globalPos) {
		if (globalPos != null && !isEmpty()) {
			List<T> newPositionsList = new ArrayList<>(positions());

			for (int i = 0; i < newPositionsList.size(); i++) {
				T t = newPositionsList.get(i);

				if (globalPos.equals(getGlobalPos(t))) {
					newPositionsList.set(i, null);
					setOnStack(dataComponentType, stack, newPositionsList);
					return true;
				}
			}
		}

		return false;
	}
}
