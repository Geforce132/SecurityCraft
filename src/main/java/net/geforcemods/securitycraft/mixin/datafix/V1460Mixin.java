package net.geforcemods.securitycraft.mixin.datafix;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;

import net.geforcemods.securitycraft.components.DataFixHandler;
import net.minecraft.util.datafix.fixes.References;
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

	/**
	 * Captures the Schema method parameter necessary for the mixin below.
	 */
	@Inject(method = "lambda$registerTypes$35", at = @At("HEAD"))
	private static void securitycraft$captureSchema(Schema schema, CallbackInfoReturnable<TypeTemplate> callbackInfo, @Share("schema") LocalRef<Schema> schemaRef) {
		schemaRef.set(schema);
	}

	/**
	 * Adds "ItemInventory" to the list of item inventory NBT paths that should get fixed by datafixers, so the items stored in
	 * there keep their data when Minecraft updates. This is necessary for proper conversion of the inventories of briefcases,
	 * keycard holders and disguise modules.
	 */
	@SuppressWarnings("unchecked")
	@ModifyArg(method = "lambda$registerTypes$35", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/DSL;optionalFields([Lcom/mojang/datafixers/util/Pair;)Lcom/mojang/datafixers/types/templates/TypeTemplate;"))
	private static Pair<String, TypeTemplate>[] securitycraft$registerCustomInventoryTag(Pair<String, TypeTemplate>[] originalFields, @Share("schema") LocalRef<Schema> schemaRef) {
		List<Pair<String, TypeTemplate>> newFields = Lists.newArrayList(originalFields);

		newFields.add(Pair.of("ItemInventory", DSL.list(References.ITEM_STACK.in(schemaRef.get()))));
		return newFields.toArray(new Pair[0]);
	}
}
