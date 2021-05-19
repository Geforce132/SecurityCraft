package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumModuleType;

public class TileEntityAllowlistOnly extends CustomizableSCTE
{
	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[] {EnumModuleType.ALLOWLIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
