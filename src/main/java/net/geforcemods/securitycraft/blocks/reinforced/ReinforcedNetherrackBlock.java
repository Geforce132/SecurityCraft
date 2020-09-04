package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class ReinforcedNetherrackBlock extends BaseReinforcedBlock
{
	public ReinforcedNetherrackBlock(Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side)
	{
		return side == Direction.UP;
	}
}
