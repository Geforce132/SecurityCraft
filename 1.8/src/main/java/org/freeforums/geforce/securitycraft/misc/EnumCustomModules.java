package org.freeforums.geforce.securitycraft.misc;

import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

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
