package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.blocks.BlockInventoryScannerField;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class TileEntityInventoryScanner extends TileEntityDisguisable implements IInventory{

	private OptionBoolean horizontal = new OptionBoolean("horizontal", false);
	private OptionBoolean solidifyField = new OptionBoolean("solidifyField", false);
	private static final EmptyHandler EMPTY_INVENTORY = new EmptyHandler();
	private NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack>withSize(37, ItemStack.EMPTY);
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
		inventoryContents = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < inventoryContents.size())
				inventoryContents.set(slot, new ItemStack(stackTag));
		}

		cooldown = tag.getInteger("cooldown");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < inventoryContents.size(); ++i)
			if (!inventoryContents.get(i).isEmpty())
			{
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte)i);
				inventoryContents.get(i).writeToNBT(stackTag);
				list.appendTag(stackTag);
			}

		tag.setTag("Items", list);
		tag.setInteger("cooldown", cooldown);
		return tag;
	}

	@Override
	public int getSizeInventory() {
		return 37;
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (!inventoryContents.get(index).isEmpty())
		{
			ItemStack stack;

			if (inventoryContents.get(index).getCount() <= count)
			{
				stack = inventoryContents.get(index);
				inventoryContents.set(index, ItemStack.EMPTY);
				markDirty();
				return stack;
			}
			else
			{
				stack = inventoryContents.get(index).splitStack(count);

				if (inventoryContents.get(index).getCount() == 0)
					inventoryContents.set(index, ItemStack.EMPTY);

				markDirty();
				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : inventoryContents.get(slot);
	}

	/**
	 * Copy of getStackInSlot which doesn't get overrided by CustomizableSCTE.
	 */

	public ItemStack getStackInSlotCopy(int index) {
		return inventoryContents.get(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventoryContents.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());

		markDirty();
	}

	/**
	 * Adds the given stack to the inventory. Will void any excess.
	 * @param stack The stack to add
	 */
	public void addItemToStorage(ItemStack stack)
	{
		ItemStack remainder = stack;

		for(int i = 10; i < getContents().size(); i++)
		{
			remainder = insertItem(i, remainder);

			if(remainder.isEmpty())
				break;
		}
	}

	public ItemStack insertItem(int slot, ItemStack stackToInsert)
	{
		if(stackToInsert.isEmpty() || slot < 0 || slot >= getContents().size())
			return stackToInsert;

		ItemStack slotStack = getStackInSlot(slot);
		int limit = stackToInsert.getItem().getItemStackLimit(stackToInsert);

		if(slotStack.isEmpty())
		{
			setInventorySlotContents(slot, stackToInsert);
			return ItemStack.EMPTY;
		}
		else if(BlockInventoryScannerField.areItemStacksEqual(slotStack, stackToInsert) && slotStack.getCount() < limit)
		{
			if(limit - slotStack.getCount() >= stackToInsert.getCount())
			{
				slotStack.setCount(slotStack.getCount() + stackToInsert.getCount());
				return ItemStack.EMPTY;
			}
			else
			{
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.splitStack((slotStack.getCount() + stackToInsert.getCount()) - limit); //this is the remaining stack that could not be inserted

				slotStack.setCount(slotStack.getCount() + toInsert.getCount());
				return toReturn;
			}
		}

		return stackToInsert;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) EMPTY_INVENTORY;
		else return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return true;
	}

	public boolean shouldProvidePower() {
		return hasModule(EnumModuleType.REDSTONE) && isProvidingPower;
	}

	public void setShouldProvidePower(boolean isProvidingPower) {
		this.isProvidingPower = isProvidingPower;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public NonNullList<ItemStack> getContents(){
		return inventoryContents;
	}

	public void setContents(NonNullList<ItemStack> contents){
		inventoryContents = contents;
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module)
	{
		super.onModuleInserted(stack, module);

		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if(connectedScanner != null && !connectedScanner.hasModule(module))
			connectedScanner.insertModule(stack);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);

		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if(connectedScanner != null && connectedScanner.hasModule(module))
			connectedScanner.removeModule(module);

		if(module == EnumModuleType.STORAGE)
		{
			for(int i = 10; i < getSizeInventory(); i++) //first 10 slots (0-9) are the prohibited slots
			{
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), getContents().get(i));
			}

			if(connectedScanner != null)
			{
				for(int i = 0; i < connectedScanner.getContents().size(); i++)
				{
					connectedScanner.getContents().set(i, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.WHITELIST, EnumModuleType.SMART, EnumModuleType.STORAGE, EnumModuleType.DISGUISE, EnumModuleType.REDSTONE};
	}

	@Override
	public void onOptionChanged(Option<?> option)
	{
		if(option.getName().equals("horizontal")) {
			OptionBoolean bo = (OptionBoolean)option;

			TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);
			IBlockState thisState = world.getBlockState(pos);

			if(connectedScanner != null)
			{
				EnumFacing facing = thisState.getValue(BlockInventoryScanner.FACING);

				for(int i = 0; i <= ConfigHandler.inventoryScannerRange; i++)
				{
					BlockPos offsetPos = pos.offset(facing, i);
					Block block = BlockUtils.getBlock(world, offsetPos);
					IBlockState state = world.getBlockState(offsetPos);

					if(block == SCContent.inventoryScannerField)
						world.setBlockState(offsetPos, state.withProperty(BlockInventoryScannerField.HORIZONTAL, bo.get()));
					else if(block != Blocks.AIR && block != SCContent.inventoryScannerField && block != SCContent.inventoryScanner)
						break;
					else if(block == SCContent.inventoryScanner && state.getValue(BlockInventoryScanner.FACING) == facing.getOpposite())
						break;
				}

				connectedScanner.setHorizontal(bo.get());
			}

			world.setBlockState(pos, thisState.withProperty(BlockInventoryScanner.HORIZONTAL, bo.get()));
		}

		else if (option.getName().equals("solidifyField")) {
			OptionBoolean bo = (OptionBoolean)option;
			TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

			connectedScanner.setSolidifyField(bo.get());
		}
	}

	public void setHorizontal(boolean isHorizontal)
	{
		horizontal.setValue(isHorizontal);
		world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockInventoryScanner.HORIZONTAL, isHorizontal));
	}

	public boolean isHorizontal()
	{
		return horizontal.get();
	}

	public boolean doesFieldSolidify() {
		return solidifyField.get();
	}

	public void setSolidifyField(boolean shouldSolidify) {
		IBlockState state = world.getBlockState(pos);

		solidifyField.setValue(shouldSolidify);
		world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[] {horizontal, solidifyField};
	}

	@Override
	public void clear()
	{
		inventoryContents.clear();
	}

	@Override
	public boolean isEmpty()
	{
		return inventoryContents.isEmpty();
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return inventoryContents.remove(index);
	}

	@Override
	public String getName()
	{
		return SCContent.inventoryScanner.getLocalizedName();
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{}

	@Override
	public int getFieldCount()
	{
		return 0;
	}
}
