package net.geforcemods.securitycraft.api;

import net.minecraft.tileentity.TileEntity;

/**
 * Default implementation of {@link IEMPAffected} for a block entity, adding synchronization when shutting down and
 * reactivating.
 */
public interface IEMPAffectedBE extends IEMPAffected {
	@Override
	public default void shutDown() {
		TileEntity be = (TileEntity) this;

		IEMPAffected.super.shutDown();

		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
	}

	@Override
	public default void reactivate() {
		TileEntity be = (TileEntity) this;

		IEMPAffected.super.reactivate();

		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
	}
}
