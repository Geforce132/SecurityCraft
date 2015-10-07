package net.breakinbad.securitycraft.tileentity;

import net.breakinbad.securitycraft.api.CustomizableSCTE;
import net.breakinbad.securitycraft.misc.EnumCustomModules;

public class TileEntitySecurityCamera extends CustomizableSCTE {
   
	public EnumCustomModules[] getCustomizableOptions(){
		return new EnumCustomModules[] { EnumCustomModules.REDSTONE };
	}

	public String[] getOptionDescriptions() {
		return new String[] { "Lets the camera emit a 15-block redstone signal when enabled." };
	}
	
}
