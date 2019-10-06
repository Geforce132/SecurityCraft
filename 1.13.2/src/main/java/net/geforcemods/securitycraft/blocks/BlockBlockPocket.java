package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedBase;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocket;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockBlockPocket extends BlockReinforcedBase implements IBlockPocket
{
	public BlockBlockPocket(Material mat, Block vB, String registryPath)
	{
		super(mat, vB, registryPath);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new TileEntityBlockPocket();
	}
}
