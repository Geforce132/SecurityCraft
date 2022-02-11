package net.geforcemods.securitycraft.mixin.piston;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Makes sure that tickable Block Entities pushed by a Reinforced Piston are added to World#tickableTileEntities at the right time (so when the world is done ticking all other Block Entities)
 */
@Mixin(World.class)
public class WorldMixin {
	@Shadow
	@Final
	public List<TileEntity> tickableBlockEntities;

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/IProfiler;popPush(Ljava/lang/String;)V"))
	private void onTickBlockEntities(CallbackInfo ci) {
		if (!ReinforcedPistonBlockEntity.SCHEDULED_TICKING_BLOCK_ENTITIES.isEmpty()) {
			tickableBlockEntities.addAll(ReinforcedPistonBlockEntity.SCHEDULED_TICKING_BLOCK_ENTITIES);
			ReinforcedPistonBlockEntity.SCHEDULED_TICKING_BLOCK_ENTITIES.clear();
		}
	}
}
