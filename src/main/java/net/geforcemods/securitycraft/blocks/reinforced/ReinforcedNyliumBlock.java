package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.data.worldgen.Features;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.TwistingVinesFeature;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.server.level.ServerLevel;

public class ReinforcedNyliumBlock extends BaseReinforcedBlock implements BonemealableBlock
{
	public ReinforcedNyliumBlock(Block.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		if (!hasLightAbove(state, world, pos)) {
			world.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
		}
	}

	private static boolean hasLightAbove(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos upperPos = pos.above();
		BlockState upperState = world.getBlockState(upperPos);
		int lightLevel = LayerLightEngine.getLightBlockInto(world, state, pos, upperState, upperPos, Direction.UP, upperState.getLightBlock(world, upperPos));
		return lightLevel < world.getMaxLightLevel();
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean flag) {
		return world.getBlockState(pos.above()).isAir(world, pos);
	}

	@Override
	public boolean isBonemealSuccess(Level world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, Random random, BlockPos pos, BlockState blockState) {
		BlockState state = world.getBlockState(pos);
		BlockPos upperPos = pos.above();

		if (state.is(SCContent.REINFORCED_CRIMSON_NYLIUM.get())) {
			NetherForestVegetationFeature.place(world, random, upperPos, Features.Configs.CRIMSON_FOREST_CONFIG, 3, 1);
		}
		else if (state.is(SCContent.REINFORCED_WARPED_NYLIUM.get())) {
			NetherForestVegetationFeature.place(world, random, upperPos, Features.Configs.WARPED_FOREST_CONFIG, 3, 1);
			NetherForestVegetationFeature.place(world, random, upperPos, Features.Configs.NETHER_SPROUTS_CONFIG, 3, 1);

			if (random.nextInt(8) == 0) {
				TwistingVinesFeature.place(world, random, upperPos, 3, 1, 2);
			}
		}
	}
}
