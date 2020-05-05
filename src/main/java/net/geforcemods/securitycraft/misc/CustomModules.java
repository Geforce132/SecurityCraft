package net.geforcemods.securitycraft.misc;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.item.ItemStack;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableTileEntity}.
 * Gives easy access to the module ItemStack and name.
 *
 * @author Geforce
 */
public enum CustomModules {

	REDSTONE(SCContent.REDSTONE_MODULE),
	WHITELIST(SCContent.WHITELIST_MODULE),
	BLACKLIST(SCContent.BLACKLIST_MODULE),
	HARMING(SCContent.HARMING_MODULE),
	SMART(SCContent.SMART_MODULE),
	STORAGE(SCContent.STORAGE_MODULE),
	DISGUISE(SCContent.DISGUISE_MODULE);

	private Supplier<ModuleItem> module;

	private CustomModules(Supplier<ModuleItem> moduleItem){
		module = moduleItem;
	}

	public ModuleItem getItem() {
		return module.get();
	}

	public String getTranslationKey() {
		return getItem().getTranslationKey();
	}

	public static CustomModules getModuleFromStack(ItemStack item) {
		if(item.isEmpty() || item.getItem() == null) return null;

		for(CustomModules module : values())
			if(module.getItem() == item.getItem())
				return module;

		return null;
	}

}