package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class ReinforcedRedstoneBlock extends BaseReinforcedBlock
{
	public ReinforcedRedstoneBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side)
	{
		return 15;
	}
}
