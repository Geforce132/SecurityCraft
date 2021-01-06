package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;

public class TileEntityCageTrap extends TileEntityDisguisable {

	private OptionBoolean shouldCaptureMobsOption = new OptionBoolean("captureMobs", false);

	public boolean capturesMobs()
	{
		return shouldCaptureMobsOption.get();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] { shouldCaptureMobsOption };
	}

}
