package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.items.ItemModule;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableSCTE}.
 * Gives easy access to the module ItemStack and name.
 *
 * @author Geforce
 */
public enum EnumCustomModules {

	REDSTONE(SCContent.redstoneModule, "redstoneModule", "Redstone module"),
	WHITELIST(SCContent.whitelistModule, "whitelistModule", "Whitelist module"),
	BLACKLIST(SCContent.blacklistModule, "blacklistModule", "Blacklist module"),
	HARMING(SCContent.harmingModule, "harmingModule", "Harming module"),
	SMART(SCContent.smartModule, "smartModule", "Smart module"),
	STORAGE(SCContent.storageModule, "storageModule", "Storage module"),
	DISGUISE(SCContent.disguiseModule, "disguiseModule", "Disguise module");

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
		if(item == null || item.getItem() == null) return null;

		for(EnumCustomModules module : values())
			if(module.getItem() == item.getItem())
				return module;

		return null;
	}

	public static void refresh() {
		for(EnumCustomModules module : values())
			module.module = (ItemModule) GameRegistry.findItem("securitycraft", module.getUnlocalizedName());
	}

}