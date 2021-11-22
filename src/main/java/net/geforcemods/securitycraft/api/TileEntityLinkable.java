package net.geforcemods.securitycraft.api;

import java.util.ArrayList;

import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityLinkable extends CustomizableSCTE implements ITickable {
	public ArrayList<LinkedBlock> linkedBlocks = new ArrayList<>();
	private NBTTagList nbtTagStorage = null;

	@Override
	public void update() {
		if(hasWorld() && nbtTagStorage != null) {
			readLinkedBlocks(nbtTagStorage);
			sync();
			nbtTagStorage = null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("linkedBlocks"))
		{
			if(!hasWorld()) {
				nbtTagStorage = tag.getTagList("linkedBlocks", Constants.NBT.TAG_COMPOUND);
				return;
			}

			readLinkedBlocks(tag.getTagList("linkedBlocks", Constants.NBT.TAG_COMPOUND));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		if(hasWorld() && linkedBlocks.size() > 0) {
			NBTTagList tagList = new NBTTagList();

			WorldUtils.addScheduledTask(world, () -> {
				for(int i = linkedBlocks.size() - 1; i >= 0; i--)
				{
					LinkedBlock block = linkedBlocks.get(i);
					NBTTagCompound toAppend = new NBTTagCompound();

					if(block != null) {
						if(!block.validate(world)) {
							linkedBlocks.remove(i);
							continue;
						}

						toAppend.setString("blockName", block.blockName);
						toAppend.setInteger("blockX", block.getX());
						toAppend.setInteger("blockY", block.getY());
						toAppend.setInteger("blockZ", block.getZ());
					}

					tagList.appendTag(toAppend);
				}

				tag.setTag("linkedBlocks", tagList);
			});
		}

		return tag;
	}

	private void readLinkedBlocks(NBTTagList list) {
		for(int i = 0; i < list.tagCount(); i++) {
			String name = list.getCompoundTagAt(i).getString("blockName");
			int x = list.getCompoundTagAt(i).getInteger("blockX");
			int y = list.getCompoundTagAt(i).getInteger("blockY");
			int z = list.getCompoundTagAt(i).getInteger("blockZ");
			LinkedBlock block = new LinkedBlock(name, new BlockPos(x, y, z));

			if(hasWorld() && !block.validate(world)) {
				list.removeTag(i);
				continue;
			}

			if(!linkedBlocks.contains(block))
				link(this, block.asTileEntity(world));
		}
	}

	@Override
	public void invalidate() {
		for(LinkedBlock block : linkedBlocks)
			TileEntityLinkable.unlink(block.asTileEntity(world), this);
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module)
	{
		super.onModuleInserted(stack, module);
		ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_INSERTED, stack, this);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);
		ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_REMOVED, stack, this);
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		createLinkedBlockAction(EnumLinkedAction.OPTION_CHANGED, new Option[]{ option }, this);
	}

	/**
	 * Links two blocks together. Calls onLinkedBlockAction()
	 * whenever certain events (found in {@link EnumLinkedAction}) occur.
	 */
	public static void link(TileEntityLinkable tileEntity1, TileEntityLinkable tileEntity2) {
		if(isLinkedWith(tileEntity1, tileEntity2)) return;

		LinkedBlock block1 = new LinkedBlock(tileEntity1);
		LinkedBlock block2 = new LinkedBlock(tileEntity2);

		if(!tileEntity1.linkedBlocks.contains(block2))
			tileEntity1.linkedBlocks.add(block2);

		if(!tileEntity2.linkedBlocks.contains(block1))
			tileEntity2.linkedBlocks.add(block1);
	}

	/**
	 * Unlinks the second tile entity from the first.
	 *
	 * @param tileEntity1 The tile entity to unlink from
	 * @param tileEntity2 The tile entity to unlink
	 */
	public static void unlink(TileEntityLinkable tileEntity1, TileEntityLinkable tileEntity2) {
		if(tileEntity1 == null || tileEntity2 == null) return;

		LinkedBlock block = new LinkedBlock(tileEntity2);

		if(tileEntity1.linkedBlocks.contains(block))
			tileEntity1.linkedBlocks.remove(block);
	}

	/**
	 * @return Are the two blocks linked together?
	 */
	public static boolean isLinkedWith(TileEntityLinkable tileEntity1, TileEntityLinkable tileEntity2) {
		return tileEntity1.linkedBlocks.contains(new LinkedBlock(tileEntity2)) && tileEntity2.linkedBlocks.contains(new LinkedBlock(tileEntity1));
	}

	/**
	 * Calls onLinkedBlockAction() for every block this tile entity
	 * is linked to. <p>
	 *
	 * <b>NOTE:</b> Never use this method in onLinkedBlockAction(),
	 * use createLinkedBlockAction(EnumLinkedAction, Object[], ArrayList[TileEntityLinkable] instead.
	 *
	 * @param action The action that occurred
	 * @param parameters Action-specific parameters, see comments in {@link EnumLinkedAction}
	 * @param excludedTE The TileEntityLinkable which called this method, prevents infinite loops.
	 */
	public void createLinkedBlockAction(EnumLinkedAction action, Object[] parameters, TileEntityLinkable excludedTE) {
		ArrayList<TileEntityLinkable> list = new ArrayList<>();

		list.add(excludedTE);

		createLinkedBlockAction(action, parameters, list);
	}

	/**
	 * Calls onLinkedBlockAction() for every block this tile entity
	 * is linked to.
	 *
	 * @param action The action that occurred
	 * @param parameters Action-specific parameters, see comments in {@link EnumLinkedAction}
	 * @param excludedTEs TileEntityLinkables that shouldn't have onLinkedBlockAction() called on them,
	 *        prevents infinite loops. Always add your tile entity to the list whenever using this method
	 */
	public void createLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<TileEntityLinkable> excludedTEs) {
		for(LinkedBlock block : linkedBlocks)
			if(excludedTEs.contains(block.asTileEntity(world)))
				continue;
			else {
				block.asTileEntity(world).onLinkedBlockAction(action, parameters, excludedTEs);
				block.asTileEntity(world).sync();
			}
	}

	/**
	 * Called whenever certain actions occur in blocks
	 * this tile entity is linked to. See {@link EnumLinkedAction}
	 * for parameter descriptions. <p>
	 *
	 * @param action The {@link EnumLinkedAction} that occurred
	 * @param parameters Important variables related to the action
	 * @param excludedTEs TileEntityLinkables that aren't going to have onLinkedBlockAction() called on them,
	 *        always add your tile entity to the list if you're going to call createLinkedBlockAction() in this method to chain-link multiple blocks (i.e: like Laser Blocks)
	 */
	protected void onLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<TileEntityLinkable> excludedTEs) {}
}
