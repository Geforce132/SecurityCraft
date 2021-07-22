package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedStainedGlassBlock extends ReinforcedGlassBlock implements BeaconBeamBlock
{
	private final DyeColor color;

	public ReinforcedStainedGlassBlock(Block.Properties properties, DyeColor color, Block vB)
	{
		super(properties, vB);
		this.color = color;
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos)
	{
		return color.getTextureDiffuseColors();
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
	{
		return true;
	}

	@Override
	public DyeColor getColor()
	{
		return color;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos)
	{
		return 1.0F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return adjacentBlockState.getBlock() == this ? true : super.skipRendering(state, adjacentBlockState, side);
	}
}
