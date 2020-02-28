package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedStainedGlassBlock extends ReinforcedGlassBlock implements IBeaconBeamColorProvider
{
	private final DyeColor color;

	public ReinforcedStainedGlassBlock(DyeColor color, Block vB)
	{
		super(vB);
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

	@Override
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos)
	{
		return 1.0F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
	}
}
