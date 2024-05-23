package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public abstract class LinkableBlockEntity extends CustomizableBlockEntity implements ITickable {
	private List<LinkedBlock> linkedBlocks = new ArrayList<>();
	private NBTTagList nbtTagStorage = null;

	@Override
	public void update() {
		if (hasWorld() && nbtTagStorage != null) {
			readLinkedBlocks(nbtTagStorage);
			sync();
			nbtTagStorage = null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if (tag.hasKey("linkedBlocks")) {
			if (!hasWorld()) {
				nbtTagStorage = tag.getTagList("linkedBlocks", Constants.NBT.TAG_COMPOUND);
				return;
			}

			readLinkedBlocks(tag.getTagList("linkedBlocks", Constants.NBT.TAG_COMPOUND));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		if (!linkedBlocks.isEmpty()) {
			NBTTagList tagList = new NBTTagList();

			for (LinkedBlock block : linkedBlocks) {
				NBTTagCompound toAppend = new NBTTagCompound();

				if (block != null) {
					toAppend.setString("blockName", block.getBlockName());
					toAppend.setInteger("blockX", block.getX());
					toAppend.setInteger("blockY", block.getY());
					toAppend.setInteger("blockZ", block.getZ());
				}

				tagList.appendTag(toAppend);
			}

			tag.setTag("linkedBlocks", tagList);
		}

		return tag;
	}

	private void readLinkedBlocks(NBTTagList list) {
		for (int i = 0; i < list.tagCount(); i++) {
			String name = list.getCompoundTagAt(i).getString("blockName");
			int x = list.getCompoundTagAt(i).getInteger("blockX");
			int y = list.getCompoundTagAt(i).getInteger("blockY");
			int z = list.getCompoundTagAt(i).getInteger("blockZ");
			LinkedBlock block = new LinkedBlock(name, new BlockPos(x, y, z));

			if (hasWorld() && !block.validate(world)) {
				list.removeTag(i);
				continue;
			}

			if (hasWorld() && world.isBlockLoaded(block.getPos()) && block.validate(world) && !linkedBlocks.contains(block))
				link(this, block.asTileEntity(world));
		}
	}

	@Override
	public void invalidate() {
		for (LinkedBlock block : linkedBlocks) {
			if (world.isBlockLoaded(block.getPos()))
				LinkableBlockEntity.unlink(block.asTileEntity(world), this);
		}

		super.invalidate();
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		propagate(new ILinkedAction.OptionChanged<>(option), this);
	}

	/**
	 * Links two blocks together. Calls onLinkedBlockAction() whenever certain events (found in {@link ILinkedAction}) occur.
	 */
	public static void link(LinkableBlockEntity tileEntity1, LinkableBlockEntity tileEntity2) {
		if (isLinkedWith(tileEntity1, tileEntity2))
			return;

		LinkedBlock block1 = new LinkedBlock(tileEntity1);
		LinkedBlock block2 = new LinkedBlock(tileEntity2);

		if (!tileEntity1.linkedBlocks.contains(block2))
			tileEntity1.linkedBlocks.add(block2);

		if (!tileEntity2.linkedBlocks.contains(block1))
			tileEntity2.linkedBlocks.add(block1);
	}

	/**
	 * Unlinks the second tile entity from the first.
	 *
	 * @param tileEntity1 The tile entity to unlink from
	 * @param tileEntity2 The tile entity to unlink
	 */
	public static void unlink(LinkableBlockEntity tileEntity1, LinkableBlockEntity tileEntity2) {
		if (tileEntity1 == null || tileEntity2 == null)
			return;

		LinkedBlock block = new LinkedBlock(tileEntity2);

		if (tileEntity1.linkedBlocks.contains(block))
			tileEntity1.linkedBlocks.remove(block);
	}

	/**
	 * @return Are the two blocks linked together?
	 */
	public static boolean isLinkedWith(LinkableBlockEntity tileEntity1, LinkableBlockEntity tileEntity2) {
		return tileEntity1.linkedBlocks.contains(new LinkedBlock(tileEntity2)) && tileEntity2.linkedBlocks.contains(new LinkedBlock(tileEntity1));
	}

	/**
	 * Calls onLinkedBlockAction() for every block this tile entity is linked to. <p> <b>NOTE:</b> Never use this method in
	 * onLinkedBlockAction(), use propagate(EnumLinkedAction, Object[], ArrayList[TileEntityLinkable] instead.
	 *
	 * @param action The action that occurred
	 * @param excludedTE The TileEntityLinkable which called this method, prevents infinite loops.
	 */
	public void propagate(ILinkedAction action, LinkableBlockEntity excludedTE) {
		ArrayList<LinkableBlockEntity> list = new ArrayList<>();

		list.add(excludedTE);
		propagate(action, list);
	}

	/**
	 * Calls onLinkedBlockAction() for every valid block this block entity is linked to, and removes invalid blocks from the
	 * linked block list.
	 *
	 * @param action The action that occurred
	 * @param excludedTEs TileEntityLinkables that shouldn't have onLinkedBlockAction() called on them, prevents infinite loops.
	 *            Always add your tile entity to the list whenever using this method
	 */
	public void propagate(ILinkedAction action, List<LinkableBlockEntity> excludedTEs) {
		Iterator<LinkedBlock> linkedBlockIterator = linkedBlocks.iterator();

		while (linkedBlockIterator.hasNext()) {
			LinkedBlock block = linkedBlockIterator.next();
			LinkableBlockEntity linkedTe = block.asTileEntity(world);

			if (world.isBlockLoaded(block.getPos()) && !excludedTEs.contains(linkedTe)) {
				if (block.validate(world)) {
					linkedTe.onLinkedBlockAction(action, excludedTEs);

					if (!world.isRemote)
						linkedTe.sync();
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
	 * @param excludedTEs TileEntityLinkables that aren't going to have onLinkedBlockAction() called on them, always add your
	 *            tile entity to the list if you're going to call propagate() in this method to chain-link multiple blocks (i.e:
	 *            like Laser Blocks)
	 */
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedTEs) {}
}
