package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;

public class TileEntityCageTrap extends TileEntityDisguisable {

	private OptionBoolean shouldCaptureMobsOption = new OptionBoolean("captureMobs", false) {
		@Override
		public void toggle() {
			setValue(!getValue());
		}
	};

	public boolean capturesMobs()
	{
		return shouldCaptureMobsOption.asBoolean();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] { shouldCaptureMobsOption };
	}

}
