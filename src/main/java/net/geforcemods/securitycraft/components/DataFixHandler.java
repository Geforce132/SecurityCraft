package net.geforcemods.securitycraft.components;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;

import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.util.datafix.fixes.References;

public class DataFixHandler {
	private static final Set<String> KEYCARD_HOLDER_OR_DISGUISE_MODULE = Set.of("securitycraft:keycard_holder", "securitycraft:disguise_module");
	private static final Set<String> KEYCARDS = Set.of("securitycraft:keycard_lv1", "securitycraft:keycard_lv2", "securitycraft:keycard_lv3", "securitycraft:keycard_lv4", "securitycraft:keycard_lv5");

	private DataFixHandler() {}

	public static void fix(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic) {
		if (itemStackData.is("securitycraft:briefcase"))
			fixBriefcase(itemStackData, dynamic);

		if (itemStackData.is(KEYCARD_HOLDER_OR_DISGUISE_MODULE))
			fixItemInventory(itemStackData, dynamic);

		if (itemStackData.is(KEYCARDS))
			fixKeycard(itemStackData, dynamic);

		if (itemStackData.is("securitycraft:codebreaker"))
			fixCodebreaker(itemStackData, dynamic);
	}

	public static void registerBlockEntities(Schema schema, Map<String, Supplier<TypeTemplate>> map) {
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

	public static void registerSentry(Schema schema, Map<String, Supplier<TypeTemplate>> map) {
		//@formatter:off
		schema.register(map, "securitycraft:sentry", () -> DSL.optionalFields(
				"InstalledWhitelist", References.ITEM_STACK.in(schema),
				"InstalledModule", References.ITEM_STACK.in(schema)));
		//@formatter:on
	}

	private static void fixBriefcase(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic) {
		String passcode = itemStackData.removeTag("passcode").asString("");
		IntStream saltKey = itemStackData.removeTag("saltKey").asIntStream();

		if (!passcode.isEmpty()) {
			//@formatter:off
			itemStackData.setComponent("securitycraft:passcode_data", dynamic.emptyMap()
					.set("passcode", dynamic.createString(passcode))
					.set("salt_key", dynamic.createIntList(saltKey)));
			//@formatter:on
		}

		fixOwner(itemStackData, dynamic, "owner", true);
		fixItemInventory(itemStackData, dynamic);
	}

	private static void fixItemInventory(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic) {
		//@formatter:off
		List<Dynamic<?>> list = dynamic.get("ItemInventory")
				.asList(d -> d.emptyMap()
						.set("slot", d.createInt(d.get("Slot").asByte((byte) 0) & 255))
						.set("item", d.remove("Slot")));
		//@formatter:on

		if (!list.isEmpty())
			itemStackData.setComponent("minecraft:container", dynamic.createList(list.stream()));

		itemStackData.removeTag("ItemInventory");
	}

	private static void fixKeycard(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic) {
		if (itemStackData.removeTag("linked").asBoolean(false)) {
			int signature = itemStackData.removeTag("signature").asInt(KeycardData.DEFAULT.signature());
			boolean limited = itemStackData.removeTag("limited").asBoolean(KeycardData.DEFAULT.limited());
			int usesLeft = itemStackData.removeTag("uses").asInt(KeycardData.DEFAULT.usesLeft());

			//@formatter:off
			itemStackData.setComponent("securitycraft:keycard_data", dynamic.emptyMap()
					.set("signature", dynamic.createInt(signature))
					.set("limited", dynamic.createBoolean(limited))
					.set("uses_left", dynamic.createInt(usesLeft)));
			//@formatter:on
			fixOwner(itemStackData, dynamic, "ownerName", false);
		}
	}

	private static void fixOwner(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic, String ownerKey, boolean showInTooltip) {
		String ownerName = itemStackData.removeTag(ownerKey).asString(OwnerData.DEFAULT.name());
		String ownerUUID = itemStackData.removeTag("ownerUUID").asString(OwnerData.DEFAULT.uuid());

		//@formatter:off
		itemStackData.setComponent("securitycraft:owner", dynamic.emptyMap()
				.set("name", dynamic.createString(ownerName))
				.set("uuid", dynamic.createString(ownerUUID))
				.set("show_in_tooltip", dynamic.createBoolean(showInTooltip)));
		//@formatter:on
	}

	private static void fixCodebreaker(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic) {
		long lastUsedTime = itemStackData.removeTag("last_used_time").asLong(CodebreakerData.DEFAULT.lastUsedTime());
		boolean wasSuccessful = itemStackData.removeTag("was_successful").asBoolean(CodebreakerData.DEFAULT.wasSuccessful());

		//@formatter:off
		itemStackData.setComponent("securitycraft:codebreaker_data", dynamic.emptyMap()
				.set("last_used_time", dynamic.createLong(lastUsedTime))
				.set("was_successful", dynamic.createBoolean(wasSuccessful)));
		//@formatter:on
	}

	private static void registerSingleItem(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityType, String itemKey) {
		schema.register(map, blockEntityType, name -> DSL.optionalFields(itemKey, References.ITEM_STACK.in(schema)));
	}

	private static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityType) {
		registerInventory(schema, map, blockEntityType, "Items");
	}

	private static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityType, String key) {
		schema.register(map, blockEntityType, () -> DSL.optionalFields(key, DSL.list(References.ITEM_STACK.in(schema))));
	}
}
