package net.geforcemods.securitycraft.mixin.portal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * Makes sure that a portal cannot spawn and replace reinforced blocks with its bottom layer. The rest of the portal frame
 * cannot replace reinforced blocks, as it needs empty blocks to spawn there.
 */
@Mixin(Teleporter.class)
public class TeleporterMixin {
	@Redirect(method = "makePortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
	private IBlockState addReinforcedBlockCheck(WorldServer level, BlockPos offsetPos) {
		IBlockState offsetState = level.getBlockState(offsetPos);

		if (offsetState.getBlock() instanceof IReinforcedBlock)
			return Blocks.AIR.getDefaultState(); //this causes the check to pass (because air's material is not solid) which results in a completely new portal position to be tried out
		else
			return offsetState; //block is not a reinforced block, proceed as normal;
	}
}
