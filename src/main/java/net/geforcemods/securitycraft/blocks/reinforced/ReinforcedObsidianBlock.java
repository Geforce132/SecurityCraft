package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ReinforcedObsidianBlock extends BaseReinforcedBlock
{
	public ReinforcedObsidianBlock(Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean isPortalFrame(BlockState state, IWorldReader world, BlockPos pos)
	{
		return true;
	}
}
