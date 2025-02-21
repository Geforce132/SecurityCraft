package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

/**
 * Makes sure that a portal cannot spawn and replace reinforced blocks with its bottom layer. The rest of the portal frame
 * cannot replace reinforced blocks, as it needs empty blocks to spawn there.
 */
@Mixin(Teleporter.class)
public class TeleporterMixin {
	@Redirect(method = "canHostFrame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState securitycraft$addReinforcedBlockCheck(ServerWorld level, BlockPos offsetPos) {
		BlockState offsetState = level.getBlockState(offsetPos);

		if (offsetState.getBlock() instanceof IReinforcedBlock)
			return Blocks.AIR.defaultBlockState(); //this causes the check to pass (because air's material is not solid) and return false for "canHostFrame"
		else
			return offsetState; //block is not a reinforced block, proceed as normal
	}
}
