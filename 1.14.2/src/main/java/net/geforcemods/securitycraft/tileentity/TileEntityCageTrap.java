package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.misc.EnumCustomModules;

public class TileEntityCageTrap extends CustomizableSCTE {

	private OptionBoolean shouldCaptureMobsOption = new OptionBoolean("captureMobs", false) {
		@Override
		public void toggle() {
			setValue(!getValue());
		}
	};

	public TileEntityCageTrap()
	{
		super(SCContent.teTypeCageTrap);
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] { shouldCaptureMobsOption };
	}

}
