package net.geforcemods.securitycraft.mixin.datafix;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.util.datafix.fixes.BoatSplitFix;

/**
 * Splits security sea boats up into a separate entity type per wood type
 */
@Mixin(BoatSplitFix.class)
public class BoatSplitFixMixin {
	@Inject(method = "isAnyBoat", at = @At("TAIL"), cancellable = true)
	private static void securitycraft$isSecuritySeaBoat(String name, CallbackInfoReturnable<Boolean> cir) {
		if (name.equals("securitycraft:security_sea_boat"))
			cir.setReturnValue(true);
	}

	@ModifyVariable(method = "lambda$makeRule$1", at = @At("STORE"), ordinal = 0)
	private static String securitycraft$mapSecuritySeaBoat(String orElse, @Local(ordinal = 0) Optional<String> entityId, @Local(ordinal = 1) Optional<String> oldType) {
		if (entityId.get().equals("securitycraft:security_sea_boat")) {
			return oldType.map(type -> switch (type) {
				case "spruce" -> "securitycraft:spruce_security_sea_boat";
				case "birch" -> "securitycraft:birch_security_sea_boat";
				case "jungle" -> "securitycraft:jungle_security_sea_boat";
				case "acacia" -> "securitycraft:acacia_security_sea_boat";
				case "cherry" -> "securitycraft:cherry_security_sea_boat";
				case "dark_oak" -> "securitycraft:dark_oak_security_sea_boat";
				case "mangrove" -> "securitycraft:mangrove_security_sea_boat";
				case "bamboo" -> "securitycraft:bamboo_security_sea_raft";
				default -> "securitycraft:oak_security_sea_boat";
			}).orElse("securitycraft:oak_security_sea_boat");
		}
		else
			return orElse;
	}
}
