package net.geforcemods.securitycraft.mixin.f3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAt.BlockStateInfo;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAt.BlockTagInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Changes the targeted block text on the right side of the screen to the disguised block, so that the player cannot
 * see the actual block (sand mine, inventory scanner (if disguised))
 */
@Mixin({BlockStateInfo.class, BlockTagInfo.class})
public class DebugEntryLookingAtBlockMixin {
	@ModifyReturnValue(method = "getInstance", at = @At("RETURN"))
	private BlockState securitycraft$spoofBlockState(BlockState original, Level level, BlockPos pos) {
		return F3Spoofer.spoofBlockState(original, pos);
	}
}
