package net.geforcemods.securitycraft.mixin.f3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAt.FluidStateInfo;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAt.FluidTagInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

/**
 * Changes the targeted fluid text on the right side of the screen to the disguised fluid, so that the player cannot
 * see the actual fluid
 */
@Mixin({FluidStateInfo.class, FluidTagInfo.class})
public class DebugEntryLookingAtFluidMixin {
	@ModifyReturnValue(method = "getInstance", at = @At("RETURN"))
	private FluidState securitycraft$spoofBlockState(FluidState original, Level level, BlockPos pos) {
		return F3Spoofer.spoofFluidState(original);
	}
}
