package net.geforcemods.securitycraft.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;

public interface ITEEMPAffected extends IEMPAffected {
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
