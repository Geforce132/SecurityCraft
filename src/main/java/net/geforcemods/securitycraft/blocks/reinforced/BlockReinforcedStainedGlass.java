package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockReinforcedStainedGlass extends BlockReinforcedGlass
{
	private final EnumDyeColor color;

	public BlockReinforcedStainedGlass(EnumDyeColor color, Block vB, String registryPath)
	{
		super(vB, registryPath);
		this.color = color;
	}

	@Override
	public float[] getBeaconColorMultiplier(IBlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos)
	{
		return color.getColorComponentValues();
	}

	@Override
	public boolean propagatesSkylightDown(IBlockState state, IBlockReader reader, BlockPos pos)
	{
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public int quantityDropped(IBlockState state, Random random)
	{
		return 0;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void onBlockAdded(IBlockState state, World world, BlockPos pos, IBlockState oldState)
	{
		if(oldState.getBlock() != state.getBlock())
		{
			if(!world.isRemote)
				BlockBeacon.updateColorAsync(world, pos);
		}
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
		{
			if(!world.isRemote)
				BlockBeacon.updateColorAsync(world, pos);
		}
	}
}
