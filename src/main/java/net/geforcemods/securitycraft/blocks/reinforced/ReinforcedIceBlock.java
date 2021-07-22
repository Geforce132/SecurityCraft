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
	public boolean isRandomlyTicking(BlockState state)
	{
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(world.getBrightness(LightType.BLOCK, pos) > 11 - state.getLightBlock(world, pos))
		{
			if(world.dimensionType().ultraWarm())
				world.removeBlock(pos, false);
			else
			{
				world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
				world.neighborChanged(pos, Blocks.WATER, pos);
			}
		}
	}
}
