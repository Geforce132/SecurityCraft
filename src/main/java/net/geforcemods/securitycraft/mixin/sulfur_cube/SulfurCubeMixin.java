package net.geforcemods.securitycraft.mixin.sulfur_cube;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;

/**
 * Makes sulfur cubes containing reinforced blocks invulnerable to attacks that they would normally take damage from
 */
@Mixin(SulfurCube.class)
public abstract class SulfurCubeMixin extends AbstractCubeMob {
	private SulfurCubeMixin(EntityType<? extends AbstractCubeMob> type, Level level) {
		super(type, level);
	}

	@Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"), cancellable = true)
	private void securitycraft$onSulfurCubeTakeDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
		if (getItemBySlot(EquipmentSlot.BODY).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IReinforcedBlock)
			cir.setReturnValue(false);
	}
}
