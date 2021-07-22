package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side)
	{
		return 15;
	}
}
