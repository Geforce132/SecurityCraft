package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Book and quills as well as written books have a specific check for the vanilla lectern before they are placed into it. To
 * make them placeable into the reinforced lectern, a check for it needs to be added.
 */
@Mixin({
		WritableBookItem.class, WrittenBookItem.class
})
public class WritableAndWrittenBookItemMixin {
	@Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;is(Lnet/minecraft/block/Block;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$allowBooksInReinforcedLectern(ItemUseContext ctx, CallbackInfoReturnable<ActionResultType> cir, World level, BlockPos pos, BlockState state) {
		if (state.is(SCContent.REINFORCED_LECTERN.get())) {
			ReinforcedLecternBlockEntity be = (ReinforcedLecternBlockEntity) level.getBlockEntity(pos);
			PlayerEntity player = ctx.getPlayer();
			ItemStack stack = player.getItemInHand(ctx.getHand());

			if (be.isOwnedBy(player) && LecternBlock.tryPlaceBook(level, pos, state, stack))
				cir.setReturnValue(ActionResultType.sidedSuccess(level.isClientSide));
		}
	}
}
