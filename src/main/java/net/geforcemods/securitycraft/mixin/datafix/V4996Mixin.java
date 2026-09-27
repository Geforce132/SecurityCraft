package net.geforcemods.securitycraft.mixin.datafix;

import java.util.SequencedMap;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V4996;

/**
 * Teaches data fixers that the securitycraft:saved_block_state component has a block state in it
 */
@Mixin(V4996.class)
public class V4996Mixin {
	@ModifyReturnValue(method = "components", at = @At("TAIL"))
	private static SequencedMap<String, Supplier<TypeTemplate>> securitycraft$teachSavedBlockState(SequencedMap<String, Supplier<TypeTemplate>> original, Schema schema) {
		original.put("securitycraft:saved_block_state", () -> DSL.optionalFields("state", References.BLOCK_STATE.in(schema)));
		return original;
	}
}
