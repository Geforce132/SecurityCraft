package net.geforcemods.securitycraft.mixin.mines;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.mines.TrackMineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.OldMinecartBehavior;
import net.minecraft.world.level.block.state.BlockState;

/**
 * In prior NeoForge versions, rail blocks could define their own functionality when a cart passes over them. This mixin
 * injects at that same place, as NeoForge no longer provides hook.
 */
@Mixin(OldMinecartBehavior.class)
public class OldMinecartBehaviorMixin {
	@Definition(id = "powerTrack", local = @Local(type = boolean.class, ordinal = 0))
	@Expression("powerTrack")
	@Inject(method = "moveAlongTrack", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void securitycraft$makeRailMineWork(ServerLevel level, CallbackInfo ci, @Local BlockPos pos, @Local BlockState state) {
		if (state.is(SCContent.TRACK_MINE))
			((TrackMineBlock) state.getBlock()).onMinecartPass(level, pos, ((MinecartBehavior) (Object) this).minecart);
	}
}
