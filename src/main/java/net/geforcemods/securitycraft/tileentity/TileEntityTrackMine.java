package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityTrackMine extends TileEntityOwnable
{
	private boolean active = true;

	public TileEntityTrackMine()
	{
		super(SCContent.teTypeTrackMine);
	}

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
	public NBTTagCompound write(NBTTagCompound tag)
	{
		tag.putBoolean("TrackMineEnabled", active);
		return super.write(tag);
	}

	@Override
	public void read(NBTTagCompound tag)
	{
		super.read(tag);
		active = tag.getBoolean("TrackMineEnabled");
	}
}
