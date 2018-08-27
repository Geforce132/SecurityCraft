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
	public NBTTagCompound writeToNBT(NBTTagCompound par1nbtTagCompound)
	{
		par1nbtTagCompound.setBoolean("TrackMineEnabled", active);
		return super.writeToNBT(par1nbtTagCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound)
	{
		super.readFromNBT(par1nbtTagCompound);
		active = par1nbtTagCompound.getBoolean("TrackMineEnabled");
	}
}
