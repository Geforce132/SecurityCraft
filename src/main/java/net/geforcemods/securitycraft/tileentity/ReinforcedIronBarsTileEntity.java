package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedIronBarsTileEntity extends OwnableTileEntity
{
	private boolean canDrop = true;

	public ReinforcedIronBarsTileEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeReinforcedIronBars, pos, state);
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
