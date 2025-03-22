package net.geforcemods.securitycraft.mixin.datafix;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.geforcemods.securitycraft.components.DataFixHandler;
import net.minecraft.util.datafix.schemas.V1460;

@Mixin(V1460.class)
public class V1460Mixin {
	/**
	 * Registers SecurityCraft's block entities to datafixers, so the item stacks in them can be fixed.
	 */
	@Inject(method = "registerBlockEntities", at = @At("TAIL"))
	private void securitycraft$registerBlockEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci, @Local Map<String, Supplier<TypeTemplate>> map) {
		DataFixHandler.registerBlockEntities(schema, map);
	}

	/**
	 * Registers the SecurityCraft's entities to datafixers so other entities in the same chunk and the items in them can be
	 * fixed properly
	 */
	@Inject(method = "registerEntities", at = @At("TAIL"))
	private void securitycraft$registerEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci, @Local Map<String, Supplier<TypeTemplate>> map) {
		DataFixHandler.registerEntities(schema, map);
	}
}
