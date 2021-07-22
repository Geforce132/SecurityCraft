package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedGlassBlock extends BaseReinforcedBlock
{
	public ReinforcedGlassBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
	{
		return true;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos)
	{
		return 1.0F;
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState)
	{
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return adjacentBlockState.getBlock() == this ? true : super.skipRendering(state, adjacentBlockState, side);
	}
}
