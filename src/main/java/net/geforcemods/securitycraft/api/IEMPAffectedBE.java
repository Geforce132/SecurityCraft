package net.geforcemods.securitycraft.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Default implementation of {@link IEMPAffected} for a block entity, adding synchronization when shutting down and
 * reactivating.
 */
public interface IEMPAffectedBE extends IEMPAffected {
	@Override
	public default void shutDown() {
		TileEntity te = (TileEntity) this;

		IEMPAffected.super.shutDown();

		if (!te.getWorld().isRemote)
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());
	}

	@Override
	public default void reactivate() {
		TileEntity te = (TileEntity) this;

		IEMPAffected.super.reactivate();

		if (!te.getWorld().isRemote)
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());
	}
}
