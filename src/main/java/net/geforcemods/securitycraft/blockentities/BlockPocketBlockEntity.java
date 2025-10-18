package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class BlockPocketBlockEntity extends OwnableBlockEntity implements ITickable {
	private BlockPocketManagerBlockEntity manager;
	private BlockPos managerPos;

	public void setManager(BlockPocketManagerBlockEntity manager) {
		IBlockState state = world.getBlockState(pos);

		this.manager = manager;
		managerPos = manager.getPos();
		markDirty();
		world.notifyBlockUpdate(pos, state, state, 3);
	}

	public void removeManager() {
		IBlockState state = world.getBlockState(pos);

		managerPos = null;
		manager = null;
		markDirty();
		world.notifyBlockUpdate(pos, state, state, 3);
	}

	public BlockPocketManagerBlockEntity getManager() {
		return manager;
	}

	@Override
	public void update() {
		if (manager == null && managerPos != null) {
			TileEntity te = world.getTileEntity(managerPos);

			if (te instanceof BlockPocketManagerBlockEntity)
				manager = (BlockPocketManagerBlockEntity) te;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		if (manager != null)
			tag.setLong("ManagerPos", manager.getPos().toLong());

		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if (tag.hasKey("ManagerPos"))
			managerPos = BlockPos.fromLong(tag.getLong("ManagerPos"));
	}
}
