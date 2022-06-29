package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.ITEEMPAffected;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityIronFence extends TileEntityOwnable implements ITEEMPAffected {
	private boolean shutDown = false;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		shutDown = tag.getBoolean("shutDown");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("shutDown", shutDown);
		return super.writeToNBT(tag);
	}

	@Override
	public boolean isShutDown() {
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}
}
