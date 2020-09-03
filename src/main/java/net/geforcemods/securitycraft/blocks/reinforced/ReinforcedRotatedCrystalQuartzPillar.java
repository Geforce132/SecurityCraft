package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ReinforcedRotatedCrystalQuartzPillar extends ReinforcedRotatedPillarBlock implements IBlockPocket
{
	public ReinforcedRotatedCrystalQuartzPillar(Block.Properties properties, Supplier<Block> vB)
	{
		super(properties, vB);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new BlockPocketTileEntity();
	}
}
