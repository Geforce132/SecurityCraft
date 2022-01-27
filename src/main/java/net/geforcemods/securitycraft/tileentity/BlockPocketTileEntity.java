package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockPocketTileEntity extends OwnableTileEntity implements ITickableTileEntity {
	private BlockPocketManagerTileEntity manager;
	private BlockPos managerPos;

	public BlockPocketTileEntity() {
		super(SCContent.teTypeBlockPocket);
	}

	public void setManager(BlockPocketManagerTileEntity manager) {
		this.manager = manager;
		managerPos = manager.getBlockPos();
	}

	public void removeManager() {
		managerPos = null;
		manager = null;
	}

	public BlockPocketManagerTileEntity getManager() {
		return manager;
	}

	@Override
	public void tick() {
		if (manager == null && managerPos != null) {
			TileEntity te = level.getBlockEntity(managerPos);

			if (te instanceof BlockPocketManagerTileEntity)
				manager = (BlockPocketManagerTileEntity) te;
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
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		if (tag.contains("ManagerPos"))
			managerPos = BlockPos.of(tag.getLong("ManagerPos"));
	}
}
