package net.geforcemods.securitycraft.mixin.datafix;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V4996_1;

/**
 * Teaches data fixers which of SecurityCraft's block entities has a block state in it
 */
@Mixin(V4996_1.class)
public class V4996_1Mixin {
	@Inject(method = "registerBlockEntities", at = @At("TAIL"))
	private void securitycraft$teachSavedBlockState(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir, @Local Map<String, Supplier<TypeTemplate>> map) {
		schema.register(map, "securitycraft:block_change_detector", () -> DSL.optionalFields(
				"filter", References.ITEM_STACK.in(schema),
				"Modules", DSL.list(References.ITEM_STACK.in(schema)),
				"entries", DSL.list(DSL.optionalFields("state", References.BLOCK_STATE.in(schema)))));
		schema.register(map, "securitycraft:projector", () -> DSL.optionalFields(
				"storedItem", References.ITEM_STACK.in(schema),
				"Modules", DSL.list(References.ITEM_STACK.in(schema)),
				"SavedState", References.BLOCK_STATE.in(schema)));
	}
}
