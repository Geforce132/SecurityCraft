package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.tileentity.ReinforcedIronBarsTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ReinforcedIronBarsBlock extends ReinforcedPaneBlock
{
	public ReinforcedIronBarsBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new ReinforcedIronBarsTileEntity();
	}
}
