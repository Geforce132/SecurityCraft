package net.geforcemods.securitycraft.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCStreamCodecs;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record SavedBlockState(BlockState state, StandingOrWallType standingOrWallType) {
	public static final SavedBlockState EMPTY = new SavedBlockState(Blocks.AIR.defaultBlockState(), StandingOrWallType.NONE);
	//@formatter:off
	public static final Codec<SavedBlockState> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BlockState.CODEC.fieldOf("state").forGetter(SavedBlockState::state),
					StringRepresentable.fromEnum(StandingOrWallType::values).fieldOf("standing_or_wall_type").forGetter(SavedBlockState::standingOrWallType))
			.apply(instance, SavedBlockState::new));
	public static final StreamCodec<FriendlyByteBuf, SavedBlockState> STREAM_CODEC = StreamCodec.composite(
			SCStreamCodecs.BLOCK_STATE, SavedBlockState::state,
			NeoForgeStreamCodecs.enumCodec(StandingOrWallType.class), SavedBlockState::standingOrWallType,
			SavedBlockState::new);
	//@formatter:on
}
