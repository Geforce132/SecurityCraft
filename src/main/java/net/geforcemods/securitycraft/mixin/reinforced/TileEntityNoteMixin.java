package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Fixes instruments of reinforced blocks to match their vanilla counterparts
 */
@Mixin(TileEntityNote.class)
public class TileEntityNoteMixin {
	@Redirect(method = "triggerNote", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", ordinal = 1))
	private IBlockState securitycraft$delegateReinforcedBlockToVanillaCounterpart(World level, BlockPos pos) {
		IBlockState state = level.getBlockState(pos);

		if (state.getBlock() instanceof IReinforcedBlock)
			return ((IReinforcedBlock) state.getBlock()).convertToVanillaState(state);

		return state;
	}
}
