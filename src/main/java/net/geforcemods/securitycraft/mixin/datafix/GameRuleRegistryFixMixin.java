package net.geforcemods.securitycraft.mixin.datafix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Dynamic;

import net.minecraft.util.datafix.fixes.GameRuleRegistryFix;

/**
 * Data fixes SecurityCraft's game rules to match vanilla's change from camelCase to snake_case as well as adding the
 * namespace to them in 1.21.11
 */
@Mixin(GameRuleRegistryFix.class)
public class GameRuleRegistryFixMixin {
	@ModifyReturnValue(method = "lambda$makeRule$9", at = @At("RETURN"))
	private static Dynamic<?> securitycraft$datafixSCGameRules(Dynamic<?> original) {
		return original
				.renameAndFixField("fakeWaterSourceConversion", "securitycraft:fake_water_source_conversion", GameRuleRegistryFixMixin::convertBoolean)
				.renameAndFixField("fakeLavaSourceConversion", "securitycraft:fake_lava_source_conversion", GameRuleRegistryFixMixin::convertBoolean);
	}

	@Shadow
	private static Dynamic<?> convertBoolean(Dynamic<?> gameRule) {}
}
