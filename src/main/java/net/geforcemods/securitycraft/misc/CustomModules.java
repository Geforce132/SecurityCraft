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

	REDSTONE(SCContent.REDSTONE_MODULE, "redstone_module", "Redstone module"),
	WHITELIST(SCContent.WHITELIST_MODULE, "whitelist_module", "Whitelist module"),
	BLACKLIST(SCContent.BLACKLIST_MODULE, "blacklist_module", "Blacklist module"),
	HARMING(SCContent.HARMING_MODULE, "harming_module", "Harming module"),
	SMART(SCContent.SMART_MODULE, "smart_module", "Smart module"),
	STORAGE(SCContent.STORAGE_MODULE, "storage_module", "Storage module"),
	DISGUISE(SCContent.DISGUISE_MODULE, "disguise_module", "Disguise module");

	private Supplier<ModuleItem> module;
	private String moduleUnlocalizedName;
	private String moduleLocalizedName;

	private CustomModules(Supplier<ModuleItem> moduleItem, String unlocalizedName, String localizedName){
		module = moduleItem;
		moduleUnlocalizedName = unlocalizedName;
		moduleLocalizedName = localizedName;
	}

	public ModuleItem getItem() {
		return module.get();
	}

	public String getTranslationKey() {
		return moduleUnlocalizedName;
	}

	public String getName() {
		return moduleLocalizedName;
	}

	public static CustomModules getModuleFromStack(ItemStack item) {
		if(item.isEmpty() || item.getItem() == null) return null;

		for(CustomModules module : values())
			if(module.getItem() == item.getItem())
				return module;

		return null;
	}

}