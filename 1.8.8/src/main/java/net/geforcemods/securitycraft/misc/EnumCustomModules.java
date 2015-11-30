package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableSCTE}.
 * Gives easy access to the module ItemStack and name.
 * 
 * @author Geforce
 */
public enum EnumCustomModules {
		
	REDSTONE(mod_SecurityCraft.redstoneModule, "redstoneModule", "Redstone module"),
	WHITELIST(mod_SecurityCraft.whitelistModule, "whitelistModule", "Whitelist module"),
	BLACKLIST(mod_SecurityCraft.blacklistModule, "blacklistModule", "Blacklist module"),
	HARMING(mod_SecurityCraft.harmingModule, "harmingModule", "Harming module"),
	SMART(mod_SecurityCraft.smartModule, "smartModule", "Smart module"),
	STORAGE(mod_SecurityCraft.storageModule, "storageModule", "Storage module");
	
	private ItemModule module;
	private String moduleUnlocalizedName;
	private String moduleLocalizedName;

	private EnumCustomModules(ItemModule moduleItem, String unlocalizedName, String localizedName){
		this.module = moduleItem;
		this.moduleUnlocalizedName = unlocalizedName;
		this.moduleLocalizedName = localizedName;
	}
	
	public ItemModule getItem() {
		return this.module;
	}
	
	public String getUnlocalizedName() {
		return this.moduleUnlocalizedName;
	}
	
	public String getName() {
		return this.moduleLocalizedName;
	}
	
	public static EnumCustomModules getModuleFromStack(ItemStack item) {
		if(item == null || item.getItem() == null) return null;
		
		for(EnumCustomModules module : values()) {
			if(module.getItem() == item.getItem()) {
				return module;
			}
		}
		
		return null;
	}
	
	public static void refresh() {
		for(EnumCustomModules module : values()) {
			module.module = (ItemModule) GameRegistry.findItem("securitycraft", module.getUnlocalizedName());
		}
	}

}