package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockReinforcedGlass extends BlockReinforcedBase
{
	public BlockReinforcedGlass(Block vB, String registryPath)
	{
		super(SoundType.GLASS, Material.GLASS, vB, registryPath);
	}

	@Override
	public boolean propagatesSkylightDown(IBlockState state, IBlockReader reader, BlockPos pos)
	{
		return true;
	}

	@Override
	public int quantityDropped(IBlockState state, Random random)
	{
		return 0;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return true;
	}
}
