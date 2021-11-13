package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ValidationOwnableBlockEntity extends OwnableBlockEntity {

	public ValidationOwnableBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.beTypeValidationOwnable,  pos, state);
	}

	@Override
	public boolean needsValidation() {
		return true;
	}
}
