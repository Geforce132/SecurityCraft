package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueInput.TypedInputList;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueOutput.TypedOutputList;

public abstract class LinkableBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	protected List<LinkedBlock> linkedBlocks = new ArrayList<>();
	private TypedInputList<LinkedBlock> nbtTagStorage = null;

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
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);

		if (!hasLevel()) {
			nbtTagStorage = tag.listOrEmpty("linkedBlocks", LinkedBlock.NEW_OR_LEGACY_CODEC);
			return;
		}

		readLinkedBlocks(tag.listOrEmpty("linkedBlocks", LinkedBlock.NEW_OR_LEGACY_CODEC));
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);

		if (!linkedBlocks.isEmpty()) {
			TypedOutputList<LinkedBlock> tagList = tag.list("linkedBlocks", LinkedBlock.CODEC);

			linkedBlocks.forEach(tagList::add);
		}
	}

	@Override
	public void setRemoved() {
		for (LinkedBlock block : linkedBlocks) {
			if (level.isLoaded(block.pos()))
				LinkableBlockEntity.unlink(block.asBlockEntity(level), this);
		}

		super.setRemoved();
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		propagate(new ILinkedAction.OptionChanged<>(option), this);
		super.onOptionChanged(option);
	}

	private void readLinkedBlocks(TypedInputList<LinkedBlock> list) {
		for (LinkedBlock block : list) {
			if (hasLevel() && level.isLoaded(block.pos()) && block.validate(level) && !linkedBlocks.contains(block))
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

			if (level.isLoaded(block.pos()) && !excludedBEs.contains(block.asBlockEntity(level))) {
				if (block.validate(level)) {
					BlockState state = level.getBlockState(block.pos());

					block.asBlockEntity(level).onLinkedBlockAction(action, excludedBEs);
					level.sendBlockUpdated(block.pos(), state, state, 3);
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
