package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class ReinforcedIronBarsTileEntity extends OwnableTileEntity
{
	private boolean canDrop = true;

	public ReinforcedIronBarsTileEntity()
	{
		super(SCContent.teTypeReinforcedIronBars);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag)
	{
		tag.putBoolean("canDrop", canDrop);
		return super.save(tag);
	}

	@Override
	public void load(BlockState state, CompoundNBT tag)
	{
		super.load(state, tag);
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
