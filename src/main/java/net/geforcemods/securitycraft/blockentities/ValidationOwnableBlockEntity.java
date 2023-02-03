package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;

public class ValidationOwnableBlockEntity extends OwnableBlockEntity {
	@Override
	public boolean needsValidation() {
		return true;
	}
}
