package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
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
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ReinforcedNyliumBlock extends BaseReinforcedBlock implements BonemealableBlock {
	public ReinforcedNyliumBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		return level.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		BlockPos upperPos = pos.above();
		ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();
		Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);

		if (state.is(SCContent.REINFORCED_CRIMSON_NYLIUM.get()))
			place(registry, NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, level, chunkGenerator, random, upperPos);
		else if (state.is(SCContent.REINFORCED_WARPED_NYLIUM.get())) {
			place(registry, NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, level, chunkGenerator, random, upperPos);
			place(registry, NetherFeatures.NETHER_SPROUTS_BONEMEAL, level, chunkGenerator, random, upperPos);

			if (random.nextInt(8) == 0)
				place(registry, NetherFeatures.TWISTING_VINES_BONEMEAL, level, chunkGenerator, random, upperPos);
		}
	}

	private void place(Registry<ConfiguredFeature<?, ?>> registry, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, ServerLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos) {
		registry.get(configuredFeatureKey).ifPresent(configuredFeature -> configuredFeature.value().place(level, chunkGenerator, random, pos));
	}
}
