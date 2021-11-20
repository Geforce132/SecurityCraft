package net.geforcemods.securitycraft.api;

import java.util.ArrayList;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Constants;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableBlockEntity extends NamedBlockEntity implements IModuleInventory, ICustomizable, ITickingBlockEntity
{
	private boolean linkable = false;
	public ArrayList<LinkedBlock> linkedBlocks = new ArrayList<>();
	private ListTag nbtTagStorage = null;
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

	public CustomizableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if(hasLevel() && nbtTagStorage != null) {
			readLinkedBlocks(nbtTagStorage);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
			nbtTagStorage = null;
		}
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		modules = readModuleInventory(tag);
		readOptions(tag);

		if (tag.contains("linkable"))
			linkable = tag.getBoolean("linkable");

		if (linkable && tag.contains("linkedBlocks"))
		{
			if(!hasLevel()) {
				nbtTagStorage = tag.getList("linkedBlocks", Constants.NBT.TAG_COMPOUND);
				return;
			}

			readLinkedBlocks(tag.getList("linkedBlocks", Constants.NBT.TAG_COMPOUND));
		}
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		writeModuleInventory(tag);
		writeOptions(tag);
		tag.putBoolean("linkable", linkable);

		if(linkable && hasLevel() && linkedBlocks.size() > 0) {
			ListTag tagList = new ListTag();

			WorldUtils.addScheduledTask(level, () -> {
				for(int i = linkedBlocks.size() - 1; i >= 0; i--)
				{
					LinkedBlock block = linkedBlocks.get(i);
					CompoundTag toAppend = new CompoundTag();

					if(block != null) {
						if(!block.validate(level)) {
							linkedBlocks.remove(i);
							continue;
						}

						toAppend.putString("blockName", block.blockName);
						toAppend.putInt("blockX", block.getX());
						toAppend.putInt("blockY", block.getY());
						toAppend.putInt("blockZ", block.getZ());
					}

					tagList.add(toAppend);
				}

				tag.put("linkedBlocks", tagList);
			});
		}

		return tag;
	}

	private void readLinkedBlocks(ListTag list) {
		if(!linkable) return;

		for(int i = 0; i < list.size(); i++) {
			String name = list.getCompound(i).getString("blockName");
			int x = list.getCompound(i).getInt("blockX");
			int y = list.getCompound(i).getInt("blockY");
			int z = list.getCompound(i).getInt("blockZ");

			LinkedBlock block = new LinkedBlock(name, new BlockPos(x, y, z));
			if(hasLevel() && !block.validate(level)) {
				list.remove(i);
				continue;
			}

			if(!linkedBlocks.contains(block))
				link(this, block.asTileEntity(level));
		}
	}

	@Override
	public void setRemoved() {
		if(linkable)
			for(LinkedBlock block : linkedBlocks)
				CustomizableBlockEntity.unlink(block.asTileEntity(level), this);
	}

	@Override
	public BlockEntity getTileEntity()
	{
		return this;
	}

	@Override
	public NonNullList<ItemStack> getInventory()
	{
		return modules;
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		IModuleInventory.super.onModuleInserted(stack, module);
		ModuleUtils.createLinkedAction(LinkedAction.MODULE_INSERTED, stack, this);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		IModuleInventory.super.onModuleRemoved(stack, module);
		ModuleUtils.createLinkedAction(LinkedAction.MODULE_REMOVED, stack, this);
	}

	/**
	 * Sets this TileEntity able to be "linked" with other blocks,
	 * and being able to do things between them. Call CustomizableSCTE.link()
	 * to link two blocks together.
	 */
	public CustomizableBlockEntity linkable() {
		linkable = true;
		return this;
	}

	/**
	 * @return If this TileEntity is able to be linked with.
	 */
	public boolean canBeLinkedWith() {
		return linkable;
	}

	/**
	 * Links two blocks together. Calls onLinkedBlockAction()
	 * whenever certain events (found in {@link LinkedAction}) occur.
	 */
	public static void link(CustomizableBlockEntity tileEntity1, CustomizableBlockEntity tileEntity2) {
		if(!tileEntity1.linkable || !tileEntity2.linkable) return;
		if(isLinkedWith(tileEntity1, tileEntity2)) return;

		LinkedBlock block1 = new LinkedBlock(tileEntity1);
		LinkedBlock block2 = new LinkedBlock(tileEntity2);

		if(!tileEntity1.linkedBlocks.contains(block2))
			tileEntity1.linkedBlocks.add(block2);

		if(!tileEntity2.linkedBlocks.contains(block1))
			tileEntity2.linkedBlocks.add(block1);
	}

	/**
	 * Unlinks the second TileEntity from the first.
	 *
	 * @param tileEntity1 The TileEntity to unlink from
	 * @param tileEntity2 The TileEntity to unlink
	 */
	public static void unlink(CustomizableBlockEntity tileEntity1, CustomizableBlockEntity tileEntity2) {
		if(tileEntity1 == null || tileEntity2 == null) return;
		if(!tileEntity1.linkable || !tileEntity2.linkable) return;

		LinkedBlock block = new LinkedBlock(tileEntity2);

		if(tileEntity1.linkedBlocks.contains(block))
			tileEntity1.linkedBlocks.remove(block);
	}

	/**
	 * @return Are the two blocks linked together?
	 */
	public static boolean isLinkedWith(CustomizableBlockEntity tileEntity1, CustomizableBlockEntity tileEntity2) {
		if(!tileEntity1.linkable || !tileEntity2.linkable) return false;

		return tileEntity1.linkedBlocks.contains(new LinkedBlock(tileEntity2)) && tileEntity2.linkedBlocks.contains(new LinkedBlock(tileEntity1));
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		createLinkedBlockAction(LinkedAction.OPTION_CHANGED, new Option[]{ option }, this);
	}

	/**
	 * Calls onLinkedBlockAction() for every block this TileEntity
	 * is linked to. <p>
	 *
	 * <b>NOTE:</b> Never use this method in onLinkedBlockAction(),
	 * use createLinkedBlockAction(EnumLinkedAction, Object[], ArrayList[CustomizableSCTE] instead.
	 *
	 * @param action The action that occurred
	 * @param parameters Action-specific parameters, see comments in {@link LinkedAction}
	 * @param excludedTE The CustomizableSCTE which called this method, prevents infinite loops.
	 */
	public void createLinkedBlockAction(LinkedAction action, Object[] parameters, CustomizableBlockEntity excludedTE) {
		ArrayList<CustomizableBlockEntity> list = new ArrayList<>();

		list.add(excludedTE);

		createLinkedBlockAction(action, parameters, list);
	}

	/**
	 * Calls onLinkedBlockAction() for every block this TileEntity
	 * is linked to.
	 *
	 * @param action The action that occurred
	 * @param parameters Action-specific parameters, see comments in {@link LinkedAction}
	 * @param excludedTEs CustomizableSCTEs that shouldn't have onLinkedBlockAction() called on them,
	 *        prevents infinite loops. Always add your TileEntity to the list whenever using this method
	 */
	public void createLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<CustomizableBlockEntity> excludedTEs) {
		if(!linkable) return;

		for(LinkedBlock block : linkedBlocks)
			if(excludedTEs.contains(block.asTileEntity(level)))
				continue;
			else {
				BlockState state = level.getBlockState(block.getPos());

				block.asTileEntity(level).onLinkedBlockAction(action, parameters, excludedTEs);
				level.sendBlockUpdated(block.getPos(), state, state, 3);
			}
	}

	/**
	 * Called whenever certain actions occur in blocks
	 * this TileEntity is linked to. See {@link LinkedAction}
	 * for parameter descriptions. <p>
	 *
	 * @param action The {@link LinkedAction} that occurred
	 * @param parameters Important variables related to the action
	 * @param excludedTEs CustomizableSCTEs that aren't going to have onLinkedBlockAction() called on them,
	 *        always add your TileEntity to the list if you're going to call createLinkedBlockAction() in this method to chain-link multiple blocks (i.e: like Laser Blocks)
	 */
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<CustomizableBlockEntity> excludedTEs) {}
}
