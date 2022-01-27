package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;

public class ValidationOwnableBlockEntity extends OwnableTileEntity {
	public ValidationOwnableBlockEntity() {
		super(SCContent.beTypeValidationOwnable);
	}

	@Override
	public boolean needsValidation() {
		return true;
	}
}
