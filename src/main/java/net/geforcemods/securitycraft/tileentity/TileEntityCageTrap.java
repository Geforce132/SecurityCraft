package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.misc.EnumModuleType;

public class TileEntityCageTrap extends TileEntityDisguisable {

	private OptionBoolean shouldCaptureMobsOption = new OptionBoolean("captureMobs", false);

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[]{EnumModuleType.DISGUISE, EnumModuleType.ALLOWLIST};
	}

	public boolean capturesMobs()
	{
		return shouldCaptureMobsOption.get();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] { shouldCaptureMobsOption };
	}

}
