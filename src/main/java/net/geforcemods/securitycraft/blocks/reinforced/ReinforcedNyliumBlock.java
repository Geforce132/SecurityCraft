package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.NetherVegetationFeature;
import net.minecraft.world.gen.feature.TwistingVineFeature;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

public class ReinforcedNyliumBlock extends BaseReinforcedBlock implements IGrowable
{
	public ReinforcedNyliumBlock(Block vB) {
		super(SoundType.NYLIUM, Material.ROCK, vB);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!hasLightAbove(state, world, pos)) {
			world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
		}
	}

	private static boolean hasLightAbove(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos upperPos = pos.up();
		BlockState upperState = world.getBlockState(upperPos);
		int lightLevel = LightEngine.func_215613_a(world, state, pos, upperState, upperPos, Direction.UP, upperState.getOpacity(world, upperPos));
		return lightLevel < world.getMaxLightLevel();
	}

	@Override
	public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean flag) {
		return world.getBlockState(pos.up()).isAir(world, pos);
	}

	@Override
	public boolean canUseBonemeal(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState blockState) {
		BlockState state = world.getBlockState(pos);
		BlockPos upperPos = pos.up();

		if (state.isIn(SCContent.REINFORCED_CRIMSON_NYLIUM.get())) {
			NetherVegetationFeature.func_236325_a_(world, random, upperPos, Features.Configs.field_243987_k, 3, 1);
		}
		else if (state.isIn(SCContent.REINFORCED_WARPED_NYLIUM.get())) {
			NetherVegetationFeature.func_236325_a_(world, random, upperPos, Features.Configs.field_243988_l, 3, 1);
			NetherVegetationFeature.func_236325_a_(world, random, upperPos, Features.Configs.field_243989_m, 3, 1);

			if (random.nextInt(8) == 0) {
				TwistingVineFeature.func_236423_a_(world, random, upperPos, 3, 1, 2);
			}
		}
	}
}
