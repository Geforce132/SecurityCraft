package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityBlockPocket extends CustomizableSCTE
{
	private TileEntityBlockPocketManager manager;
	private BlockPos managerPos;

	public void setManager(TileEntityBlockPocketManager manager)
	{
		this.manager = manager;
		managerPos = manager.getPos();
	}

	public void removeManager()
	{
		managerPos = null;
		manager = null;
	}

	public TileEntityBlockPocketManager getManager()
	{
		return manager;
	}

	@Override
	public void update()
	{
		super.update();

		if(manager == null && managerPos != null)
		{
			TileEntity te = world.getTileEntity(managerPos);

			if(te instanceof TileEntityBlockPocketManager)
				manager = (TileEntityBlockPocketManager)te;
		}
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();

		if(manager != null && world.getBlockState(pos).getBlock() != SCContent.blockPocketWall && world.getBlockState(pos).getBlock() != SCContent.reinforcedCrystalQuartz)
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
			managerPos = BlockPos.fromLong(tag.getLong("ManagerPos"));
	}

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
