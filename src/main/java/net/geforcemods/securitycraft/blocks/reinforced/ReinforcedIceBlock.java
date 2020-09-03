package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.server.ServerWorld;

public class ReinforcedIceBlock extends BaseReinforcedBlock
{
	public ReinforcedIceBlock(Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean ticksRandomly(BlockState state)
	{
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(world.getLightFor(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos))
		{
			if(world.func_230315_m_().func_236040_e_()) //getDimensionType, doesWaterVaporize
				world.removeBlock(pos, false);
			else
			{
				world.setBlockState(pos, Blocks.WATER.getDefaultState());
				world.neighborChanged(pos, Blocks.WATER, pos);
			}
		}
	}
}
