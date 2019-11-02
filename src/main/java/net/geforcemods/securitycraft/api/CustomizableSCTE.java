package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Iterator;

import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableSCTE extends TileEntityOwnable implements IInventory{

	private boolean linkable = false;
	public ArrayList<LinkedBlock> linkedBlocks = new ArrayList<LinkedBlock>();
	private NBTTagList nbtTagStorage = null;

	public ItemStack[] itemStacks = new ItemStack[getNumberOfCustomizableOptions()];

	@Override
	public void update() {
		super.update();

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

		NBTTagList list = tag.getTagList("Modules", 10);
		itemStacks = new ItemStack[getNumberOfCustomizableOptions()];

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			byte slot = stackTag.getByte("ModuleSlot");

			if (slot >= 0 && slot < itemStacks.length)
				itemStacks[slot] = ItemStack.loadItemStackFromNBT(stackTag);
		}

		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.readFromNBT(tag);

		if (tag.hasKey("linkable"))
			linkable = tag.getBoolean("linkable");

		if (linkable && tag.hasKey("linkedBlocks"))
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

		NBTTagList nbttaglist = new NBTTagList();

		for(int i = 0; i < itemStacks.length; i++)
			if (itemStacks[i] != null)
			{
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("ModuleSlot", (byte)i);
				itemStacks[i].writeToNBT(stackTag);
				nbttaglist.appendTag(stackTag);
			}

		tag.setTag("Modules", nbttaglist);

		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.writeToNBT(tag);

		tag.setBoolean("linkable", linkable);

		if(linkable && hasWorld() && linkedBlocks.size() > 0) {
			NBTTagList tagList = new NBTTagList();

			WorldUtils.addScheduledTask(world, () -> {
				Iterator<LinkedBlock> iterator = linkedBlocks.iterator();

				while(iterator.hasNext()) {
					LinkedBlock block = iterator.next();
					NBTTagCompound toAppend = new NBTTagCompound();

					if(block != null) {
						if(!block.validate(world)) {
							linkedBlocks.remove(block);
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
		if(!linkable) return;

		for(int i = 0; i < list.tagCount(); i++) {
			String name = list.getCompoundTagAt(i).getString("blockName");
			int x = list.getCompoundTagAt(i).getInteger("blockX");
			int y = list.getCompoundTagAt(i).getInteger("blockY");
			int z = list.getCompoundTagAt(i).getInteger("blockZ");

			LinkedBlock block = new LinkedBlock(name, x, y, z);
			if(hasWorld() && !block.validate(world)) {
				list.removeTag(i);
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
		return itemStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (itemStacks[index] != null)
		{
			ItemStack stack;

			if (itemStacks[index].stackSize <= count)
			{
				stack = itemStacks[index];
				itemStacks[index] = null;
				onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

				if(this instanceof TileEntitySecurityCamera)
					getWorld().notifyNeighborsOfStateChange(pos.offset(getWorld().getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), getWorld().getBlockState(pos).getBlock());

				return stack;
			}
			else
			{
				stack = itemStacks[index].splitStack(count);

				if (itemStacks[index].stackSize == 0)
					itemStacks[index] = null;

				onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

				if(this instanceof TileEntitySecurityCamera)
					getWorld().notifyNeighborsOfStateChange(pos.offset(getWorld().getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), getWorld().getBlockState(pos).getBlock());

				return stack;
			}
		}
		else
			return null;
	}

	/**
	 * Copy of decrStackSize which can't be overrided by subclasses.
	 */

	public ItemStack safeDecrStackSize(int index, int count)
	{
		if (itemStacks[index] != null)
		{
			ItemStack stack;

			if (itemStacks[index].stackSize <= count)
			{
				stack = itemStacks[index];
				itemStacks[index] = null;
				onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

				if(this instanceof TileEntitySecurityCamera)
					getWorld().notifyNeighborsOfStateChange(pos.offset(getWorld().getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), getWorld().getBlockState(pos).getBlock());

				return stack;
			}
			else
			{
				stack = itemStacks[index].splitStack(count);

				if (itemStacks[index].stackSize == 0)
					itemStacks[index] = null;

				onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

				if(this instanceof TileEntitySecurityCamera)
					getWorld().notifyNeighborsOfStateChange(pos.offset(getWorld().getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), getWorld().getBlockState(pos).getBlock());

				return stack;
			}
		}
		else
			return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		if (itemStacks[index] != null)
		{
			ItemStack stack = itemStacks[index];
			itemStacks[index] = null;
			return stack;
		}
		else
			return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		itemStacks[index] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		if(stack != null)
		{
			onModuleInserted(stack, ((ItemModule) stack.getItem()).getModule());

			if(this instanceof TileEntitySecurityCamera)
				getWorld().notifyNeighborsOfStateChange(pos.offset(getWorld().getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), getWorld().getBlockState(pos).getBlock());
		}
	}

	/**
	 * Copy of setInventorySlotContents which can't be overrided by subclasses.
	 */
	public void safeSetInventorySlotContents(int index, ItemStack stack) {
		itemStacks[index] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemModule){
			onModuleInserted(stack, ((ItemModule) stack.getItem()).getModule());
			createLinkedBlockAction(EnumLinkedAction.MODULE_INSERTED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

			if(this instanceof TileEntitySecurityCamera)
				getWorld().notifyNeighborsOfStateChange(pos.offset(getWorld().getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), getWorld().getBlockState(pos).getBlock());
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

	@Override
	public String getName(){
		return "Customize";
	}

	@Override
	public boolean hasCustomName() {
		return (getCustomName() != null && !getCustomName().equals("name"));
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() instanceof ItemModule ? true : false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for(int i = 0; i < itemStacks.length; i++)
			itemStacks[i] = null;
	}

	@Override
	public void onTileEntityDestroyed() {
		if(linkable)
			for(LinkedBlock block : linkedBlocks)
				CustomizableSCTE.unlink(block.asTileEntity(world), this);
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
	public void onModuleInserted(ItemStack stack, EnumCustomModules module) {}

	/**
	 * Called whenever a module is removed from a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being removed.
	 * @param module The EnumCustomModules variant of stack.
	 */
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module) {}

	/**
	 * @return An ArrayList of all EnumCustomModules currently inserted in the TileEntity.
	 */
	public ArrayList<EnumCustomModules> getModules(){
		ArrayList<EnumCustomModules> modules = new ArrayList<EnumCustomModules>();

		for(ItemStack stack : itemStacks)
			if(stack != null && stack.getItem() instanceof ItemModule)
				modules.add(((ItemModule) stack.getItem()).getModule());

		return modules;
	}

	/**
	 * @return The ItemStack for the given EnumCustomModules type.
	 * If there is no ItemStack for that type, returns null.
	 */
	public ItemStack getModule(EnumCustomModules module){
		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] != null && itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) itemStacks[i].getItem()).getModule() == module)
				return itemStacks[i];

		return null;
	}

	/**
	 * Inserts a generic copy of the given module type into the Customization inventory.
	 */
	public void insertModule(EnumCustomModules module){
		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] != null)
				if(itemStacks[i].getItem() == module.getItem())
					return;

		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] == null && module != null){
				itemStacks[i] = new ItemStack(module.getItem());
				break;
			}else if(itemStacks[i] != null && module == null)
				itemStacks[i] = null;
			else
				continue;
	}

	/**
	 * Inserts an exact copy of the given item into the Customization inventory.
	 */
	public void insertModule(ItemStack module){
		if(module == null || !(module.getItem() instanceof ItemModule))
			return;

		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] != null)
				if(itemStacks[i].getItem() == module.getItem())
					return;

		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] == null){
				itemStacks[i] = module.copy();
				break;
			}
			else
				continue;
	}

	/**
	 * Removes the first item with the given module type from the inventory.
	 */
	public void removeModule(EnumCustomModules module){
		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] != null && itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) itemStacks[i].getItem()).getModule() == module)
				itemStacks[i] = null;
	}

	/**
	 * Does this inventory contain a item with the given module type?
	 */
	public boolean hasModule(EnumCustomModules module){
		if(module == null){
			for(int i = 0; i < itemStacks.length; i++)
				if(itemStacks[i] == null)
					return true;
		}
		else
			for(int i = 0; i < itemStacks.length; i++)
				if(itemStacks[i] != null && itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) itemStacks[i].getItem()).getModule() == module)
					return true;

		return false;
	}

	public int getNumberOfCustomizableOptions(){
		return acceptedModules().length;
	}

	public ArrayList<EnumCustomModules> getAcceptedModules(){
		ArrayList<EnumCustomModules> list = new ArrayList<EnumCustomModules>();

		for(EnumCustomModules module : acceptedModules())
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
	public CustomizableSCTE linkable() {
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
	 * whenever certain events (found in {@link EnumLinkedAction}) occur.
	 */
	public static void link(CustomizableSCTE tileEntity1, CustomizableSCTE tileEntity2) {
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
	public static void unlink(CustomizableSCTE tileEntity1, CustomizableSCTE tileEntity2) {
		if(tileEntity1 == null || tileEntity2 == null) return;
		if(!tileEntity1.linkable || !tileEntity2.linkable) return;

		LinkedBlock block = new LinkedBlock(tileEntity2);

		if(tileEntity1.linkedBlocks.contains(block))
			tileEntity1.linkedBlocks.remove(block);
	}

	/**
	 * @return Are the two blocks linked together?
	 */
	public static boolean isLinkedWith(CustomizableSCTE tileEntity1, CustomizableSCTE tileEntity2) {
		if(!tileEntity1.linkable || !tileEntity2.linkable) return false;

		return tileEntity1.linkedBlocks.contains(new LinkedBlock(tileEntity2)) && tileEntity2.linkedBlocks.contains(new LinkedBlock(tileEntity1));
	}

	/**
	 * Called whenever an {@link Option} in this TileEntity changes values.
	 *
	 * @param option The changed Option
	 */
	public void onOptionChanged(Option<?> option) {
		createLinkedBlockAction(EnumLinkedAction.OPTION_CHANGED, new Option[]{ option }, this);
	}

	/**
	 * Calls onLinkedBlockAction() for every block this TileEntity
	 * is linked to. <p>
	 *
	 * <b>NOTE:</b> Never use this method in onLinkedBlockAction(),
	 * use createLinkedBlockAction(EnumLinkedAction, Object[], ArrayList[CustomizableSCTE] instead.
	 *
	 * @param action The action that occurred
	 * @param parameters Action-specific parameters, see comments in {@link EnumLinkedAction}
	 * @param excludedTE The CustomizableSCTE which called this method, prevents infinite loops.
	 */
	public void createLinkedBlockAction(EnumLinkedAction action, Object[] parameters, CustomizableSCTE excludedTE) {
		ArrayList<CustomizableSCTE> list = new ArrayList<CustomizableSCTE>();

		list.add(excludedTE);

		createLinkedBlockAction(action, parameters, list);
	}

	/**
	 * Calls onLinkedBlockAction() for every block this TileEntity
	 * is linked to.
	 *
	 * @param action The action that occurred
	 * @param parameters Action-specific parameters, see comments in {@link EnumLinkedAction}
	 * @param excludedTEs CustomizableSCTEs that shouldn't have onLinkedBlockAction() called on them,
	 *        prevents infinite loops. Always add your TileEntity to the list whenever using this method
	 */
	public void createLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<CustomizableSCTE> excludedTEs) {
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
	 * this TileEntity is linked to. See {@link EnumLinkedAction}
	 * for parameter descriptions. <p>
	 *
	 * @param action The {@link EnumLinkedAction} that occurred
	 * @param parameters Important variables related to the action
	 * @param excludedTEs CustomizableSCTEs that aren't going to have onLinkedBlockAction() called on them,
	 *        always add your TileEntity to the list if you're going to call createLinkedBlockAction() in this method to chain-link multiple blocks (i.e: like Laser Blocks)
	 */
	protected void onLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<CustomizableSCTE> excludedTEs) {}

	/**
	 * @return An array of what {@link EnumCustomModules} can be inserted
	 *         into this TileEntity.
	 */
	public abstract EnumCustomModules[] acceptedModules();

	/**
	 * @return An array of what custom {@link Option}s this
	 *         TileEntity has.
	 */
	public abstract Option<?>[] customOptions();

}
