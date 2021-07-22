package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ReinforcedObsidianBlock extends BaseReinforcedBlock
{
	public ReinforcedObsidianBlock(Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean isPortalFrame(BlockState state, BlockGetter world, BlockPos pos)
	{
		return true;
	}
}
