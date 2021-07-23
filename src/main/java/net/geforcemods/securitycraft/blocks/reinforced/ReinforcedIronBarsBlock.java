package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.tileentity.ReinforcedIronBarsTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedIronBarsBlock extends ReinforcedPaneBlock
{
	public ReinforcedIronBarsBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new ReinforcedIronBarsTileEntity(pos, state);
	}
}
