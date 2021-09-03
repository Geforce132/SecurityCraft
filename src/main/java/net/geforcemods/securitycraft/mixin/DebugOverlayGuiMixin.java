package net.geforcemods.securitycraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

@Mixin(DebugOverlayGui.class)
public class DebugOverlayGuiMixin
{
	@Shadow
	protected RayTraceResult rayTraceBlock;

	@ModifyVariable(method="getDebugInfoRight", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	public BlockState spoofBlockState(BlockState originalState)
	{
		Block originalBlock = originalState.getBlock();

		if(originalBlock instanceof DisguisableBlock)
			return ((DisguisableBlock)originalBlock).getDisguisedBlockState(Minecraft.getInstance().world, ((BlockRayTraceResult)rayTraceBlock).getPos());
		else if(originalBlock instanceof BaseFullMineBlock)
			return ((BaseFullMineBlock)originalBlock).getBlockDisguisedAs().getDefaultState();
		else if(originalBlock instanceof FurnaceMineBlock)
			return Blocks.FURNACE.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, originalState.get(BlockStateProperties.HORIZONTAL_FACING));

		return originalState;
	}

	@ModifyVariable(method="getDebugInfoRight", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/client/world/ClientWorld;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/IFluidState;"))
	public IFluidState spoofFluidState(IFluidState originalState)
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
