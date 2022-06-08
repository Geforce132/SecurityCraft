package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class ReinforcedNyliumBlock extends BaseReinforcedBlock implements BonemealableBlock {
	public ReinforcedNyliumBlock(Block.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean flag) {
		return level.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState blockState) {
		BlockState state = level.getBlockState(pos);
		BlockPos upperPos = pos.above();
		ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();

		if (state.is(SCContent.REINFORCED_CRIMSON_NYLIUM.get()))
			NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL.value().place(level, chunkGenerator, random, upperPos);
		else if (state.is(SCContent.REINFORCED_WARPED_NYLIUM.get())) {
			NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL.value().place(level, chunkGenerator, random, upperPos);
			NetherFeatures.NETHER_SPROUTS_BONEMEAL.value().place(level, chunkGenerator, random, upperPos);

			if (random.nextInt(8) == 0)
				NetherFeatures.TWISTING_VINES_BONEMEAL.value().place(level, chunkGenerator, random, upperPos);
		}
	}
}
