package net.geforcemods.securitycraft.mixin.mines;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.mines.TrackMineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.block.state.BlockState;

/**
 * In prior NeoForge versions, rail blocks could define their own functionality when a cart passes over them. This mixin
 * implements a similar hook for vanilla's minecart experiment.
 */
@Mixin(NewMinecartBehavior.class)
public class NewMinecartBehaviorMixin {
	@Inject(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;setOldPosAndRot()V"))
	private void securitycraft$makeRailMineWork(ServerLevel level, CallbackInfo ci, @Local BlockPos currentPos, @Local BlockState currentState) {
		if (currentState.is(SCContent.TRACK_MINE))
			((TrackMineBlock) currentState.getBlock()).onMinecartPass(level, currentPos, ((MinecartBehavior) (Object) this).minecart);
	}
}
