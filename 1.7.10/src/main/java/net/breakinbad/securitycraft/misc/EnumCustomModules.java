package net.breakinbad.securitycraft.misc;

import net.breakinbad.securitycraft.items.ItemModule;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;

/**
 * Simple enum that is supposed to be used in conjunction with {@link CustomizableSCTE}.
 * Gives easy access to the module ItemStack and name.
 * 
 * @author Geforce
 */
public enum EnumCustomModules {
		
	REDSTONE(mod_SecurityCraft.redstoneModule, "Redstone module"),
	WHITELIST(mod_SecurityCraft.whitelistModule, "Whitelist module"),
	BLACKLIST(mod_SecurityCraft.blacklistModule, "Blacklist module"),
	HARMING(mod_SecurityCraft.harmingModule, "Harming module"),
	SMART(mod_SecurityCraft.smartModule, "Smart module"),
	STORAGE(mod_SecurityCraft.storageModule, "Storage module");
	
	private final ItemModule correspondingStack;
	private final String moduleName;

	private EnumCustomModules(ItemModule correspondingStack, String name){
		this.correspondingStack = correspondingStack;
		this.moduleName = name;
	}
	
	public ItemModule getCorrespondingStack(){
		return this.correspondingStack;
	}
	
	public String getModuleName(){
		return this.moduleName;
	}

}
