package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.items.ItemModule;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableSCTE}.
 * Gives easy access to the module ItemStack and name.
 *
 * @author Geforce
 */
public enum EnumModuleType {

	REDSTONE(SCContent.redstoneModule, "redstone_module"),
	ALLOWLIST(SCContent.allowlistModule, "whitelist_module"),
	DENYLIST(SCContent.denylistModule, "blacklist_module"),
	HARMING(SCContent.harmingModule, "harming_module"),
	SMART(SCContent.smartModule, "smart_module"),
	STORAGE(SCContent.storageModule, "storage_module"),
	DISGUISE(SCContent.disguiseModule, "disguise_module");

	private ItemModule module;
	private String registryPath;

	private EnumModuleType(ItemModule moduleItem, String registryPath){
		module = moduleItem;
		this.registryPath = registryPath;
	}

	public ItemModule getItem() {
		return module;
	}

	public String getTranslationKey() {
		return module.getTranslationKey() + ".name";
	}

	public String getRegistryPath() {
		return registryPath;
	}

	public static void refresh() {
		for(EnumModuleType module : values())
			module.module = (ItemModule) ForgeRegistries.ITEMS.getValue(new ResourceLocation(SecurityCraft.MODID, module.getRegistryPath()));
	}

}