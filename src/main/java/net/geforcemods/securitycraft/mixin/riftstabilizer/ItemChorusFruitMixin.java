package net.geforcemods.securitycraft.mixin.riftstabilizer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Notifies all Rift Stabilizers in the vicinity that a player has just teleported by using a chorus fruit. If a Rift
 * Stabilizer prohibits the teleport, the player is put back to their old position.
 */
@Mixin(ItemChorusFruit.class)
public class ItemChorusFruitMixin {
	@Inject(method = "onItemUseFinish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/EntityPlayer;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onChorusFruitTeleport(ItemStack stack, World world, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> callback, ItemStack unused, double oldX, double oldY, double oldZ) {
		if (SCEventHandler.handleEntityTeleport(entityLiving, new Vec3d(oldX, oldY, oldZ), entityLiving.getPositionVector(), TeleportationType.CHORUS_FRUIT))
			entityLiving.setPositionAndUpdate(oldX, oldY, oldZ);
	}
}
