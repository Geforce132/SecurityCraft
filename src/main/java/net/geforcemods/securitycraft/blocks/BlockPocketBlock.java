package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockPocketBlock extends BaseReinforcedBlock implements IBlockPocket
{
	public BlockPocketBlock(Material mat, Block vB, String registryPath)
	{
		super(mat, vB, registryPath);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new BlockPocketTileEntity();
	}
}
