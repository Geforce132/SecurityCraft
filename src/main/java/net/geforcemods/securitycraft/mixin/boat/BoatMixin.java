package net.geforcemods.securitycraft.mixin.boat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fixes <a href="https://bugs.mojang.com/browse/MC-119369">MC-119369</a> for SecurityCraft's security sea boats, as they
 * should not be destroyable by anyone other than the owner.
 */
@Mixin(Boat.class)
public abstract class BoatMixin extends Entity {
	protected BoatMixin(EntityType<? extends Boat> type, Level level) {
		super(type, level);
	}

	@Inject(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z"), cancellable = true)
	private void securitycraft$fixMC199369(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
		if (getType() == SCContent.SECURITY_SEA_BOAT_ENTITY.get()) {
			resetFallDistance();
			ci.cancel();
		}
	}
}
