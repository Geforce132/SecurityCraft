package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockPocketBlockEntity extends OwnableBlockEntity implements ITickableTileEntity {
	private BlockPocketManagerBlockEntity manager;
	private BlockPos managerPos;

	public BlockPocketBlockEntity() {
		super(SCContent.BLOCK_POCKET_BLOCK_ENTITY.get());
	}

	public void setManager(BlockPocketManagerBlockEntity manager) {
		this.manager = manager;
		managerPos = manager.getBlockPos();
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	public void removeManager() {
		managerPos = null;
		manager = null;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	public BlockPocketManagerBlockEntity getManager() {
		return manager;
	}

	@Override
	public void tick() {
		if (manager == null && managerPos != null) {
			TileEntity te = level.getBlockEntity(managerPos);

			if (te instanceof BlockPocketManagerBlockEntity)
				manager = (BlockPocketManagerBlockEntity) te;
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		if (manager != null)
			tag.putLong("ManagerPos", manager.getBlockPos().asLong());

		return super.save(tag);
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		if (tag.contains("ManagerPos"))
			managerPos = BlockPos.of(tag.getLong("ManagerPos"));
	}
}
