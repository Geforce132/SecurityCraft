package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockPocketBlockEntity extends OwnableTileEntity implements ITickableTileEntity {
	private BlockPocketManagerBlockEntity manager;
	private BlockPos managerPos;

	public BlockPocketBlockEntity() {
		super(SCContent.beTypeBlockPocket);
	}

	public void setManager(BlockPocketManagerBlockEntity manager) {
		this.manager = manager;
		managerPos = manager.getBlockPos();
	}

	public void removeManager() {
		managerPos = null;
		manager = null;
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
	public void setRemoved() {
		super.setRemoved();

		if (level.hasChunkAt(worldPosition) && manager != null && !(level.getBlockState(worldPosition).getBlock() instanceof IBlockPocket))
			manager.disableMultiblock();
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		if (manager != null)
			tag.putLong("ManagerPos", manager.getBlockPos().asLong());

		return super.save(tag);
	}

	@Override
	public void load(CompoundNBT tag) {
		super.load(tag);

		if (tag.contains("ManagerPos"))
			managerPos = BlockPos.of(tag.getLong("ManagerPos"));
	}
}
