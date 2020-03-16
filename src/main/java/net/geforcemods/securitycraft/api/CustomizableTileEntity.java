package net.geforcemods.securitycraft.api;

import java.util.ArrayList;

import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableTileEntity extends SecurityCraftTileEntity implements IInventory{

	private boolean linkable = false;
	public ArrayList<LinkedBlock> linkedBlocks = new ArrayList<>();
	private ListNBT nbtTagStorage = null;

	public NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getNumberOfCustomizableOptions(), ItemStack.EMPTY);

	public CustomizableTileEntity(TileEntityType<?> type)
	{
		super(type);
	}

	@Override
	public void tick() {
		super.tick();

		if(hasWorld() && nbtTagStorage != null) {
			readLinkedBlocks(nbtTagStorage);
			sync();
			nbtTagStorage = null;
		}
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);

		ListNBT list = tag.getList("Modules", 10);
		modules = NonNullList.withSize(getNumberOfCustomizableOptions(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i)
		{
			CompoundNBT stackTag = list.getCompound(i);
			byte slot = stackTag.getByte("ModuleSlot");

			if (slot >= 0 && slot < modules.size())
				modules.set(slot, ItemStack.read(stackTag));
		}

		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.readFromNBT(tag);

		if (tag.contains("linkable"))
			linkable = tag.getBoolean("linkable");

		if (linkable && tag.contains("linkedBlocks"))
		{
			if(!hasWorld()) {
				nbtTagStorage = tag.getList("linkedBlocks", Constants.NBT.TAG_COMPOUND);
				return;
			}

			readLinkedBlocks(tag.getList("linkedBlocks", Constants.NBT.TAG_COMPOUND));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

		ListNBT list = new ListNBT();

		for(int i = 0; i < modules.size(); i++)
			if (!modules.get(i).isEmpty())
			{
				CompoundNBT stackTag = new CompoundNBT();
				stackTag.putByte("ModuleSlot", (byte)i);
				modules.get(i).write(stackTag);
				list.add(stackTag);
			}

		tag.put("Modules", list);

		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.writeToNBT(tag);

		tag.putBoolean("linkable", linkable);

		if(linkable && hasWorld() && linkedBlocks.size() > 0) {
			ListNBT tagList = new ListNBT();

			WorldUtils.addScheduledTask(world, () -> {
				for(int i = linkedBlocks.size() - 1; i >= 0; i--)
				{
					LinkedBlock block = linkedBlocks.get(i);
					CompoundNBT toAppend = new CompoundNBT();

					if(block != null) {
						if(!block.validate(world)) {
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

	private void readLinkedBlocks(ListNBT list) {
		if(!linkable) return;

		for(int i = 0; i < list.size(); i++) {
			String name = list.getCompound(i).getString("blockName");
			int x = list.getCompound(i).getInt("blockX");
			int y = list.getCompound(i).getInt("blockY");
			int z = list.getCompound(i).getInt("blockZ");

			LinkedBlock block = new LinkedBlock(name, x, y, z);
			if(hasWorld() && !block.validate(world)) {
				list.remove(i);
				continue;
			}

			if(!linkedBlocks.contains(block))
				link(this, block.asTileEntity(world));
		}
	}

	@Override
	public int getSizeInventory() {
		return getNumberOfCustomizableOptions();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return modules.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (!modules.get(index).isEmpty())
		{
			ItemStack stack;

			if (modules.get(index).getCount() <= count)
			{
				stack = modules.get(index);
				modules.set(index, ItemStack.EMPTY);
				onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModule());
				createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, this);

				if(this instanceof SecurityCameraTileEntity)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).get(SecurityCameraBlock.FACING), -1), world.getBlockState(pos).getBlock());

				return stack;
			}
			else
			{
				stack = modules.get(index).split(count);

				if (modules.get(index).getCount() == 0)
					modules.set(index, ItemStack.EMPTY);

				onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModule());
				createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, this);

				if(this instanceof SecurityCameraTileEntity)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).get(SecurityCameraBlock.FACING), -1), world.getBlockState(pos).getBlock());

				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	/**
	 * Copy of decrStackSize which can't be overrided by subclasses.
	 */
	public ItemStack safeDecrStackSize(int index, int count)
	{
		if (!modules.get(index).isEmpty())
		{
			ItemStack stack;

			if (modules.get(index).getCount() <= count)
			{
				stack = modules.get(index);
				modules.set(index, ItemStack.EMPTY);
				onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModule());
				createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, this);

				if(this instanceof SecurityCameraTileEntity)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).get(SecurityCameraBlock.FACING), -1), world.getBlockState(pos).getBlock());

				return stack;
			}
			else
			{
				stack = modules.get(index).split(count);

				if (modules.get(index).getCount() == 0)
					modules.set(index, ItemStack.EMPTY);

				onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModule());
				createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, this);

				if(this instanceof SecurityCameraTileEntity)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).get(SecurityCameraBlock.FACING), -1), world.getBlockState(pos).getBlock());

				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		if (!modules.get(index).isEmpty())
		{
			ItemStack stack = modules.get(index);
			modules.set(index, ItemStack.EMPTY);
			return stack;
		}
		else
			return ItemStack.EMPTY;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		modules.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack = new ItemStack(stack.getItem(), getInventoryStackLimit());

		if(!stack.isEmpty())
		{
			onModuleInserted(stack, ((ModuleItem) stack.getItem()).getModule());

			if(this instanceof SecurityCameraTileEntity)
				world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).get(SecurityCameraBlock.FACING), -1), world.getBlockState(pos).getBlock());
		}
	}

	/**
	 * Copy of setInventorySlotContents which can't be overrided by subclasses.
	 */
	public void safeSetInventorySlotContents(int index, ItemStack stack) {
		modules.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack = new ItemStack(stack.getItem(), getInventoryStackLimit());

		if(!stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ModuleItem){
			onModuleInserted(stack, ((ModuleItem) stack.getItem()).getModule());
			createLinkedBlockAction(LinkedAction.MODULE_INSERTED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, this);

			if(this instanceof SecurityCameraTileEntity)
				world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).get(SecurityCameraBlock.FACING), -1), world.getBlockState(pos).getBlock());
		}
	}

	@Override
	public boolean hasCustomSCName() {
		return (getCustomSCName() != null && !getCustomSCName().getFormattedText().equals("name"));
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player)
	{
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {}

	@Override
	public void closeInventory(PlayerEntity player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() instanceof ModuleItem ? true : false;
	}

	@Override
	public boolean isEmpty()
	{
		for(ItemStack stack : modules)
			if(!stack.isEmpty())
				return false;

		return true;
	}

	@Override
	public void clear() {
		for(int i = 0; i < modules.size(); i++)
			modules.set(i, ItemStack.EMPTY);
	}

	@Override
	public void onTileEntityDestroyed() {
		if(linkable)
			for(LinkedBlock block : linkedBlocks)
				CustomizableTileEntity.unlink(block.asTileEntity(world), this);

		for(ItemStack module : modules)
		{
			Block.spawnAsEntity(world, pos, module);
		}
	}

	////////////////////////
	// MODULE STUFF //
	////////////////////////

	/**
	 * Called whenever a module is inserted into a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being inserted.
	 * @param module The EnumCustomModules variant of stack.
	 */
	public void onModuleInserted(ItemStack stack, CustomModules module) {}

	/**
	 * Called whenever a module is removed from a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being removed.
	 * @param module The EnumCustomModules variant of stack.
	 */
	public void onModuleRemoved(ItemStack stack, CustomModules module) {}

	/**
	 * @return An ArrayList of all EnumCustomModules currently inserted in the TileEntity.
	 */
	public ArrayList<CustomModules> getModules(){
		ArrayList<CustomModules> modules = new ArrayList<>();

		for(ItemStack stack : this.modules)
			if(!stack.isEmpty() && stack.getItem() instanceof ModuleItem)
				modules.add(((ModuleItem) stack.getItem()).getModule());

		return modules;
	}

	/**
	 * @return The ItemStack for the given EnumCustomModules type.
	 * If there is no ItemStack for that type, returns null.
	 */
	public ItemStack getModule(CustomModules module){
		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModule() == module)
				return modules.get(i);

		return ItemStack.EMPTY;
	}

	/**
	 * Inserts a generic copy of the given module type into the Customization inventory.
	 */
	public void insertModule(CustomModules module){
		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty())
				if(modules.get(i).getItem() == module.getItem())
					return;

		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty() && module != null){
				modules.set(i, new ItemStack(module.getItem()));
				break;
			}else if(!modules.get(i).isEmpty() && module == null)
				modules.set(i, ItemStack.EMPTY);
			else
				continue;
	}

	/**
	 * Inserts an exact copy of the given item into the Customization inventory.
	 */
	public void insertModule(ItemStack module){
		if(module.isEmpty() || !(module.getItem() instanceof ModuleItem))
			return;

		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty())
				if(modules.get(i).getItem() == module.getItem())
					return;

		for(int i = 0; i < modules.size(); i++)
			if(modules.get(i).isEmpty()){
				modules.set(i, module.copy());
				break;
			}
			else
				continue;
	}

	/**
	 * Removes the first item with the given module type from the inventory.
	 */
	public void removeModule(CustomModules module){
		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModule() == module)
				modules.set(i, ItemStack.EMPTY);
	}

	/**
	 * Does this inventory contain a item with the given module type?
	 */
	public boolean hasModule(CustomModules module){
		if(module == null){
			for(int i = 0; i < modules.size(); i++)
				if(modules.get(i).isEmpty())
					return true;
		}
		else
			for(int i = 0; i < modules.size(); i++)
				if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModule() == module)
					return true;
		return false;
	}

	public int getNumberOfCustomizableOptions(){
		return acceptedModules().length;
	}

	public ArrayList<CustomModules> getAcceptedModules(){
		ArrayList<CustomModules> list = new ArrayList<>();

		for(CustomModules module : acceptedModules())
			list.add(module);

		return list;
	}

	/**
	 * Checks to see if this TileEntity has an {@link Option}
	 * with the given name, and if so, returns it.
	 *
	 * @param name Option name
	 * @return The Option
	 */
	public Option<?> getOptionByName(String name) {
		for(Option<?> option : customOptions())
			if(option.getName().equals(name))
				return option;

		return null;
	}

	/**
	 * Sets this TileEntity able to be "linked" with other blocks,
	 * and being able to do things between them. Call CustomizableSCTE.link()
	 * to link two blocks together.
	 */
	public CustomizableTileEntity linkable() {
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
	public static void link(CustomizableTileEntity tileEntity1, CustomizableTileEntity tileEntity2) {
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
	public static void unlink(CustomizableTileEntity tileEntity1, CustomizableTileEntity tileEntity2) {
		if(tileEntity1 == null || tileEntity2 == null) return;
		if(!tileEntity1.linkable || !tileEntity2.linkable) return;

		LinkedBlock block = new LinkedBlock(tileEntity2);

		if(tileEntity1.linkedBlocks.contains(block))
			tileEntity1.linkedBlocks.remove(block);
	}

	/**
	 * @return Are the two blocks linked together?
	 */
	public static boolean isLinkedWith(CustomizableTileEntity tileEntity1, CustomizableTileEntity tileEntity2) {
		if(!tileEntity1.linkable || !tileEntity2.linkable) return false;

		return tileEntity1.linkedBlocks.contains(new LinkedBlock(tileEntity2)) && tileEntity2.linkedBlocks.contains(new LinkedBlock(tileEntity1));
	}

	/**
	 * Called whenever an {@link Option} in this TileEntity changes values.
	 *
	 * @param option The changed Option
	 */
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
	public void createLinkedBlockAction(LinkedAction action, Object[] parameters, CustomizableTileEntity excludedTE) {
		ArrayList<CustomizableTileEntity> list = new ArrayList<>();

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
	public void createLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<CustomizableTileEntity> excludedTEs) {
		if(!linkable) return;

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
	 * this TileEntity is linked to. See {@link LinkedAction}
	 * for parameter descriptions. <p>
	 *
	 * @param action The {@link LinkedAction} that occurred
	 * @param parameters Important variables related to the action
	 * @param excludedTEs CustomizableSCTEs that aren't going to have onLinkedBlockAction() called on them,
	 *        always add your TileEntity to the list if you're going to call createLinkedBlockAction() in this method to chain-link multiple blocks (i.e: like Laser Blocks)
	 */
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<CustomizableTileEntity> excludedTEs) {}

	/**
	 * @return An array of what {@link CustomModules} can be inserted
	 *         into this TileEntity.
	 */
	public abstract CustomModules[] acceptedModules();

	/**
	 * @return An array of what custom {@link Option}s this
	 *         TileEntity has.
	 */
	public abstract Option<?>[] customOptions();

}
