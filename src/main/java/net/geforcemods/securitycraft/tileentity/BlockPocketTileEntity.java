package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockPocketTileEntity extends CustomizableTileEntity
{
	private BlockPocketManagerTileEntity manager;

	public BlockPocketTileEntity()
	{
		super(SCContent.teTypeBlockPocket);
	}

	public void setManager(BlockPocketManagerTileEntity manager)
	{
		this.manager = manager;
	}

	public void removeManager()
	{
		manager = null;
	}

	public BlockPocketManagerTileEntity getManager()
	{
		return manager;
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();

		if(manager != null)
			manager.disableMultiblock();
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		if(manager != null)
			tag.putLong("ManagerPos", manager.getPos().toLong());
		return super.write(tag);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);

		if(tag.contains("ManagerPos"))
		{
			TileEntity te = world.getTileEntity(BlockPos.fromLong(tag.getLong("ManagerPos")));

			if(te instanceof BlockPocketManagerTileEntity)
				manager = (BlockPocketManagerTileEntity)te;
		}
	}

	@Override
	public CustomModules[] acceptedModules()
	{
		return new CustomModules[] {};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
