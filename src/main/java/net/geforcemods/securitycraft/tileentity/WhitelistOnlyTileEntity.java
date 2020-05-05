package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;

public class WhitelistOnlyTileEntity extends CustomizableTileEntity
{
	public WhitelistOnlyTileEntity()
	{
		super(SCContent.teTypeWhitelistOnly);
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {ModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}