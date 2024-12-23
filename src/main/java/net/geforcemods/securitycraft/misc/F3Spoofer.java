package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
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
import net.minecraftforge.fml.loading.FMLEnvironment;

public class F3Spoofer {
	private F3Spoofer() {}

	public static BlockState spoofBlockState(BlockState originalState, BlockPos pos) {
		Block originalBlock = originalState.getBlock();

		if (FMLEnvironment.production) {
			if (originalBlock instanceof IDisguisable)
				return IDisguisable.getDisguisedBlockState(Minecraft.getInstance().level.getBlockEntity(pos)).orElse(originalState);
			else if (originalBlock instanceof FurnaceMineBlock)
				return Blocks.FURNACE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, originalState.getValue(BlockStateProperties.HORIZONTAL_FACING));
			else if (originalBlock instanceof BaseFullMineBlock)
				return ((BaseFullMineBlock) originalBlock).getBlockDisguisedAs().defaultBlockState();
		}

		return originalState;
	}

	public static FluidState spoofFluidState(FluidState originalState) {
		Fluid originalFluid = originalState.getType();

		if (FMLEnvironment.production) {
			if (originalFluid == SCContent.FAKE_WATER.get())
				return Fluids.WATER.defaultFluidState().setValue(FlowingFluid.FALLING, originalState.getValue(FlowingFluid.FALLING));
			else if (originalFluid == SCContent.FLOWING_FAKE_WATER.get())
				return Fluids.FLOWING_WATER.defaultFluidState().setValue(FlowingFluid.FALLING, originalState.getValue(FlowingFluid.FALLING)).setValue(FlowingFluid.LEVEL, originalState.getValue(FlowingFluid.LEVEL));
			else if (originalFluid == SCContent.FAKE_LAVA.get())
				return Fluids.LAVA.defaultFluidState().setValue(FlowingFluid.FALLING, originalState.getValue(FlowingFluid.FALLING));
			else if (originalFluid == SCContent.FLOWING_FAKE_LAVA.get())
				return Fluids.FLOWING_LAVA.defaultFluidState().setValue(FlowingFluid.FALLING, originalState.getValue(FlowingFluid.FALLING)).setValue(FlowingFluid.LEVEL, originalState.getValue(FlowingFluid.LEVEL));
		}

		return originalState;
	}
}
