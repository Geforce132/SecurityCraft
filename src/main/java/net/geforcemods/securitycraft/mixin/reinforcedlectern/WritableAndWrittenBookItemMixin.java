package net.geforcemods.securitycraft.mixin.reinforcedlectern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.block.state.BlockState;

@Mixin({
		WritableBookItem.class, WrittenBookItem.class
})
public class WritableAndWrittenBookItemMixin {
	@ModifyExpressionValue(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
	private boolean securitycraft$allowBooksInReinforcedLectern(boolean original, @Local BlockState state) {
		return original || state.is(SCContent.REINFORCED_LECTERN.get());
	}
}
