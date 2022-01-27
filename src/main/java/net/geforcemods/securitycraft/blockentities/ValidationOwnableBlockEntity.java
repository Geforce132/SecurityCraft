package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;

public class ValidationOwnableBlockEntity extends OwnableBlockEntity {
	public ValidationOwnableBlockEntity() {
		super(SCContent.beTypeValidationOwnable);
	}

	@Override
	public boolean needsValidation() {
		return true;
	}
}
