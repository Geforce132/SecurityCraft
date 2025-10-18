package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPocketBlockEntity extends OwnableBlockEntity implements ITickingBlockEntity {
	private BlockPocketManagerBlockEntity manager;
	private BlockPos managerPos;

	public BlockPocketBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.BLOCK_POCKET_BLOCK_ENTITY.get(), pos, state);
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
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (manager == null && managerPos != null && level.getBlockEntity(managerPos) instanceof BlockPocketManagerBlockEntity be)
			manager = be;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		if (manager != null)
			tag.putLong("ManagerPos", manager.getBlockPos().asLong());

		super.saveAdditional(tag);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		if (tag.contains("ManagerPos"))
			managerPos = BlockPos.of(tag.getLong("ManagerPos"));
	}
}
