package net.geforcemods.securitycraft.misc;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.items.ModuleItem;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableTileEntity}.
 * Gives easy access to the module ItemStack and name.
 *
 * @author Geforce
 */
public enum ModuleType {

	REDSTONE(SCContent.REDSTONE_MODULE),
	ALLOWLIST(SCContent.ALLOWLIST_MODULE),
	DENYLIST(SCContent.DENYLIST_MODULE),
	HARMING(SCContent.HARMING_MODULE),
	SMART(SCContent.SMART_MODULE),
	STORAGE(SCContent.STORAGE_MODULE),
	DISGUISE(SCContent.DISGUISE_MODULE),
	SPEED(SCContent.SPEED_MODULE);

	private Supplier<ModuleItem> module;

	private ModuleType(Supplier<ModuleItem> moduleItem){
		module = moduleItem;
	}

	public ModuleItem getItem() {
		return module.get();
	}

	public String getTranslationKey() {
		return getItem().getDescriptionId();
	}

}