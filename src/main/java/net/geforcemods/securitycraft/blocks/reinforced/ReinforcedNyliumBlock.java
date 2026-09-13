package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public class ReinforcedNyliumBlock extends BaseReinforcedBlock implements BonemealableBlock {
	public ReinforcedNyliumBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
		return level.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		BlockPos abovePos = pos.above();
		ChunkGenerator generator = level.getChunkSource().getGenerator();
		Registry<Feature> configuredFeatures = level.registryAccess().lookupOrThrow(Registries.FEATURE);

		place(configuredFeatures, NetherFeatures.NYLIUM_BONEMEAL, level, generator, random, abovePos);
	}

	private void place(Registry<Feature> configuredFeatures, ResourceKey<Feature> id, ServerLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
		if (level.isInsideBuildHeight(pos))
			configuredFeatures.get(id).ifPresent(feature -> feature.value().place(level, generator, random, pos));
	}

	@Override
	public BonemealableBlock.Type getType() {
		return BonemealableBlock.Type.NEIGHBOR_SPREADER;
	}
}
