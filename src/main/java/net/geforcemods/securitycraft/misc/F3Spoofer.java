package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class F3Spoofer
{
	public static BlockState spoofBlockState(BlockState originalState, BlockPos pos)
	{
		Block originalBlock = originalState.getBlock();

		if(originalBlock instanceof DisguisableBlock)
		{
			BlockState disguisedState = ((DisguisableBlock)originalBlock).getDisguisedBlockState(Minecraft.getInstance().level, pos);

			return disguisedState != null ? disguisedState : originalState;
		}
		else if(originalBlock instanceof BaseFullMineBlock)
			return ((BaseFullMineBlock)originalBlock).getBlockDisguisedAs().defaultBlockState();
		else if(originalBlock instanceof FurnaceMineBlock)
			return Blocks.FURNACE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, originalState.getValue(BlockStateProperties.HORIZONTAL_FACING));

		return originalState;
	}

	public static FluidState spoofFluidState(FluidState originalState)
	{
		Fluid originalFluid = originalState.getType();

		if(originalFluid == SCContent.FAKE_WATER.get())
			return Fluids.WATER.defaultFluidState().setValue(FlowingFluid.FALLING, originalState.getValue(FlowingFluid.FALLING));
		else if(originalFluid == SCContent.FLOWING_FAKE_WATER.get())
			return Fluids.FLOWING_WATER.defaultFluidState().setValue(FlowingFluid.FALLING, originalState.getValue(FlowingFluid.FALLING)).setValue(FlowingFluid.LEVEL, originalState.getValue(FlowingFluid.LEVEL));
		else if(originalFluid == SCContent.FAKE_LAVA.get())
			return Fluids.LAVA.defaultFluidState().setValue(FlowingFluid.FALLING, originalState.getValue(FlowingFluid.FALLING));
		else if(originalFluid == SCContent.FLOWING_FAKE_LAVA.get())
			return Fluids.FLOWING_LAVA.defaultFluidState().setValue(FlowingFluid.FALLING, originalState.getValue(FlowingFluid.FALLING)).setValue(FlowingFluid.LEVEL, originalState.getValue(FlowingFluid.LEVEL));

		return originalState;
	}
}
