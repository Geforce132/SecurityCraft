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

	REDSTONE(SCContent.redstoneModule, "redstone_module", "Redstone module"),
	WHITELIST(SCContent.whitelistModule, "whitelist_module", "Whitelist module"),
	BLACKLIST(SCContent.blacklistModule, "blacklist_module", "Blacklist module"),
	HARMING(SCContent.harmingModule, "harming_module", "Harming module"),
	SMART(SCContent.smartModule, "smart_module", "Smart module"),
	STORAGE(SCContent.storageModule, "storage_module", "Storage module"),
	DISGUISE(SCContent.disguiseModule, "disguise_module", "Disguise module");

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