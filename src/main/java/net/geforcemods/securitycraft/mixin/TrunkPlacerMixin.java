package net.geforcemods.securitycraft.mixin;

import java.util.Random;
import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSnowyDirtBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

/**
 * Mimics IForgeBlockState#onTreeGrow in 1.19.2 by disallowing reinforced blocks to be replaced with dirt when a tree grows
 * above them
 */
@Mixin(TrunkPlacer.class)
public class TrunkPlacerMixin {
	@Inject(method = "setDirtAt", at = @At("HEAD"), cancellable = true)
	private static void onSetDirtAt(LevelSimulatedReader simulatedLevel, BiConsumer<BlockPos, BlockState> blockSetter, Random random, BlockPos pos, TreeConfiguration config, CallbackInfo callback) {
		if (simulatedLevel instanceof LevelReader level && (level.getBlockState(pos).getBlock() instanceof BaseReinforcedBlock || level.getBlockState(pos).getBlock() instanceof ReinforcedSnowyDirtBlock))
			callback.cancel();
	}
}
