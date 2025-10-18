package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class LinkableBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	protected List<LinkedBlock> linkedBlocks = new ArrayList<>();
	private ListTag nbtTagStorage = null;

	protected LinkableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (hasLevel() && nbtTagStorage != null) {
			readLinkedBlocks(nbtTagStorage);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
			nbtTagStorage = null;
		}
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		if (tag.contains("linkedBlocks")) {
			if (!hasLevel()) {
				nbtTagStorage = tag.getListOrEmpty("linkedBlocks");
				return;
			}

			readLinkedBlocks(tag.getListOrEmpty("linkedBlocks"));
		}
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		if (!linkedBlocks.isEmpty()) {
			ListTag tagList = new ListTag();

			for (LinkedBlock block : linkedBlocks) {
				CompoundTag toAppend = new CompoundTag();

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
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		unlinkFromAllLinked(this);
		super.preRemoveSideEffects(pos, state);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		propagate(new ILinkedAction.OptionChanged<>(option), this);
		super.onOptionChanged(option);
	}

	private void readLinkedBlocks(ListTag list) {
		for (int i = 0; i < list.size(); i++) {
			CompoundTag tag = list.getCompoundOrEmpty(i);

			String name = tag.getStringOr("blockName", "");
			int x = tag.getIntOr("blockX", 0);
			int y = tag.getIntOr("blockY", 0);
			int z = tag.getIntOr("blockZ", 0);
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

		if (!blockEntity1.linkedBlocks.contains(block2)) {
			blockEntity1.linkedBlocks.add(block2);
			blockEntity1.setChanged();
		}

		if (!blockEntity2.linkedBlocks.contains(block1)) {
			blockEntity2.linkedBlocks.add(block1);
			blockEntity2.setChanged();
		}
	}

	/**
	 * Unlinks the second block entity from the first.
	 *
	 * @param blockEntity1 The block entity to unlink from
	 * @param blockEntity2 The block entity to unlink
	 */
	public static void unlink(LinkableBlockEntity blockEntity1, LinkableBlockEntity blockEntity2) {
		if (blockEntity1 == null || blockEntity2 == null)
			return;

		LinkedBlock block = new LinkedBlock(blockEntity2);

		if (blockEntity1.linkedBlocks.contains(block)) {
			blockEntity1.linkedBlocks.remove(block);
			blockEntity1.setChanged();
		}
	}

	/**
	 * Unlinks the block entity from all blocks that it is linked to.
	 *
	 * @param blockEntity The block entity to unlink from all blocks that it is linked to
	 */
	public static void unlinkFromAllLinked(LinkableBlockEntity blockEntity) {
		if (blockEntity == null)
			return;

		Level level = blockEntity.level;

		for (LinkedBlock block : blockEntity.linkedBlocks) {
			if (level.isLoaded(block.getPos()))
				LinkableBlockEntity.unlink(block.asBlockEntity(level), blockEntity);
		}

		blockEntity.setChanged();
	}

	/**
	 * @return Are the two blocks linked together?
	 */
	public static boolean isLinkedWith(LinkableBlockEntity blockEntity1, LinkableBlockEntity blockEntity2) {
		return blockEntity1.linkedBlocks.contains(new LinkedBlock(blockEntity2)) && blockEntity2.linkedBlocks.contains(new LinkedBlock(blockEntity1));
	}

	/**
	 * Calls onLinkedBlockAction() for every block this block entity is linked to. <p> <b>NOTE:</b> Never use this method in
	 * onLinkedBlockAction(), use propagate(EnumLinkedAction, Object[], ArrayList[LinkableBlockEntity] instead.
	 *
	 * @param action The action that occurred
	 * @param excludedBE The LinkableBlockEntity which called this method, prevents infinite loops.
	 */
	public void propagate(ILinkedAction action, LinkableBlockEntity excludedBE) {
		ArrayList<LinkableBlockEntity> list = new ArrayList<>();

		list.add(excludedBE);
		propagate(action, list);
	}

	/**
	 * Calls onLinkedBlockAction() for every valid block this block entity is linked to, and removes invalid blocks from the
	 * linked block list.
	 *
	 * @param action The action that occurred
	 * @param excludedBEs LinkableBlockEntities that shouldn't have onLinkedBlockAction() called on them, prevents infinite
	 *            loops. Always add your block entity to the list whenever using this method
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
	 * Called whenever certain actions occur in blocks this block entity is linked to. See {@link ILinkedAction} for parameter
	 * descriptions. <p>
	 *
	 * @param action The {@link ILinkedAction} that occurred
	 * @param excludedBEs LinkableBlockEntities that aren't going to have onLinkedBlockAction() called on them, always add your
	 *            block entity to the list if you're going to call propagate() in this method to chain-link multiple blocks (i.e:
	 *            like Laser Blocks)
	 */
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {}
}
