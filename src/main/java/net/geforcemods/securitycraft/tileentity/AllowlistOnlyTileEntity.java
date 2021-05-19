package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;

public class AllowlistOnlyTileEntity extends CustomizableTileEntity
{
	public AllowlistOnlyTileEntity()
	{
		super(SCContent.teTypeAllowlistOnly);
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {ModuleType.ALLOWLIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}