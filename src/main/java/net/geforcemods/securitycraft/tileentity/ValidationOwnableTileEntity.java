package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;

public class ValidationOwnableTileEntity extends OwnableTileEntity {
	public ValidationOwnableTileEntity() {
		super(SCContent.teTypeValidationOwnable);
	}

	@Override
	public boolean needsValidation() {
		return true;
	}
}
