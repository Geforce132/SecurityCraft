package net.geforcemods.securitycraft.api;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEMPAffectedBE extends IEMPAffected {
	@Override
	public default void shutDown() {
		BlockEntity be = (BlockEntity) this;

		IEMPAffected.super.shutDown();

		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
	}

	@Override
	public default void reactivate() {
		BlockEntity be = (BlockEntity) this;

		IEMPAffected.super.reactivate();

		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
	}
}
