package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.nbt.CompoundNBT;

public class TrackMineTileEntity extends OwnableTileEntity
{
	private boolean active = true;

	public TrackMineTileEntity()
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
	public CompoundNBT write(CompoundNBT tag)
	{
		tag.putBoolean("TrackMineEnabled", active);
		return super.write(tag);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);
		active = tag.getBoolean("TrackMineEnabled");
	}
}
