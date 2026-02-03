package net.geforcemods.securitycraft.mixin.f3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAtFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

/**
 * Changes the targeted fluid text on the right side of the screen to the disguised block/fluid, so that the player cannot
 * see the actual block (fake water, fake lava)
 */
@Mixin(DebugEntryLookingAtFluid.class)
public class DebugEntryLookingAtFluidMixin {
	@WrapOperation(method = "display", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
	private FluidState securitycraft$spoofFluidState(Level level, BlockPos pos, Operation<FluidState> original) {
		return F3Spoofer.spoofFluidState(original.call(level, pos));
	}
}
