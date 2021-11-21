package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.minecraft.tileentity.TileEntity;

public class TileEntityIronFence extends TileEntityOwnable implements IEMPAffected
{
	private boolean shutDown = false;

	@Override
	public boolean isShutDown()
	{
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown)
	{
		this.shutDown = shutDown;
	}

	@Override
	public TileEntity getTileEntity()
	{
		return this;
	}
}
