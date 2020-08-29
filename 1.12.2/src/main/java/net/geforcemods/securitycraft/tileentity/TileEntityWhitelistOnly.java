package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumModuleType;

public class TileEntityWhitelistOnly extends CustomizableSCTE
{
	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[] {EnumModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
