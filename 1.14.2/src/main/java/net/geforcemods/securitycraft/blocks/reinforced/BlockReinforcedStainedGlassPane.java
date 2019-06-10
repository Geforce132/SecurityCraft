package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockReinforcedStainedGlassPane extends BlockReinforcedPane
{
	private final EnumDyeColor color;

	public BlockReinforcedStainedGlassPane(EnumDyeColor color, Block vB, String registryPath)
	{
		super(SoundType.GLASS, Material.GLASS, vB, registryPath);
		this.color = color;
	}

	@Override
	public float[] getBeaconColorMultiplier(IBlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos)
	{
		return color.getColorComponentValues();
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
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
