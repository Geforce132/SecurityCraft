package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.Features;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.TwistingVinesFeature;
import net.minecraft.world.level.lighting.LayerLightEngine;

public class ReinforcedNyliumBlock extends BaseReinforcedBlock implements BonemealableBlock
{
	public ReinforcedNyliumBlock(Block.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (!hasLightAbove(state, level, pos)) {
			level.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
		}
	}

	private static boolean hasLightAbove(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos upperPos = pos.above();
		BlockState upperState = level.getBlockState(upperPos);
		int lightLevel = LayerLightEngine.getLightBlockInto(level, state, pos, upperState, upperPos, Direction.UP, upperState.getLightBlock(level, upperPos));
		return lightLevel < level.getMaxLightLevel();
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean flag) {
		return level.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState blockState) {
		BlockState state = level.getBlockState(pos);
		BlockPos upperPos = pos.above();

		if (state.is(SCContent.REINFORCED_CRIMSON_NYLIUM.get())) {
			NetherForestVegetationFeature.place(level, random, upperPos, Features.Configs.CRIMSON_FOREST_CONFIG, 3, 1);
		}
		else if (state.is(SCContent.REINFORCED_WARPED_NYLIUM.get())) {
			NetherForestVegetationFeature.place(level, random, upperPos, Features.Configs.WARPED_FOREST_CONFIG, 3, 1);
			NetherForestVegetationFeature.place(level, random, upperPos, Features.Configs.NETHER_SPROUTS_CONFIG, 3, 1);

			if (random.nextInt(8) == 0) {
				TwistingVinesFeature.place(level, random, upperPos, 3, 1, 2);
			}
		}
	}
}
