package net.geforcemods.securitycraft.mixin.sussandmine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.SCContent;
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

/**
 * The brush is hardcoded to only be able to successfully brush suspicious sand. Because the suspicious sand mine should also
 * yield items when being brushed while disarmed, the brush needs to check for that as well.
 */
@Mixin(BrushItem.class)
public class BrushItemMixin {
	@Inject(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void securitycraft$checkForSuspiciousSandMine(Level level, LivingEntity entity, ItemStack stack, int tick, CallbackInfo ci, Player player, BlockHitResult blockHitResult, BlockPos pos, int newUseDuration, BlockState brushedState) {
		if (!level.isClientSide() && brushedState.is(SCContent.SUSPICIOUS_SAND_MINE.get()) && level.getBlockEntity(pos) instanceof BrushableMineBlockEntity be) {
			boolean brushFinished = be.brush(level.getGameTime(), player, blockHitResult.getDirection());

			if (brushFinished)
				stack.hurtAndBreak(1, player, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

			ci.cancel();
		}
	}
}