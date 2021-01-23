package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityReinforcedIronBars extends TileEntityOwnable
{
	private boolean canDrop = true;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("canDrop", canDrop);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
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
