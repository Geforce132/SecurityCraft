package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalForcer;

/**
 * Makes sure that a portal cannot spawn and replace reinforced blocks with its bottom layer. The rest of the portal frame
 * cannot replace reinforced blocks, as it needs empty blocks to spawn there.
 */
@Mixin(PortalForcer.class)
public class PortalForcerMixin {
	@ModifyExpressionValue(method = "canHostFrame", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState securitycraft$addReinforcedBlockCheck(BlockState offsetState) {
		if (offsetState.getBlock() instanceof IReinforcedBlock)
			return Blocks.AIR.defaultBlockState(); //this causes the check to pass (because air's material is not solid) and return false for "canHostFrame"
		else
			return offsetState; //block is not a reinforced block, proceed as normal
	}
}
