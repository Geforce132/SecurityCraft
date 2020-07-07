package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockPocketTileEntity extends SecurityCraftTileEntity
{
	private BlockPocketManagerTileEntity manager;
	private BlockPos managerPos;

	public BlockPocketTileEntity()
	{
		super(SCContent.teTypeBlockPocket);
	}

	public void setManager(BlockPocketManagerTileEntity manager)
	{
		this.manager = manager;
		managerPos = manager.getPos();
	}

	public void removeManager()
	{
		managerPos = null;
		manager = null;
	}

	public BlockPocketManagerTileEntity getManager()
	{
		return manager;
	}

	@Override
	public void tick()
	{
		super.tick();

		if(manager == null && managerPos != null)
		{
			TileEntity te = world.getTileEntity(managerPos);

			if(te instanceof BlockPocketManagerTileEntity)
				manager = (BlockPocketManagerTileEntity)te;
		}
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();

		BlockState state = world.getBlockState(pos);

		if(manager != null && state.getBlock() != SCContent.BLOCK_POCKET_WALL.get() && state.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get() && state.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
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
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		if(tag.contains("ManagerPos"))
			managerPos = BlockPos.fromLong(tag.getLong("ManagerPos"));
	}
}
