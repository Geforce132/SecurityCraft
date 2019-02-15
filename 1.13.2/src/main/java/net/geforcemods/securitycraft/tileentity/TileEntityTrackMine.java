package net.geforcemods.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityTrackMine extends TileEntityOwnable
{
	private boolean active = true;

	public void activate()
	{
		if(!active)
		{
			active = true;
			markDirty();
		}
	}

	public void deactivate()
	{
		if(active)
		{
			active = false;
			markDirty();
		}
	}

	public boolean isActive()
	{
		return active;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("TrackMineEnabled", active);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		active = tag.getBoolean("TrackMineEnabled");
	}
}
