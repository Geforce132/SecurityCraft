package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.items.ItemModule;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableSCTE}.
 * Gives easy access to the module ItemStack and name.
 *
 * @author Geforce
 */
public enum EnumCustomModules {

	REDSTONE(SCContent.redstoneModule, "redstone_module"),
	WHITELIST(SCContent.whitelistModule, "whitelist_module"),
	BLACKLIST(SCContent.blacklistModule, "blacklist_module"),
	HARMING(SCContent.harmingModule, "harming_module"),
	SMART(SCContent.smartModule, "smart_module"),
	STORAGE(SCContent.storageModule, "storage_module"),
	DISGUISE(SCContent.disguiseModule, "disguise_module");

	private ItemModule module;
	private String registryPath;

	private EnumCustomModules(ItemModule moduleItem, String registryPath){
		module = moduleItem;
		this.registryPath = registryPath;
	}

	public ItemModule getItem() {
		return module;
	}

	public String getTranslationKey() {
		return module.getTranslationKey();
	}

	public String getRegistryPath() {
		return registryPath;
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
			module.module = (ItemModule) ForgeRegistries.ITEMS.getValue(new ResourceLocation(SecurityCraft.MODID, module.getRegistryPath()));
	}

}