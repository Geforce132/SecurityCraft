package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;

public class F3Spoofer
{
	public static BlockState spoofBlockState(BlockState originalState, BlockPos pos)
	{
		Block originalBlock = originalState.getBlock();

		if(originalBlock instanceof DisguisableBlock)
		{
			BlockState disguisedState = ((DisguisableBlock)originalBlock).getDisguisedBlockState(Minecraft.getInstance().world, pos);

			return disguisedState != null ? disguisedState : originalState;
		}
		else if(originalBlock instanceof BaseFullMineBlock)
			return ((BaseFullMineBlock)originalBlock).getBlockDisguisedAs().getDefaultState();
		else if(originalBlock instanceof FurnaceMineBlock)
			return Blocks.FURNACE.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, originalState.get(BlockStateProperties.HORIZONTAL_FACING));

		return originalState;
	}

	public static FluidState spoofFluidState(FluidState originalState)
	{
		Fluid originalFluid = originalState.getFluid();

		if(originalFluid == SCContent.FAKE_WATER.get())
			return Fluids.WATER.getDefaultState().with(FlowingFluid.FALLING, originalState.get(FlowingFluid.FALLING));
		else if(originalFluid == SCContent.FLOWING_FAKE_WATER.get())
			return Fluids.FLOWING_WATER.getDefaultState().with(FlowingFluid.FALLING, originalState.get(FlowingFluid.FALLING)).with(FlowingFluid.LEVEL_1_8, originalState.get(FlowingFluid.LEVEL_1_8));
		else if(originalFluid == SCContent.FAKE_LAVA.get())
			return Fluids.LAVA.getDefaultState().with(FlowingFluid.FALLING, originalState.get(FlowingFluid.FALLING));
		else if(originalFluid == SCContent.FLOWING_FAKE_LAVA.get())
			return Fluids.FLOWING_LAVA.getDefaultState().with(FlowingFluid.FALLING, originalState.get(FlowingFluid.FALLING)).with(FlowingFluid.LEVEL_1_8, originalState.get(FlowingFluid.LEVEL_1_8));

		return originalState;
	}
}
