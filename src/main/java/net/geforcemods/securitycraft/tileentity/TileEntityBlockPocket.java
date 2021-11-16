package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityBlockPocket extends TileEntitySCTE
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

		if(world.isBlockLoaded(pos) && manager != null)
		{
			Block block = world.getBlockState(pos).getBlock();

			if(block != SCContent.blockPocketWall && block != SCContent.reinforcedCrystalQuartz)
				manager.disableMultiblock();
		}
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
}
