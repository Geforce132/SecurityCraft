package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.CustomModules;

public class CageTrapTileEntity extends CustomizableTileEntity {

	private BooleanOption shouldCaptureMobsOption = new BooleanOption("captureMobs", false) {
		@Override
		public void toggle() {
			setValue(!getValue());
		}
	};

	public CageTrapTileEntity()
	{
		super(SCContent.teTypeCageTrap);
	}

	@Override
	public CustomModules[] acceptedModules() {
		return new CustomModules[]{};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] { shouldCaptureMobsOption };
	}

}
