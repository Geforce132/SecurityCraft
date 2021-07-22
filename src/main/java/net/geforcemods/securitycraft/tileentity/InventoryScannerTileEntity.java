package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class InventoryScannerTileEntity extends DisguisableTileEntity implements IInventory, INamedContainerProvider{

	private BooleanOption horizontal = new BooleanOption("horizontal", false);
	private BooleanOption solidifyField = new BooleanOption("solidifyField", false);
	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> new EmptyHandler());
	private NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack>withSize(37, ItemStack.EMPTY);
	private boolean isProvidingPower;
	private int cooldown;

	public InventoryScannerTileEntity()
	{
		super(SCContent.teTypeInventoryScanner);
	}

	@Override
	public void tick(){
		if(cooldown > 0)
			cooldown--;
		else if(isProvidingPower){
			isProvidingPower = false;
			BlockUtils.updateAndNotify(getLevel(), worldPosition, getLevel().getBlockState(worldPosition).getBlock(), 1, true);
		}
	}

	@Override
	public void load(BlockState state, CompoundNBT tag){
		super.load(state, tag);

		ListNBT list = tag.getList("Items", 10);
		inventoryContents = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i)
		{
			CompoundNBT stackTag = list.getCompound(i);
			int slot = stackTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < inventoryContents.size())
				inventoryContents.set(slot, ItemStack.of(stackTag));
		}

		cooldown = tag.getInt("cooldown");
	}

	@Override
	public CompoundNBT save(CompoundNBT tag){
		super.save(tag);

		ListNBT list = new ListNBT();

		for (int i = 0; i < inventoryContents.size(); ++i)
			if (!inventoryContents.get(i).isEmpty())
			{
				CompoundNBT stackTag = new CompoundNBT();
				stackTag.putByte("Slot", (byte)i);
				inventoryContents.get(i).save(stackTag);
				list.add(stackTag);
			}

		tag.put("Items", list);
		tag.putInt("cooldown", cooldown);
		return tag;
	}

	@Override
	public int getContainerSize() {
		return 37;
	}

	@Override
	public ItemStack removeItem(int index, int count)
	{
		if (!inventoryContents.get(index).isEmpty())
		{
			ItemStack stack;

			if (inventoryContents.get(index).getCount() <= count)
			{
				stack = inventoryContents.get(index);
				inventoryContents.set(index, ItemStack.EMPTY);
				setChanged();
				return stack;
			}
			else
			{
				stack = inventoryContents.get(index).split(count);

				if (inventoryContents.get(index).getCount() == 0)
					inventoryContents.set(index, ItemStack.EMPTY);

				setChanged();
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

	@Override
	public ItemStack getItem(int slot)
	{
		return getStackInSlot(slot);
	}

	/**
	 * Copy of getStackInSlot which doesn't get overrided by CustomizableSCTE.
	 */

	public ItemStack getStackInSlotCopy(int index) {
		return inventoryContents.get(index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		inventoryContents.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());

		setChanged();
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
			setItem(slot, stackToInsert);
			return ItemStack.EMPTY;
		}
		else if(InventoryScannerFieldBlock.areItemStacksEqual(slotStack, stackToInsert) && slotStack.getCount() < limit)
		{
			if(limit - slotStack.getCount() >= stackToInsert.getCount())
			{
				slotStack.setCount(slotStack.getCount() + stackToInsert.getCount());
				return ItemStack.EMPTY;
			}
			else
			{
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.split((slotStack.getCount() + stackToInsert.getCount()) - limit); //this is the remaining stack that could not be inserted

				slotStack.setCount(slotStack.getCount() + toInsert.getCount());
				return toReturn;
			}
		}

		return stackToInsert;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return EMPTY_INVENTORY.cast();
		else return super.getCapability(cap, side);
	}

	@Override
	public boolean hasCustomSCName() {
		return true;
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}

	@Override
	public void startOpen(PlayerEntity player) {}

	@Override
	public void stopOpen(PlayerEntity player) {}

	@Override
	public boolean canPlaceItem(int var1, ItemStack var2) {
		return true;
	}

	public boolean shouldProvidePower() {
		return hasModule(ModuleType.REDSTONE) && isProvidingPower;
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
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);

		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

		if(connectedScanner != null && !connectedScanner.hasModule(module))
			connectedScanner.insertModule(stack);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

		if(connectedScanner != null && connectedScanner.hasModule(module))
			connectedScanner.removeModule(module);

		if(module == ModuleType.STORAGE)
		{
			for(int i = 10; i < getContainerSize(); i++) //first 10 slots (0-9) are the prohibited slots
			{
				InventoryHelper.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), getContents().get(i));
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.SMART, ModuleType.STORAGE, ModuleType.DISGUISE, ModuleType.REDSTONE};
	}

	@Override
	public void onOptionChanged(Option<?> option)
	{
		if(option.getName().equals("horizontal")) {
			BooleanOption bo = (BooleanOption)option;

			InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

			if (connectedScanner != null) {
				Direction facing = getBlockState().getValue(InventoryScannerBlock.FACING);

				for (int i = 0; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++) {
					BlockPos offsetPos = worldPosition.relative(facing, i);
					BlockState state = level.getBlockState(offsetPos);
					Block block = state.getBlock();

					if (block == SCContent.INVENTORY_SCANNER_FIELD.get())
						level.setBlockAndUpdate(offsetPos, state.setValue(InventoryScannerFieldBlock.HORIZONTAL, bo.get()));
					else if (!state.isAir(level, offsetPos) && block != SCContent.INVENTORY_SCANNER_FIELD.get() && block != SCContent.INVENTORY_SCANNER.get())
						break;
					else if (block == SCContent.INVENTORY_SCANNER.get() && state.getValue(InventoryScannerBlock.FACING) == facing.getOpposite())
						break;
				}

				connectedScanner.setHorizontal(bo.get());
			}

			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(InventoryScannerBlock.HORIZONTAL, bo.get()));
		}
		else if (option.getName().equals("solidifyField")) {
			BooleanOption bo = (BooleanOption)option;
			InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

			connectedScanner.setSolidifyField(bo.get());
		}
	}

	public void setHorizontal(boolean isHorizontal)
	{
		horizontal.setValue(isHorizontal);
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(InventoryScannerBlock.HORIZONTAL, isHorizontal));
	}

	public boolean isHorizontal()
	{
		return horizontal.get();
	}

	public boolean doesFieldSolidify() {
		return solidifyField.get();
	}

	public void setSolidifyField(boolean shouldSolidify) {
		solidifyField.setValue(shouldSolidify);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[] {horizontal, solidifyField};
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new InventoryScannerContainer(windowId, level, worldPosition, inv);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.INVENTORY_SCANNER.get().getDescriptionId());
	}

	@Override
	public void clearContent()
	{
		inventoryContents.clear();
	}

	@Override
	public boolean isEmpty()
	{
		return inventoryContents.isEmpty();
	}

	@Override
	public ItemStack removeItemNoUpdate(int index)
	{
		return inventoryContents.remove(index);
	}
}
