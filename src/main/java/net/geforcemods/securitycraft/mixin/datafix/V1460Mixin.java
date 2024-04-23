package net.geforcemods.securitycraft.mixin.datafix;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1460;

/**
 * Registers SecurityCraft's block entities to datafixers, so the item stacks in them can be fixed.
 */
@Mixin(V1460.class)
public class V1460Mixin {
	@Inject(method = "registerBlockEntities", at = @At("TAIL"))
	private void securitycraft$registerBlockEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci, @Local Map<String, Supplier<TypeTemplate>> map) {
		registerInventory(schema, map, "securitycraft:keypad_chest");
		registerInventory(schema, map, "securitycraft:keypad_furnace");
		registerInventory(schema, map, "securitycraft:keypad_blast_furnace");
		registerInventory(schema, map, "securitycraft:keypad_smoker");
		registerInventory(schema, map, "securitycraft:keypad_barrel");
		//@formatter:off
		schema.register(map, "securitycraft:inventory_scanner", () -> DSL.optionalFields(
				"Items", DSL.list(References.ITEM_STACK.in(schema)),
				"lens", DSL.list(References.ITEM_STACK.in(schema))));
		//@formatter:on
		registerInventory(schema, map, "securitycraft:block_pocket_manager");
		registerInventory(schema, map, "securitycraft:reinforced_hopper");
		registerInventory(schema, map, "securitycraft:reinforced_dropper");
		registerInventory(schema, map, "securitycraft:reinforced_dispenser");
		registerSingleItem(schema, map, "securitycraft:reinforced_lectern", "Book");
		registerInventory(schema, map, "securitycraft:reinforced_chiseled_bookshelf");
		registerSingleItem(schema, map, "securitycraft:block_change_detector", "filter");
		registerSingleItem(schema, map, "securitycraft:projector", "storedItem");
		//@formatter:off
		schema.register(map, "securitycraft:laser_block", () -> DSL.allWithRemainder(
				DSL.optional(DSL.field("lens0", References.ITEM_STACK.in(schema))),
				DSL.optional(DSL.field("lens1", References.ITEM_STACK.in(schema))),
				DSL.optional(DSL.field("lens2", References.ITEM_STACK.in(schema))),
				DSL.optional(DSL.field("lens3", References.ITEM_STACK.in(schema))),
				DSL.optional(DSL.field("lens4", References.ITEM_STACK.in(schema))),
				DSL.optional(DSL.field("lens5", References.ITEM_STACK.in(schema)))));
		//@formatter:on
		registerInventory(schema, map, "securitycraft:security_camera", "lens");
		registerInventory(schema, map, "securitycraft:trophy_system", "lens");
		registerInventory(schema, map, "securitycraft:claymore", "lens");
		registerSingleItem(schema, map, "securitycraft:display_case", "DisplayedStack");
		registerSingleItem(schema, map, "securitycraft:glow_display_case", "DisplayedStack");
	}

	@Unique
	private static void registerSingleItem(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityType, String itemKey) {
		schema.register(map, blockEntityType, name -> DSL.optionalFields(itemKey, References.ITEM_STACK.in(schema)));
	}

	@Unique
	private static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityType) {
		registerInventory(schema, map, blockEntityType, "Items");
	}

	@Unique
	private static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityType, String key) {
		schema.register(map, blockEntityType, () -> DSL.optionalFields(key, DSL.list(References.ITEM_STACK.in(schema))));
	}
}
