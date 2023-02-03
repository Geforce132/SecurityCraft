package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.IEMPAffectedBE;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.nbt.NBTTagCompound;

public class IronFenceBlockEntity extends OwnableBlockEntity implements IEMPAffectedBE {
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
