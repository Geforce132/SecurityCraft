package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.CustomModules;

public class ReinforcedButtonTileEntity extends CustomizableTileEntity
{
	public ReinforcedButtonTileEntity()
	{
		super(SCContent.teTypeReinforcedButton);
	}

	@Override
	public CustomModules[] acceptedModules()
	{
		return new CustomModules[] {CustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}