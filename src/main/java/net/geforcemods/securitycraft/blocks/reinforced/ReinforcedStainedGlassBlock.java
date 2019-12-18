package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class ReinforcedStainedGlassBlock extends ReinforcedGlassBlock implements IBeaconBeamColorProvider
{
	private final DyeColor color;

	public ReinforcedStainedGlassBlock(DyeColor color, Block vB, String registryPath)
	{
		super(vB, registryPath);
		this.color = color;
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos)
	{
		return color.getColorComponentValues();
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return true;
	}

	@Override
	public DyeColor getColor()
	{
		return color;
	}
}
