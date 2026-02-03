package net.geforcemods.securitycraft.mixin.suspiciousmines;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.blockentities.BrushableMineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

@Mixin(BrushItem.class)
public class BrushItemMixin {
	/**
	 * The brush can only brush blocks that the player can collide with. As suspicious mines aren't collidable for non-owners not
	 * in creative mode, this has to be adjusted for them.
	 */
	@Inject(method = "calculateHitResult", at = @At("HEAD"), cancellable = true)
	private void securitycraft$makeSuspiciousMinesBrushablePartTwo(Player player, CallbackInfoReturnable<HitResult> cir) {
		Vec3 eyePosition = player.getEyePosition();
		Predicate<Entity> entitySelector = e -> (e instanceof LivingEntity livingEntity ? livingEntity.canBeSeenByAnyone() : !e.isSpectator()) && e.isPickable();
		Vec3 viewVector = player.getViewVector(0.0F).scale(player.blockInteractionRange());
		Level level = player.level();
		Vec3 direction = eyePosition.add(viewVector);
		HitResult hitResult = level.clip(new ClipContext(eyePosition, direction, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

		if (hitResult.getType() != Type.MISS)
			direction = hitResult.getLocation();

		HitResult hitResult1 = ProjectileUtil.getEntityHitResult(level, player, eyePosition, direction, player.getBoundingBox().expandTowards(viewVector).inflate(1.0D), entitySelector, 0.0F);

		if (hitResult1 != null)
			hitResult = hitResult1;

		if (hitResult.getType() == Type.BLOCK && level.getBlockState(BlockPos.containing(hitResult.getLocation())).is(SCTags.Blocks.SUSPICIOUS_MINES))
			cir.setReturnValue(hitResult);
	}

	/**
	 * The brush is hardcoded to only be able to successfully brush vanilla blocks. Because the suspicious mines should also
	 * yield items when being brushed while disarmed, the brush needs to check for that as well.
	 */
	@Inject(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"), cancellable = true)
	private void securitycraft$checkForSuspiciousMine(Level level, LivingEntity entity, ItemStack stack, int tick, CallbackInfo ci, @Local Player player, @Local BlockHitResult blockHitResult, @Local BlockPos pos, @Local BlockState brushedState) {
		if (!level.isClientSide() && brushedState.is(SCTags.Blocks.SUSPICIOUS_MINES) && level.getBlockEntity(pos) instanceof BrushableMineBlockEntity be) {
			boolean brushFinished = be.brush(level.getGameTime(), (ServerLevel) level, player, blockHitResult.getDirection(), stack);

			if (brushFinished)
				stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

			ci.cancel();
		}
	}
}