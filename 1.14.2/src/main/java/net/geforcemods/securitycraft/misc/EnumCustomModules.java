package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.items.ItemModule;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableSCTE}.
 * Gives easy access to the module ItemStack and name.
 *
 * @author Geforce
 */
public enum EnumCustomModules {

	REDSTONE(SCContent.redstoneModule, "redstone_module", "Redstone module"),
	WHITELIST(SCContent.whitelistModule, "whitelist_module", "Whitelist module"),
	BLACKLIST(SCContent.blacklistModule, "blacklist_module", "Blacklist module"),
	HARMING(SCContent.harmingModule, "harming_module", "Harming module"),
	SMART(SCContent.smartModule, "smart_module", "Smart module"),
	STORAGE(SCContent.storageModule, "storage_module", "Storage module"),
	DISGUISE(SCContent.disguiseModule, "disguise_module", "Disguise module");

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

	public String getTranslationKey() {
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
			module.module = (ItemModule) ForgeRegistries.ITEMS.getValue(new ResourceLocation("securitycraft" + ":" + module.getTranslationKey()));
	}

}