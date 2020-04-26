package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockReinforcedHopper extends BlockHopper implements IReinforcedBlock
{
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityReinforcedHopper();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(Blocks.HOPPER);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}
