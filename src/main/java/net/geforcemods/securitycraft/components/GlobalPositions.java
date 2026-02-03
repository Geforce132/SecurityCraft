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

public record GlobalPositions(List<GlobalPos> positions) implements GlobalPositionComponent<GlobalPositions, GlobalPos, Void> {
	public static Codec<GlobalPositions> codec(int size) {
		//@formatter:off
		return RecordCodecBuilder.create(
				instance -> instance.group(GlobalPositionComponent.nullableSizedCodec(GlobalPos.CODEC, size).fieldOf("positions").forGetter(GlobalPositions::positions))
				.apply(instance, GlobalPositions::new));
		//@formatter:on
	}

	public static StreamCodec<ByteBuf, GlobalPositions> streamCodec(int size) {
		//@formatter:off
		return StreamCodec.composite(
				GlobalPositionComponent.nullableSizedStreamCodec(GlobalPos.STREAM_CODEC, size, DUMMY_GLOBAL_POS), GlobalPositions::positions,
				GlobalPositions::new);
		//@formatter:on
	}

	public static GlobalPositions sized(int size) {
		return new GlobalPositions(Arrays.asList(new GlobalPos[size]));
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
	public void setOnStack(Supplier<DataComponentType<GlobalPositions>> dataComponentType, ItemStack stack, List<GlobalPos> newPositionList) {
		stack.set(dataComponentType, new GlobalPositions(newPositionList));
	}

	public boolean add(Supplier<DataComponentType<GlobalPositions>> boundCameras, ItemStack stack, GlobalPos globalPos) {
		return add(boundCameras, stack, globalPos, null);
	}
}
