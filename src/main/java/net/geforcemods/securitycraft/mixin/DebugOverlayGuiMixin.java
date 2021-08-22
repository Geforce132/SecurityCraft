package net.geforcemods.securitycraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

@Mixin(DebugOverlayGui.class)
public class DebugOverlayGuiMixin
{
	@Shadow
	protected RayTraceResult rayTraceBlock;

	@ModifyVariable(method="getDebugInfoRight", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	public BlockState spoofState(BlockState originalState)
	{
		Block originalBlock = originalState.getBlock();
		BlockState disguisedState = null;

		if(originalBlock instanceof DisguisableBlock)
			disguisedState = ((DisguisableBlock)originalBlock).getDisguisedBlockState(Minecraft.getInstance().world, ((BlockRayTraceResult)rayTraceBlock).getPos());
		else if(originalBlock instanceof BaseFullMineBlock)
			disguisedState = ((BaseFullMineBlock)originalBlock).getBlockDisguisedAs().getDefaultState();
		else if(originalBlock instanceof FurnaceMineBlock)
			disguisedState = Blocks.FURNACE.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, originalState.get(BlockStateProperties.HORIZONTAL_FACING));

		return disguisedState != null ? disguisedState : originalState;
	}
}
