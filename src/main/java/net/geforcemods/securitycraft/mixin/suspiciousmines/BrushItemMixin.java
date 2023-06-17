package net.geforcemods.securitycraft.mixin.suspiciousmines;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.blockentities.BrushableMineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * The brush is hardcoded to only be able to successfully brush vanilla blocks. Because the suspicious mines should also
 * yield items when being brushed while disarmed, the brush needs to check for that as well.
 */
@Mixin(BrushItem.class)
public class BrushItemMixin {
	@Inject(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void securitycraft$checkForSuspiciousMine(Level level, LivingEntity entity, ItemStack stack, int tick, CallbackInfo ci, Player player, HitResult hitResult, BlockHitResult blockHitResult, int i, boolean flag, BlockPos pos, BlockState brushedState) {
		if (!level.isClientSide() && brushedState.is(SCTags.Blocks.SUSPICIOUS_MINES)) {
			if (level.getBlockEntity(pos) instanceof BrushableMineBlockEntity be) {
				boolean brushFinished = be.brush(level.getGameTime(), player, blockHitResult.getDirection());

				if (brushFinished)
					stack.hurtAndBreak(1, player, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

				ci.cancel();
			}
		}
	}
}