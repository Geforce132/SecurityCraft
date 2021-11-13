package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedRootedDirtBlock extends BaseReinforcedBlock implements BonemealableBlock
{
	public ReinforcedRootedDirtBlock(Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClientSide)
	{
		return level.getBlockState(pos.below()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state)
	{
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state)
	{
		level.setBlockAndUpdate(pos.below(), Blocks.HANGING_ROOTS.defaultBlockState());
	}
}
