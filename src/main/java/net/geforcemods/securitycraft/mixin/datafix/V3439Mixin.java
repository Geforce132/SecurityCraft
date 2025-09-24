package net.geforcemods.securitycraft.mixin.datafix;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V3439;

/**
 * Makes sure Minecraft correctly fixes secret (hanging) signs' text data
 */
@Mixin(V3439.class)
public class V3439Mixin extends NamespacedSchema {
	private V3439Mixin(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Shadow
	public static TypeTemplate sign(Schema schema) {
		throw new UnsupportedOperationException("Shadowing sign method failed!");
	}

	@Inject(method = "registerBlockEntities", at = @At("TAIL"))
	private void securitycraft$registerSigns(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci, @Local Map<String, Supplier<TypeTemplate>> map) {
		register(map, "securitycraft:secret_sign", () -> sign(schema));
		register(map, "securitycraft:secret_hanging_sign", () -> sign(schema));
	}
}
