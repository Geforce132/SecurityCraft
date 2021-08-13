package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.SecurityCraftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPocketBlockEntity extends SecurityCraftBlockEntity
{
	private BlockPocketManagerBlockEntity manager;
	private BlockPos managerPos;

	public BlockPocketBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeBlockPocket, pos, state);
	}

	public void setManager(BlockPocketManagerBlockEntity manager)
	{
		this.manager = manager;
		managerPos = manager.getBlockPos();
	}

	public void removeManager()
	{
		managerPos = null;
		manager = null;
	}

	public BlockPocketManagerBlockEntity getManager()
	{
		return manager;
	}

	public static void tick(Level world, BlockPos pos, BlockState state, BlockPocketBlockEntity te)
	{
		SecurityCraftBlockEntity.tick(world, pos, state, te);

		if(te.manager == null && te.managerPos != null)
		{
			if(world.getBlockEntity(te.managerPos) instanceof BlockPocketManagerBlockEntity manager)
				te.manager = manager;
		}
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();

		BlockState state = level.getBlockState(worldPosition);

		if(manager != null && state.getBlock() != SCContent.BLOCK_POCKET_WALL.get() && state.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get() && state.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
			manager.disableMultiblock();
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		if(manager != null)
			tag.putLong("ManagerPos", manager.getBlockPos().asLong());
		return super.save(tag);
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		if(tag.contains("ManagerPos"))
			managerPos = BlockPos.of(tag.getLong("ManagerPos"));
	}
}
