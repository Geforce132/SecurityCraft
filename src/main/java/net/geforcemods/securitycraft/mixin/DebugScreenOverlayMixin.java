package net.geforcemods.securitycraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin
{
	@Shadow
	protected HitResult block;

	@ModifyVariable(method="getSystemInformation", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	public BlockState spoofBlockState(BlockState originalState)
	{
		return F3Spoofer.spoofBlockState(originalState, ((BlockHitResult)block).getBlockPos());
	}

	@ModifyVariable(method="getSystemInformation", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/client/multiplayer/ClientLevel;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
	public FluidState spoofFluidState(FluidState originalState)
	{
		return F3Spoofer.spoofFluidState(originalState);
	}
}
