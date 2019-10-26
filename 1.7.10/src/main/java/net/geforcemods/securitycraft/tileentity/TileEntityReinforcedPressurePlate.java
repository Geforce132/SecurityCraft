package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumCustomModules;

public class TileEntityReinforcedPressurePlate extends CustomizableSCTE
{
	@Override
	public EnumCustomModules[] acceptedModules()
	{
		return new EnumCustomModules[] {EnumCustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
