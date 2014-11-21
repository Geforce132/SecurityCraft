package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;


public class TileEntityRetinalScanner extends CustomizableSCTE {

	protected EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

}
