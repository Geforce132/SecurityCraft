package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Book and quills as well as written books have a specific check for the vanilla lectern before they are placed into it. To
 * make them placeable into the reinforced lectern, a check for it needs to be added.
 */
@Mixin({
		WritableBookItem.class, WrittenBookItem.class
})
public class WritableAndWrittenBookItemMixin {
	@Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$allowBooksInReinforcedLectern(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir, Level level, BlockPos pos, BlockState state) {
		if (state.is(SCContent.REINFORCED_LECTERN.get())) {
			ReinforcedLecternBlockEntity be = (ReinforcedLecternBlockEntity) level.getBlockEntity(pos);
			Player player = ctx.getPlayer();
			ItemStack stack = player.getItemInHand(ctx.getHand());

			if (be.isOwnedBy(player) && LecternBlock.tryPlaceBook(player, level, pos, state, stack))
				cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
		}
	}
}
