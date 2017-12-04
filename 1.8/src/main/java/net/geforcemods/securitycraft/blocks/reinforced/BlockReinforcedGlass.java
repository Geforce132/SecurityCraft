package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedGlass extends BlockGlass implements ITileEntityProvider, IReinforcedBlock {

	public BlockReinforcedGlass(Material par1Material) {
		super(par1Material, false);
	}

	@Override
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
		super.breakBlock(par1World, pos, state);
		par1World.removeTileEntity(pos);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.glass
		});
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}
