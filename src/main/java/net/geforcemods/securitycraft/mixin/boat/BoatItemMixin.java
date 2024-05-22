package net.geforcemods.securitycraft.mixin.boat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Vanilla's getBoat method doesn't get the player who placed the boat, so in order to set the owner of the security sea
 * boat, this Mixin is necessary
 */
@Mixin(BoatItem.class)
public class BoatItemMixin {
	@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;setVariant(Lnet/minecraft/world/entity/vehicle/Boat$Type;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$maybeSetSecuritySeaBoatOwner(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack heldStack, HitResult hitResult, Vec3 viewVector, double size, List<Entity> entities, Boat boat) {
		if (boat instanceof SecuritySeaBoat securitySeaBoat)
			securitySeaBoat.setOwner(player);
	}
}
