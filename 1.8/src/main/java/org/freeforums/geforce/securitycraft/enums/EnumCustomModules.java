package org.freeforums.geforce.securitycraft.enums;

import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public enum EnumCustomModules {
		
	REDSTONE(mod_SecurityCraft.redstoneModule),
	WHITELIST(mod_SecurityCraft.whitelistModule),
	BLACKLIST(mod_SecurityCraft.blacklistModule),
	HARMING(mod_SecurityCraft.harmingModule),
	SMART(mod_SecurityCraft.smartModule);
	
	private final ItemModule correspondingStack;

	private EnumCustomModules(ItemModule correspondingStack){
		this.correspondingStack = correspondingStack;
	}
	
	public ItemModule getCorrespondingStack(){
		return this.correspondingStack;
	}

}
