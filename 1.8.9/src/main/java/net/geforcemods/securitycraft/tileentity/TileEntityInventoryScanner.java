package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.blocks.BlockInventoryScannerField;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityInventoryScanner extends CustomizableSCTE implements IInventory{

	private ItemStack[] inventoryContents = new ItemStack[37];
	private String type = "check";
	private boolean isProvidingPower;
	private int cooldown;

	@Override
	public void update(){
		if(cooldown > 0)
			cooldown--;
		else if(isProvidingPower){
			isProvidingPower = false;
			BlockUtils.updateAndNotify(getWorld(), pos, getWorld().getBlockState(pos).getBlock(), 1, true);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		NBTTagList list = tag.getTagList("Items", 10);
		inventoryContents = new ItemStack[getSizeInventory()];

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < inventoryContents.length)
				inventoryContents[slot] = ItemStack.loadItemStackFromNBT(stackTag);
		}


		if(tag.hasKey("cooldown"))
			cooldown = tag.getInteger("cooldown");

		if(tag.hasKey("type"))
			type = tag.getString("type");

	}

	@Override
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < inventoryContents.length; ++i)
			if (inventoryContents[i] != null)
			{
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte)i);
				inventoryContents[i].writeToNBT(stackTag);
				list.appendTag(stackTag);
			}

		tag.setTag("Items", list);

		tag.setInteger("cooldown", cooldown);

		tag.setString("type", type);

	}

	@Override
	public int getSizeInventory() {
		return 37;
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (inventoryContents[index] != null)
		{
			ItemStack stack;

			if (inventoryContents[index].stackSize <= count)
			{
				stack = inventoryContents[index];
				inventoryContents[index] = null;
				markDirty();
				return stack;
			}
			else
			{
				stack = inventoryContents[index].splitStack(count);

				if (inventoryContents[index].stackSize == 0)
					inventoryContents[index] = null;

				markDirty();
				return stack;
			}
		}
		else
			return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		if (inventoryContents[index] != null)
		{
			ItemStack stack = inventoryContents[index];
			inventoryContents[index] = null;
			return stack;
		}
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventoryContents[index];
	}

	/**
	 * Copy of getStackInSlot which doesn't get overrided by CustomizableSCTE.
	 */

	public ItemStack getStackInSlotCopy(int index) {
		return inventoryContents[index];
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventoryContents[index] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		markDirty();
	}

	/**
	 * Adds the given stack to the inventory. Will void any excess.
	 * @param stack The stack to add
	 */
	public void addItemToStorage(ItemStack stack)
	{
		ItemStack remainder = stack;

		for(int i = 10; i < inventoryContents.length; i++)
		{
			remainder = insertItem(i, remainder);

			if(remainder == null)
				break;
		}
	}

	private ItemStack insertItem(int slot, ItemStack stackToInsert)
	{
		if(stackToInsert == null || slot < 10 || slot >= inventoryContents.length)
			return null;

		ItemStack slotStack = getStackInSlot(slot);
		int limit = stackToInsert.getItem().getItemStackLimit(stackToInsert);

		if(slotStack == null)
		{
			setInventorySlotContents(slot, stackToInsert);
			return null;
		}
		else if(BlockInventoryScannerField.areItemStacksEqual(slotStack, stackToInsert) && slotStack.stackSize < limit)
		{
			if(limit - slotStack.stackSize >= stackToInsert.stackSize)
			{
				slotStack.stackSize += stackToInsert.stackSize;
				return null;
			}
			else
			{
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.splitStack((slotStack.stackSize + stackToInsert.stackSize) - limit); //this is the remaining stack that could not be inserted

				slotStack.stackSize += toInsert.stackSize;
				return toReturn;
			}
		}

		return stackToInsert;
	}

	public void clearStorage() {
		for(int i = 10; i < inventoryContents.length; i++)
			if(inventoryContents[i] != null){
				inventoryContents[i] = null;
				break;
			}

		markDirty();
	}

	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	public String getType(){
		return type;
	}

	public void setType(String type){
		this.type = type;
	}

	public boolean shouldProvidePower() {
		return (type.equals("redstone") && isProvidingPower) ? true : false;
	}

	public void setShouldProvidePower(boolean isProvidingPower) {
		this.isProvidingPower = isProvidingPower;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public ItemStack[] getContents(){
		return inventoryContents;
	}

	public void setContents(ItemStack[] contents){
		inventoryContents = contents;
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(worldObj, pos);

		if(connectedScanner != null && !connectedScanner.hasModule(module))
			connectedScanner.insertModule(stack);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(worldObj, pos);

		if(connectedScanner != null && connectedScanner.hasModule(module))
			connectedScanner.removeModule(module);
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.SMART, EnumCustomModules.STORAGE};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

}
