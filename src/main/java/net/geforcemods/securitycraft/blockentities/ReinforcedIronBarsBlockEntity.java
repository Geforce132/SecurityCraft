package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedIronBarsBlockEntity extends OwnableBlockEntity
{
	private boolean canDrop = true;

	public ReinforcedIronBarsBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeReinforcedIronBars, pos, state);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		tag.putBoolean("canDrop", canDrop);
		return super.save(tag);
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		canDrop = tag.getBoolean("canDrop");
	}

	public boolean canDrop()
	{
		return canDrop;
	}

	public void setCanDrop(boolean canDrop)
	{
		this.canDrop = canDrop;
	}
}
