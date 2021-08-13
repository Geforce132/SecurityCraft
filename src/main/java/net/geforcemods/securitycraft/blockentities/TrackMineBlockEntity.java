package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TrackMineBlockEntity extends OwnableBlockEntity
{
	private boolean active = true;

	public TrackMineBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeTrackMine, pos, state);
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
	public CompoundTag save(CompoundTag tag)
	{
		tag.putBoolean("TrackMineEnabled", active);
		return super.save(tag);
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		active = tag.getBoolean("TrackMineEnabled");
	}
}
