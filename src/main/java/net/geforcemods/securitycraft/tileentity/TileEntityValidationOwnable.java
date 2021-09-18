package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.TileEntityOwnable;

public class TileEntityValidationOwnable extends TileEntityOwnable {

	@Override
	public boolean needsValidation() {
		return true;
	}
}
