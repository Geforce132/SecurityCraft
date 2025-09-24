package net.geforcemods.securitycraft.mixin.f3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAtBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Changes the targeted block text on the right side of the screen to the disguised block/fluid, so that the player cannot
 * see the actual block (sand mine, inventory scanner (if disguised))
 */
@Mixin(DebugEntryLookingAtBlock.class)
public class DebugEntryLookingAtBlockMixin {
	@WrapOperation(method = "display", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState securitycraft$spoofBlockState(Level level, BlockPos pos, Operation<BlockState> original) {
		return F3Spoofer.spoofBlockState(original.call(level, pos), pos);
	}
}
