package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.blockentities.ReinforcedIronBarsBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ReinforcedIronBarsBlock extends ReinforcedPaneBlock {
	public ReinforcedIronBarsBlock(AbstractBlock.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new ReinforcedIronBarsBlockEntity();
	}
}
