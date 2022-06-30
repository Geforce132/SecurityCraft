package net.geforcemods.securitycraft.api;

import net.minecraft.tileentity.TileEntity;

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
