package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.tileentity.ReinforcedIronBarsTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class ReinforcedIronBarsBlock extends ReinforcedPaneBlock
{
	public ReinforcedIronBarsBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world)
	{
		return new ReinforcedIronBarsTileEntity();
	}
}
