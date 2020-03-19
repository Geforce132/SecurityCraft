package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockReinforcedObserver extends BlockObserver implements IReinforcedBlock
{
	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.OBSERVER);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityOwnable();
	}
}
