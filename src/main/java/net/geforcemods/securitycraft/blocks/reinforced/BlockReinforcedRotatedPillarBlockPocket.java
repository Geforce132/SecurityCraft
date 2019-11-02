package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocket;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockReinforcedRotatedPillarBlockPocket extends BlockReinforcedRotatedPillar implements IBlockPocket
{
	public BlockReinforcedRotatedPillarBlockPocket(Material mat, Block vB, String registryPath)
	{
		super(mat, vB, registryPath);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new TileEntityBlockPocket();
	}
}
