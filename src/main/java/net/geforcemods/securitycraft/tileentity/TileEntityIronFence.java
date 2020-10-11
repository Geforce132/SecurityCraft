package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.minecraft.tileentity.TileEntity;

public class TileEntityIronFence extends TileEntitySCTE implements IEMPAffected
{
	private boolean shutDown = false;

	@Override
	public void update()
	{
		if(!shutDown)
			super.update();
	}

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
