package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;

public class CageTrapTileEntity extends DisguisableTileEntity {

	private BooleanOption shouldCaptureMobsOption = new BooleanOption("captureMobs", false) {
		@Override
		public void toggle() {
			setValue(!get());
		}
	};

	public CageTrapTileEntity()
	{
		super(SCContent.teTypeCageTrap);
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
