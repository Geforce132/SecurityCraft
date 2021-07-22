package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.block.BlockState;
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
			setChanged();
		}
	}

	public void deactivate()
	{
		if(active)
		{
			active = false;
			setChanged();
		}
	}

	public boolean isActive()
	{
		return active;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag)
	{
		tag.putBoolean("TrackMineEnabled", active);
		return super.save(tag);
	}

	@Override
	public void load(BlockState state, CompoundNBT tag)
	{
		super.load(state, tag);
		active = tag.getBoolean("TrackMineEnabled");
	}
}
