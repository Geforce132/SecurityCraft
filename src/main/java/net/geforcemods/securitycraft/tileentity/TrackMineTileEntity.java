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
	public void func_230337_a_(BlockState state, CompoundNBT tag)
	{
		super.func_230337_a_(state, tag);
		active = tag.getBoolean("TrackMineEnabled");
	}
}
