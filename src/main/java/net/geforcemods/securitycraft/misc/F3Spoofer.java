package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
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
import net.neoforged.fml.loading.FMLEnvironment;

public class F3Spoofer {
	private F3Spoofer() {}

	public static BlockState spoofBlockState(BlockState originalState, BlockPos pos) {
		Block originalBlock = originalState.getBlock();

		if (FMLEnvironment.production) {
			switch (originalBlock) {
				case IDisguisable disguisable -> IDisguisable.getDisguisedStateOrDefault(originalState, Minecraft.getInstance().level, pos);
				case FurnaceMineBlock mine -> Blocks.FURNACE.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, originalState.getValue(BlockStateProperties.HORIZONTAL_FACING));
				case BaseFullMineBlock mine -> mine.getBlockDisguisedAs().defaultBlockState();
				default -> {
				}
			}
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
