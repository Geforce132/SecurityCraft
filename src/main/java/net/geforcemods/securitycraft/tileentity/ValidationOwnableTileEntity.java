package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class ValidationOwnableTileEntity extends OwnableTileEntity {
	public ValidationOwnableTileEntity() {
		super(SCContent.teTypeValidationOwnable);
	}

	@Override
	public boolean needsValidation() {
		return true;
	}
}
