package net.geforcemods.securitycraft.components;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record IndexedPositions(List<GlobalPos> positions) implements PositionComponent<IndexedPositions, GlobalPos, Void> {
	public static Codec<IndexedPositions> codec(int size) {
		//@formatter:off
		return RecordCodecBuilder.create(
				instance -> instance.group(PositionComponent.nullableSizedCodec(GlobalPos.CODEC, size).fieldOf("positions").forGetter(IndexedPositions::positions))
				.apply(instance, IndexedPositions::new));
		//@formatter:on
	}

	public static StreamCodec<ByteBuf, IndexedPositions> streamCodec(int size) {
		//@formatter:off
		return StreamCodec.composite(
				PositionComponent.nullableSizedStreamCodec(GlobalPos.STREAM_CODEC, size, DUMMY_GLOBAL_POS), IndexedPositions::positions,
				IndexedPositions::new);
		//@formatter:on
	}

	public static IndexedPositions sized(int size) {
		return new IndexedPositions(Arrays.asList(new GlobalPos[size]));
	}

	@Override
	public boolean isPositionAdded(GlobalPos globalPos) {
		return globalPos != null && positions.contains(globalPos);
	}

	@Override
	public GlobalPos getGlobalPos(GlobalPos globalPos) {
		return globalPos;
	}

	@Override
	public GlobalPos createEntry(GlobalPos globalPos, Void extra) {
		return globalPos;
	}

	@Override
	public void setOnStack(Supplier<DataComponentType<IndexedPositions>> dataComponentType, ItemStack stack, List<GlobalPos> newPositionList) {
		stack.set(dataComponentType, new IndexedPositions(newPositionList));
	}

	public boolean add(Supplier<DataComponentType<IndexedPositions>> boundCameras, ItemStack stack, GlobalPos globalPos) {
		return add(boundCameras, stack, globalPos, null);
	}
}
