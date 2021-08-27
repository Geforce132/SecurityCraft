package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedTintedGlassBlock extends ReinforcedGlassBlock
{
	public ReinforcedTintedGlassBlock(Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
	{
		return false;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos)
	{
		return level.getMaxLightLevel();
	}
}
