package net.geforcemods.securitycraft.api;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Default implementation of {@link IEMPAffected} for a block entity, adding synchronization when shutting down and
 * reactivating.
 */
public interface IEMPAffectedBE extends IEMPAffected {
	@Override
	public default void shutDown() {
		BlockEntity be = (BlockEntity) this;

		IEMPAffected.super.shutDown();

		if (!be.getLevel().isClientSide())
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
	}

	@Override
	public default void reactivate() {
		BlockEntity be = (BlockEntity) this;

		IEMPAffected.super.reactivate();

		if (!be.getLevel().isClientSide())
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
	}
}
