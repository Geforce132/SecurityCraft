package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public abstract class LinkableBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity {
	private final List<LinkedBlock> linkedBlocks = new ArrayList<>();
	private ListNBT nbtTagStorage = null;

	protected LinkableBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public void tick() {
		if (hasLevel() && nbtTagStorage != null) {
			readLinkedBlocks(nbtTagStorage);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
			nbtTagStorage = null;
		}
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		if (tag.contains("linkedBlocks")) {
			if (!hasLevel()) {
				nbtTagStorage = tag.getList("linkedBlocks", Constants.NBT.TAG_COMPOUND);
				return;
			}

			readLinkedBlocks(tag.getList("linkedBlocks", Constants.NBT.TAG_COMPOUND));
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		if (!linkedBlocks.isEmpty()) {
			ListNBT tagList = new ListNBT();

			for (LinkedBlock block : linkedBlocks) {
				CompoundNBT toAppend = new CompoundNBT();

				if (block != null) {
					toAppend.putString("blockName", block.getBlockName());
					toAppend.putInt("blockX", block.getX());
					toAppend.putInt("blockY", block.getY());
					toAppend.putInt("blockZ", block.getZ());
				}

				tagList.add(toAppend);
			}

			tag.put("linkedBlocks", tagList);
		}

		return tag;
	}

	@Override
	public void setRemoved() {
		for (LinkedBlock block : linkedBlocks) {
			if (level.isLoaded(block.getPos()))
				LinkableBlockEntity.unlink(block.asBlockEntity(level), this);
		}

		super.setRemoved();
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		propagate(new ILinkedAction.OptionChanged<>(option), this);
	}

	private void readLinkedBlocks(ListNBT list) {
		for (int i = 0; i < list.size(); i++) {
			String name = list.getCompound(i).getString("blockName");
			int x = list.getCompound(i).getInt("blockX");
			int y = list.getCompound(i).getInt("blockY");
			int z = list.getCompound(i).getInt("blockZ");
			LinkedBlock block = new LinkedBlock(name, new BlockPos(x, y, z));

			if (hasLevel() && level.isLoaded(block.getPos()) && block.validate(level) && !linkedBlocks.contains(block))
				link(this, block.asBlockEntity(level));
		}
	}

	/**
	 * Links two blocks together. Calls onLinkedBlockAction() whenever certain events (found in {@link ILinkedAction}) occur.
	 */
	public static void link(LinkableBlockEntity blockEntity1, LinkableBlockEntity blockEntity2) {
		if (isLinkedWith(blockEntity1, blockEntity2))
			return;

		LinkedBlock block1 = new LinkedBlock(blockEntity1);
		LinkedBlock block2 = new LinkedBlock(blockEntity2);

		if (!blockEntity1.linkedBlocks.contains(block2))
			blockEntity1.linkedBlocks.add(block2);

		if (!blockEntity2.linkedBlocks.contains(block1))
			blockEntity2.linkedBlocks.add(block1);
	}

	/**
	 * Unlinks the second tile entity from the first.
	 *
	 * @param blockEntity1 The tile entity to unlink from
	 * @param blockEntity2 The tile entity to unlink
	 */
	public static void unlink(LinkableBlockEntity blockEntity1, LinkableBlockEntity blockEntity2) {
		if (blockEntity1 == null || blockEntity2 == null)
			return;

		LinkedBlock block = new LinkedBlock(blockEntity2);

		if (blockEntity1.linkedBlocks.contains(block))
			blockEntity1.linkedBlocks.remove(block);
	}

	/**
	 * @return Are the two blocks linked together?
	 */
	public static boolean isLinkedWith(LinkableBlockEntity blockEntity1, LinkableBlockEntity blockEntity2) {
		return blockEntity1.linkedBlocks.contains(new LinkedBlock(blockEntity2)) && blockEntity2.linkedBlocks.contains(new LinkedBlock(blockEntity1));
	}

	/**
	 * Calls onLinkedBlockAction() for every block this tile entity is linked to. <p> <b>NOTE:</b> Never use this method in
	 * onLinkedBlockAction(), use propagate(EnumILinkedAction, Object[], ArrayList[LinkableTileEntity] instead.
	 *
	 * @param action The action that occurred
	 * @param excludedBE The LinkableTileEntity which called this method, prevents infinite loops.
	 */
	public void propagate(ILinkedAction action, LinkableBlockEntity excludedBE) {
		List<LinkableBlockEntity> list = new ArrayList<>();

		list.add(excludedBE);
		propagate(action, list);
	}

	/**
	 * Calls onLinkedBlockAction() for every valid block this block entity is linked to, and removes invalid blocks from the
	 * linked block list.
	 *
	 * @param action The action that occurred
	 * @param excludedBEs LinkableBlockEntities that shouldn't have onLinkedBlockAction() called on them, prevents infinite loops.
	 *            Always add your tile entity to the list whenever using this method
	 */
	public void propagate(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {
		Iterator<LinkedBlock> linkedBlockIterator = linkedBlocks.iterator();

		while (linkedBlockIterator.hasNext()) {
			LinkedBlock block = linkedBlockIterator.next();

			if (level.isLoaded(block.getPos()) && !excludedBEs.contains(block.asBlockEntity(level))) {
				if (block.validate(level)) {
					BlockState state = level.getBlockState(block.getPos());

					block.asBlockEntity(level).onLinkedBlockAction(action, excludedBEs);
					level.sendBlockUpdated(block.getPos(), state, state, 3);
				}
				else
					linkedBlockIterator.remove();
			}
		}
	}

	/**
	 * Called whenever certain actions occur in blocks this tile entity is linked to. See {@link ILinkedAction} for parameter
	 * descriptions. <p>
	 *
	 * @param action The {@link ILinkedAction} that occurred
	 * @param excludedBEs LinkableBlockEntities that aren't going to have onLinkedBlockAction() called on them, always add your
	 *            tile entity to the list if you're going to call propagate() in this method to chain-link multiple
	 *            blocks (i.e: like Laser Blocks)
	 */
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {}
}
