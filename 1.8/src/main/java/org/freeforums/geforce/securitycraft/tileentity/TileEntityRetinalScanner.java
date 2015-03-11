package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.enums.EnumCustomModules;


public class TileEntityRetinalScanner extends CustomizableSCTE {

	protected EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

}
