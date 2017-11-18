package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableSCTE}.
 * Gives easy access to the module ItemStack and name.
 *
 * @author Geforce
 */
public enum EnumCustomModules {

	REDSTONE(mod_SecurityCraft.redstoneModule, "redstone_module", "Redstone module"),
	WHITELIST(mod_SecurityCraft.whitelistModule, "whitelist_module", "Whitelist module"),
	BLACKLIST(mod_SecurityCraft.blacklistModule, "blacklist_module", "Blacklist module"),
	HARMING(mod_SecurityCraft.harmingModule, "harming_module", "Harming module"),
	SMART(mod_SecurityCraft.smartModule, "smart_module", "Smart module"),
	STORAGE(mod_SecurityCraft.storageModule, "storage_module", "Storage module"),
	DISGUISE(mod_SecurityCraft.disguiseModule, "disguise_module", "Disguise module");

	private ItemModule module;
	private String moduleUnlocalizedName;
	private String moduleLocalizedName;

	private EnumCustomModules(ItemModule moduleItem, String unlocalizedName, String localizedName){
		module = moduleItem;
		moduleUnlocalizedName = unlocalizedName;
		moduleLocalizedName = localizedName;
	}

	public ItemModule getItem() {
		return module;
	}

	public String getUnlocalizedName() {
		return moduleUnlocalizedName;
	}

	public String getName() {
		return moduleLocalizedName;
	}

	public static EnumCustomModules getModuleFromStack(ItemStack item) {
		if(item.isEmpty() || item.getItem() == null) return null;

		for(EnumCustomModules module : values())
			if(module.getItem() == item.getItem())
				return module;

		return null;
	}

	public static void refresh() {
		for(EnumCustomModules module : values())
			module.module = (ItemModule) Item.REGISTRY.getObject(new ResourceLocation("securitycraft" + ":" + module.getUnlocalizedName()));
	}

}