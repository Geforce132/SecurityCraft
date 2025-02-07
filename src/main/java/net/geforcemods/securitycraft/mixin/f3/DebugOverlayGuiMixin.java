package net.geforcemods.securitycraft.mixin.f3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

@Mixin(DebugOverlayGui.class)
public class DebugOverlayGuiMixin {
	@Shadow
	protected RayTraceResult block;

	@ModifyVariable(method = "getSystemInformation", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState securitycraft$spoofBlockState(BlockState originalState) {
		return F3Spoofer.spoofBlockState(originalState, ((BlockRayTraceResult) block).getBlockPos());
	}

	@ModifyVariable(method = "getSystemInformation", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/world/ClientWorld;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
	private FluidState securitycraft$spoofFluidState(FluidState originalState) {
		return F3Spoofer.spoofFluidState(originalState);
	}
}
