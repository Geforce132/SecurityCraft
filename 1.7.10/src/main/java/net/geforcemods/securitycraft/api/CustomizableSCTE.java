package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Iterator;

import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.Constants;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableSCTE extends TileEntityOwnable implements IInventory {

	private boolean linkable = false;
	public ArrayList<LinkedBlock> linkedBlocks = new ArrayList<LinkedBlock>();
	private NBTTagList nbtTagStorage = null;

	public ItemStack[] itemStacks = new ItemStack[getNumberOfCustomizableOptions()];

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(hasWorldObj() && nbtTagStorage != null) {
			readLinkedBlocks(nbtTagStorage);
			sync();
			nbtTagStorage = null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		NBTTagList nbttaglist = tag.getTagList("Modules", 10);
		itemStacks = new ItemStack[getNumberOfCustomizableOptions()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound moduleTag = nbttaglist.getCompoundTagAt(i);
			byte b0 = moduleTag.getByte("ModuleSlot");

			if (b0 >= 0 && b0 < itemStacks.length)
				itemStacks[b0] = ItemStack.loadItemStackFromNBT(moduleTag);
		}

		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.readFromNBT(tag);

		if (tag.hasKey("linkable"))
			linkable = tag.getBoolean("linkable");

		if (linkable && tag.hasKey("linkedBlocks"))
		{
			if(!hasWorldObj()) {
				nbtTagStorage = tag.getTagList("linkedBlocks", Constants.NBT.TAG_COMPOUND);
				return;
			}

			readLinkedBlocks(tag.getTagList("linkedBlocks", Constants.NBT.TAG_COMPOUND));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		NBTTagList nbttaglist = new NBTTagList();

		for(int i = 0; i < itemStacks.length; i++)
			if (itemStacks[i] != null)
			{
				NBTTagCompound moduleTag = new NBTTagCompound();
				moduleTag.setByte("ModuleSlot", (byte)i);
				itemStacks[i].writeToNBT(moduleTag);
				nbttaglist.appendTag(moduleTag);
			}

		tag.setTag("Modules", nbttaglist);

		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.writeToNBT(tag);

		tag.setBoolean("linkable", linkable);

		if(linkable && hasWorldObj() && linkedBlocks.size() > 0) {
			NBTTagList tagList = new NBTTagList();

			Iterator<LinkedBlock> iterator = linkedBlocks.iterator();

			while(iterator.hasNext()) {
				LinkedBlock block = iterator.next();
				NBTTagCompound toAppend = new NBTTagCompound();

				if(block != null) {
					if(!block.validate(worldObj)) {
						linkedBlocks.remove(block);
						continue;
					}

					toAppend.setString("blockName", block.blockName);
					toAppend.setInteger("blockX", block.blockX);
					toAppend.setInteger("blockY", block.blockY);
					toAppend.setInteger("blockZ", block.blockZ);
				}

				tagList.appendTag(toAppend);
			}

			tag.setTag("linkedBlocks", tagList);
		}
	}

	private void readLinkedBlocks(NBTTagList list) {
		if(!linkable) return;

		for(int i = 0; i < list.tagCount(); i++) {
			String name = list.getCompoundTagAt(i).getString("blockName");
			int x = list.getCompoundTagAt(i).getInteger("blockX");
			int y = list.getCompoundTagAt(i).getInteger("blockY");
			int z = list.getCompoundTagAt(i).getInteger("blockZ");

			LinkedBlock block = new LinkedBlock(name, x, y, z);
			if(hasWorldObj() && !block.validate(worldObj)) {
				list.removeTag(i);
				continue;
			}

			if(!linkedBlocks.contains(block))
				link(this, block.asTileEntity(worldObj));
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
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
	public ItemStack decrStackSize(int index, int amount)
	{
		if (itemStacks[index] != null)
		{
			ItemStack itemstack;

			if (itemStacks[index].stackSize <= amount)
			{
				itemstack = itemStacks[index];
				itemStacks[index] = null;
				onModuleRemoved(itemstack, ((ItemModule) itemstack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ itemstack, ((ItemModule) itemstack.getItem()).getModule() }, this);
				return itemstack;
			}
			else
			{
				itemstack = itemStacks[index].splitStack(amount);

				if (itemStacks[index].stackSize == 0)
					itemStacks[index] = null;

				onModuleRemoved(itemstack, ((ItemModule) itemstack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ itemstack, ((ItemModule) itemstack.getItem()).getModule() }, this);

				return itemstack;
			}
		}
		else
			return null;
	}

	/**
	 * Copy of decrStackSize which can't be overrided by subclasses.
	 */

	public ItemStack safeDecrStackSize(int index, int amount)
	{
		if (itemStacks[index] != null)
		{
			ItemStack itemstack;

			if (itemStacks[index].stackSize <= amount)
			{
				itemstack = itemStacks[index];
				itemStacks[index] = null;
				onModuleRemoved(itemstack, ((ItemModule) itemstack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ itemstack, ((ItemModule) itemstack.getItem()).getModule() }, this);
				return itemstack;
			}
			else
			{
				itemstack = itemStacks[index].splitStack(amount);

				if (itemStacks[index].stackSize == 0)
					itemStacks[index] = null;

				onModuleRemoved(itemstack, ((ItemModule) itemstack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ itemstack, ((ItemModule) itemstack.getItem()).getModule() }, this);

				return itemstack;
			}
		}
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index)
	{
		if (itemStacks[index] != null)
		{
			ItemStack itemstack = itemStacks[index];
			itemStacks[index] = null;
			return itemstack;
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
			onModuleInserted(stack, ((ItemModule) stack.getItem()).getModule());
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
		}
	}

	@Override
	public String getInventoryName() {
		return "Customize";
	}

	@Override
	public boolean isCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() instanceof ItemModule ? true : false;
	}

	@Override
	public void onTileEntityDestroyed() {
		if(linkable)
			for(LinkedBlock block : linkedBlocks)
				CustomizableSCTE.unlink(block.asTileEntity(worldObj), this);
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
	 * Inserts an exact copy of the given item into the Customization inventory. Be sure that
	 * the item is an instanceof {@link ItemModule}, or it will not be inserted.
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

	public ArrayList<Item> getItemAddonsFromModule(EnumCustomModules module) {
		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] != null && itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) itemStacks[i].getItem()).getModule() == module)
				return ((ItemModule) itemStacks[i].getItem()).getItemAddons(itemStacks[i].getTagCompound());

		return null;
	}

	public ArrayList<Block> getBlockAddonsFromModule(EnumCustomModules module) {
		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] != null && itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) itemStacks[i].getItem()).getModule() == module)
				return ((ItemModule) itemStacks[i].getItem()).getBlockAddons(itemStacks[i].getTagCompound());

		return null;
	}

	public ArrayList<ItemStack> getAddonsFromModule(EnumCustomModules module) {
		for(int i = 0; i < itemStacks.length; i++)
			if(itemStacks[i] != null && itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) itemStacks[i].getItem()).getModule() == module)
				return ((ItemModule) itemStacks[i].getItem()).getAddons(itemStacks[i].getTagCompound());

		return null;
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
			if(excludedTEs.contains(block.asTileEntity(worldObj)))
				continue;
			else {
				block.asTileEntity(worldObj).onLinkedBlockAction(action, parameters, excludedTEs);
				block.asTileEntity(worldObj).sync();
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
