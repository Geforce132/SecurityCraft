package net.geforcemods.securitycraft.blockentities;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.inventory.ExtractOnlyItemStackHandler;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class InventoryScannerBlockEntity extends DisguisableBlockEntity implements IInventory, ITickable, ILockable {
	private BooleanOption horizontal = new BooleanOption("horizontal", false);
	private BooleanOption solidifyField = new BooleanOption("solidifyField", false);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private IItemHandler storageHandler;
	private NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack>withSize(37, ItemStack.EMPTY);
	private boolean isProvidingPower;
	private int cooldown;

	@Override
	public void update() {
		if (cooldown > 0)
			cooldown--;
		else if (isProvidingPower) {
			isProvidingPower = false;
			BlockUtils.updateAndNotify(getWorld(), pos, getWorld().getBlockState(pos).getBlock(), 1, true);
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.inventoryScanner);
		}
	}

	@Override
	public void onOwnerChanged(IBlockState state, World world, BlockPos pos, EntityPlayer player) {
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos, world.getBlockState(pos), be -> be.setOwner(getOwner().getUUID(), getOwner().getName()));

		if (connectedScanner != null) {
			connectedScanner.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isRemote)
				world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(connectedScanner.getUpdatePacket());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		NBTTagList list = tag.getTagList("Items", 10);
		inventoryContents = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < inventoryContents.size())
				inventoryContents.set(slot, new ItemStack(stackTag));
		}

		cooldown = tag.getInteger("cooldown");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < inventoryContents.size(); ++i) {
			if (!inventoryContents.get(i).isEmpty()) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				inventoryContents.get(i).writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
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
	public ItemStack decrStackSize(int index, int count) {
		if (!inventoryContents.get(index).isEmpty()) {
			ItemStack stack;

			if (inventoryContents.get(index).getCount() <= count) {
				stack = inventoryContents.get(index);
				inventoryContents.set(index, ItemStack.EMPTY);
				markDirty();
				return stack;
			}
			else {
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
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : inventoryContents.get(slot);
	}

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

	public void addItemToStorage(ItemStack stack) {
		ItemStack remainder = stack;

		for (int i = 10; i < getContents().size(); i++) {
			remainder = insertItem(i, remainder);

			if (remainder.isEmpty())
				break;
		}
	}

	public ItemStack insertItem(int slot, ItemStack stackToInsert) {
		if (stackToInsert.isEmpty() || slot < 0 || slot >= getContents().size())
			return stackToInsert;

		ItemStack slotStack = getStackInSlot(slot);
		int limit = stackToInsert.getItem().getItemStackLimit(stackToInsert);

		if (slotStack.isEmpty()) {
			setInventorySlotContents(slot, stackToInsert);
			return ItemStack.EMPTY;
		}
		else if (InventoryScannerFieldBlock.areItemStacksEqual(slotStack, stackToInsert) && slotStack.getCount() < limit) {
			if (limit - slotStack.getCount() >= stackToInsert.getCount()) {
				slotStack.setCount(slotStack.getCount() + stackToInsert.getCount());
				return ItemStack.EMPTY;
			}
			else {
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.splitStack((slotStack.getCount() + stackToInsert.getCount()) - limit); //this is the remaining stack that could not be inserted

				slotStack.setCount(slotStack.getCount() + toInsert.getCount());
				return toReturn;
			}
		}

		return stackToInsert;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) BlockUtils.getProtectedCapability(facing, this, () -> getExtractionHandler(), () -> EmptyHandler.INSTANCE); //disallow inserting
		else
			return super.getCapability(capability, facing);
	}

	public IItemHandler getExtractionHandler() {
		if (storageHandler == null) {
			storageHandler = new ExtractOnlyItemStackHandler(inventoryContents) {
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					return slot < 10 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate); //don't allow extracting from the prohibited item slots
				}
			};
		}

		return storageHandler;
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
		return isModuleEnabled(ModuleType.REDSTONE) && isProvidingPower;
	}

	public void setShouldProvidePower(boolean isProvidingPower) {
		this.isProvidingPower = isProvidingPower;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public NonNullList<ItemStack> getContents() {
		return inventoryContents;
	}

	public void setContents(NonNullList<ItemStack> contents) {
		inventoryContents = contents;
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if (connectedScanner != null) {
			if (toggled ? !connectedScanner.isModuleEnabled(module) : !connectedScanner.hasModule(module))
				connectedScanner.insertModule(stack, toggled);
		}

		if (world.isRemote && module == ModuleType.DISGUISE) {
			BlockEntityRenderDelegate.putDisguisedTeRenderer(this, stack);

			if (connectedScanner != null)
				BlockEntityRenderDelegate.putDisguisedTeRenderer(connectedScanner, stack);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if (connectedScanner != null) {
			if (toggled ? connectedScanner.isModuleEnabled(module) : connectedScanner.hasModule(module))
				connectedScanner.removeModule(module, toggled);
		}

		if (module == ModuleType.STORAGE) {
			//first 10 slots (0-9) are the prohibited slots
			for (int i = 10; i < getSizeInventory(); i++) {
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), getContents().get(i));
			}

			if (connectedScanner != null) {
				for (int i = 10; i < connectedScanner.getSizeInventory(); i++) {
					connectedScanner.getContents().set(i, ItemStack.EMPTY);
				}
			}
		}
		else if (module == ModuleType.DISGUISE && world.isRemote) {
			BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);

			if (connectedScanner != null)
				BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(connectedScanner);
		}
	}

	@Override
	public boolean shouldDropModules() {
		return InventoryScannerBlock.getConnectedInventoryScanner(world, getPos()) == null;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.SMART, ModuleType.STORAGE, ModuleType.DISGUISE, ModuleType.REDSTONE
		};
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		if (option.getName().equals("horizontal")) {
			BooleanOption bo = (BooleanOption) option;

			modifyFields((offsetPos, state) -> world.setBlockState(offsetPos, state.withProperty(InventoryScannerFieldBlock.HORIZONTAL, bo.get())), connectedScanner -> connectedScanner.setHorizontal(bo.get()));
			world.setBlockState(pos, world.getBlockState(pos).withProperty(InventoryScannerBlock.HORIZONTAL, bo.get()));
		}
		else if (option.getName().equals("solidifyField")) {
			InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

			if (connectedScanner != null)
				connectedScanner.setSolidifyField(((BooleanOption) option).get());
		}
		else if (option.getName().equals("disabled")) {
			if (!((BooleanOption) option).get())
				InventoryScannerBlock.checkAndPlaceAppropriately(world, pos, null, true);
			else
				modifyFields((offsetPos, state) -> world.destroyBlock(offsetPos, false), connectedScanner -> connectedScanner.setDisabled(true));
		}
		else if (option.getName().equals("ignoreOwner")) {
			InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

			if (connectedScanner != null)
				connectedScanner.setIgnoresOwner(((BooleanOption) option).get());
		}
	}

	private void modifyFields(BiConsumer<BlockPos, IBlockState> blockSetter, Consumer<InventoryScannerBlockEntity> connectedScannerModifier) {
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);
		IBlockState thisState = world.getBlockState(pos);

		if (connectedScanner != null) {
			EnumFacing facing = thisState.getValue(InventoryScannerBlock.FACING);

			connectedScannerModifier.accept(connectedScanner);

			for (int i = 0; i <= ConfigHandler.inventoryScannerRange; i++) {
				BlockPos offsetPos = pos.offset(facing, i);
				IBlockState state = world.getBlockState(offsetPos);
				Block block = state.getBlock();

				if (block == SCContent.inventoryScannerField)
					blockSetter.accept(offsetPos, state);
				else if (!block.isAir(thisState, world, offsetPos) && block != SCContent.inventoryScannerField && block != SCContent.inventoryScanner)
					break;
				else if (block == SCContent.inventoryScanner && state.getValue(InventoryScannerBlock.FACING) == facing.getOpposite())
					break;
			}
		}
	}

	public void setHorizontal(boolean isHorizontal) {
		if (isHorizontal() != isHorizontal) {
			horizontal.setValue(isHorizontal);
			world.setBlockState(pos, world.getBlockState(pos).withProperty(InventoryScannerBlock.HORIZONTAL, isHorizontal));
		}
	}

	public boolean isHorizontal() {
		return horizontal.get();
	}

	public boolean doesFieldSolidify() {
		return solidifyField.get();
	}

	public void setSolidifyField(boolean shouldSolidify) {
		if (doesFieldSolidify() != shouldSolidify) {
			IBlockState state = world.getBlockState(pos);

			solidifyField.setValue(shouldSolidify);
			world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
		}
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public void setDisabled(boolean disabled) {
		if (isDisabled() != disabled) {
			IBlockState state = world.getBlockState(pos);

			this.disabled.setValue(disabled);
			world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
		}
	}

	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public void setIgnoresOwner(boolean ignoresOwner) {
		if (ignoresOwner() != ignoresOwner) {
			IBlockState state = world.getBlockState(pos);

			ignoreOwner.setValue(ignoresOwner);
			world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
			markDirty();
		}
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				horizontal, solidifyField, disabled, ignoreOwner
		};
	}

	@Override
	public void clear() {
		inventoryContents.clear();
	}

	@Override
	public boolean isEmpty() {
		return inventoryContents.isEmpty();
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inventoryContents.remove(index);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}
}
