package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityBlockPocket extends CustomizableSCTE
{
	private TileEntityBlockPocketManager manager;

	public void setManager(TileEntityBlockPocketManager manager)
	{
		this.manager = manager;
	}

	public void removeManager()
	{
		manager = null;
	}

	public TileEntityBlockPocketManager getManager()
	{
		return manager;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();

		if(manager != null)
			manager.disableMultiblock();
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();

		if(manager != null)
			manager.disableMultiblock();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		if(manager != null)
			tag.setLong("ManagerPos", manager.getPos().toLong());
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if(tag.hasKey("ManagerPos"))
		{
			TileEntity te = world.getTileEntity(BlockPos.fromLong(tag.getLong("ManagerPos")));

			if(te instanceof TileEntityBlockPocketManager)
				manager = (TileEntityBlockPocketManager)te;
		}
	}

	@Override
	public EnumCustomModules[] acceptedModules()
	{
		return new EnumCustomModules[] {};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
