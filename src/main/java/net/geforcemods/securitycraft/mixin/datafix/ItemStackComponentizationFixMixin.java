package net.geforcemods.securitycraft.mixin.datafix;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.serialization.Dynamic;

import net.geforcemods.securitycraft.components.KeycardData;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;

/**
 * Makes sure SecurityCraft's briefcase, keycard holder, and disguise module get inserted items converted properly
 */
@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
	@Unique
	private static final Set<String> BRIEFCASE_OR_KEYCARD_HOLDER_OR_DISGUISE_MODULE = Set.of("securitycraft:briefcase", "securitycraft:keycard_holder", "securitycraft:disguise_module");
	private static final Set<String> KEYCARDS = Set.of("securitycraft:keycard_lv1", "securitycraft:keycard_lv2", "securitycraft:keycard_lv3", "securitycraft:keycard_lv4", "securitycraft:keycard_lv5");

	@Inject(method = "fixItemStack", at = @At("TAIL"))
	private static void securitycraft$fixItemStacks(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic, CallbackInfo ci) {
		if (itemStackData.is(BRIEFCASE_OR_KEYCARD_HOLDER_OR_DISGUISE_MODULE)) {
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

		if (itemStackData.is(KEYCARDS))
			fixKeycard(itemStackData, dynamic);
	}

	@Unique
	private static void fixKeycard(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic) {
		boolean linked = itemStackData.removeTag("linked").asBoolean(KeycardData.DEFAULT.linked());
		int signature = itemStackData.removeTag("signature").asInt(KeycardData.DEFAULT.signature());
		boolean limited = itemStackData.removeTag("limited").asBoolean(KeycardData.DEFAULT.limited());
		int usesLeft = itemStackData.removeTag("uses").asInt(KeycardData.DEFAULT.usesLeft());
		String ownerName = itemStackData.removeTag("ownerName").asString(KeycardData.DEFAULT.ownerName());
		String ownerUUID = itemStackData.removeTag("ownerUUID").asString(KeycardData.DEFAULT.ownerUUID());

		//@formatter:off
		itemStackData.setComponent("securitycraft:keycard_data", dynamic.emptyMap()
				.set("linked", dynamic.createBoolean(linked))
				.set("signature", dynamic.createInt(signature))
				.set("limited", dynamic.createBoolean(limited))
				.set("uses_left", dynamic.createInt(usesLeft))
				.set("owner_name", dynamic.createString(ownerName))
				.set("owner_uuid", dynamic.createString(ownerUUID)));
		//@formatter:on
	}
}
