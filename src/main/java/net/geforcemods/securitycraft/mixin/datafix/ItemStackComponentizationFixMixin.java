package net.geforcemods.securitycraft.mixin.datafix;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.serialization.Dynamic;

import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;

/**
 * Makes sure SecurityCraft's briefcase, keycard holder, and disguise module get inserted items converted properly
 */
@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
	@Unique
	private static final Set<String> BRIEFCASE_OR_KEYCARD_HOLDER_OR_DISGUISE_MODULE = Set.of("securitycraft:briefcase", "securitycraft:keycard_holder", "securitycraft:disguise_module");

	@Inject(method = "fixItemStack", at = @At("TAIL"))
	private static void securitycraft$fixItemStack(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic, CallbackInfo ci) {
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
	}
}
