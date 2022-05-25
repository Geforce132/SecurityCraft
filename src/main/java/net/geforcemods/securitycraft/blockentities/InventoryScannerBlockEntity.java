package net.geforcemods.securitycraft.blockentities;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.inventory.ExtractOnlyItemStackHandler;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class InventoryScannerBlockEntity extends DisguisableBlockEntity implements Container, MenuProvider, ITickingBlockEntity, ILockable {
	private BooleanOption horizontal = new BooleanOption("horizontal", false);
	private BooleanOption solidifyField = new BooleanOption("solidifyField", false);
	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> EmptyHandler.INSTANCE);
	private LazyOptional<IItemHandler> storageHandler;
	private NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack> withSize(37, ItemStack.EMPTY);
	private boolean isProvidingPower;
	private int cooldown;

	public InventoryScannerBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.INVENTORY_SCANNER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (cooldown > 0)
			cooldown--;
		else if (isProvidingPower) {
			isProvidingPower = false;
			BlockUtils.updateAndNotify(level, pos, state.getBlock(), 1, true);
			BlockUtils.updateIndirectNeighbors(level, pos, SCContent.INVENTORY_SCANNER.get());
			setChanged();
		}
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, pos, be -> be.setOwner(getOwner().getUUID(), getOwner().getName()));

		if (connectedScanner != null) {
			connectedScanner.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(connectedScanner.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player);
	}

	@Override
	public void load(CompoundTag tag) {
		ListTag list = tag.getList("Items", 10);

		super.load(tag);
		inventoryContents = NonNullList.<ItemStack> withSize(getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i) {
			CompoundTag stackTag = list.getCompound(i);
			int slot = stackTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < inventoryContents.size())
				inventoryContents.set(slot, ItemStack.of(stackTag));
		}

		cooldown = tag.getInt("cooldown");
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		ListTag list = new ListTag();

		for (int i = 0; i < inventoryContents.size(); ++i)
			if (!inventoryContents.get(i).isEmpty()) {
				CompoundTag stackTag = new CompoundTag();

				stackTag.putByte("Slot", (byte) i);
				inventoryContents.get(i).save(stackTag);
				list.add(stackTag);
			}

		tag.put("Items", list);
		tag.putInt("cooldown", cooldown);
	}

	@Override
	public int getContainerSize() {
		return 37;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (!inventoryContents.get(index).isEmpty()) {
			ItemStack stack;

			if (inventoryContents.get(index).getCount() <= count) {
				stack = inventoryContents.get(index);
				inventoryContents.set(index, ItemStack.EMPTY);
				setChanged();
				return stack;
			}
			else {
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
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : inventoryContents.get(slot);
	}

	@Override
	public ItemStack getItem(int slot) {
		return getStackInSlot(slot);
	}

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
			setItem(slot, stackToInsert);
			setChanged();
			return ItemStack.EMPTY;
		}
		else if (InventoryScannerFieldBlock.areItemStacksEqual(slotStack, stackToInsert) && slotStack.getCount() < limit) {
			if (limit - slotStack.getCount() >= stackToInsert.getCount()) {
				slotStack.setCount(slotStack.getCount() + stackToInsert.getCount());
				setChanged();
				return ItemStack.EMPTY;
			}
			else {
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.split((slotStack.getCount() + stackToInsert.getCount()) - limit); //this is the remaining stack that could not be inserted

				slotStack.setCount(slotStack.getCount() + toInsert.getCount());
				setChanged();
				return toReturn;
			}
		}

		return stackToInsert;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.getProtectedCapability(side, this, () -> getExtractionHandler(), () -> EMPTY_INVENTORY).cast(); //disallow inserting
		else
			return super.getCapability(cap, side);
	}

	public LazyOptional<IItemHandler> getExtractionHandler() {
		if (storageHandler == null) {
			storageHandler = LazyOptional.of(() -> new ExtractOnlyItemStackHandler(inventoryContents) {
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					return slot < 10 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate); //don't allow extracting from the prohibited item slots
				}
			});
		}

		return storageHandler;
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void startOpen(Player player) {}

	@Override
	public void stopOpen(Player player) {}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	public boolean shouldProvidePower() {
		return isModuleEnabled(ModuleType.REDSTONE) && isProvidingPower;
	}

	public void setShouldProvidePower(boolean isProvidingPower) {
		this.isProvidingPower = isProvidingPower;
		setChanged();
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
		setChanged();
	}

	public NonNullList<ItemStack> getContents() {
		return inventoryContents;
	}

	public void setContents(NonNullList<ItemStack> contents) {
		inventoryContents = contents;
		setChanged();
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);
		Predicate<ModuleType> test = toggled ? connectedScanner::isModuleEnabled : connectedScanner::hasModule;

		if (connectedScanner != null && !test.test(module))
			connectedScanner.insertModule(stack, toggled);

		if (module == ModuleType.DISGUISE) {
			onInsertDisguiseModule(this, stack);

			if (connectedScanner != null)
				onInsertDisguiseModule(connectedScanner, stack);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);
		Predicate<ModuleType> test = toggled ? connectedScanner::isModuleEnabled : connectedScanner::hasModule;

		if (connectedScanner != null && test.test(module))
			connectedScanner.removeModule(module, toggled);

		if (module == ModuleType.STORAGE) {
			//first 10 slots (0-9) are the prohibited slots
			for (int i = 10; i < getContainerSize(); i++) {
				Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), getContents().get(i));
			}

			if (connectedScanner != null) {
				for (int i = 10; i < connectedScanner.getContainerSize(); i++) {
					connectedScanner.getContents().set(i, ItemStack.EMPTY);
				}
			}
		}
		else if (module == ModuleType.DISGUISE) {
			onRemoveDisguiseModule(this, stack);

			if (connectedScanner != null)
				onRemoveDisguiseModule(connectedScanner, stack);
		}
	}

	private void onInsertDisguiseModule(BlockEntity be, ItemStack stack) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.putDisguisedBeRenderer(be, stack);
	}

	private void onRemoveDisguiseModule(BlockEntity be, ItemStack stack) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);
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
			InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

			if (connectedScanner != null) {
				Direction facing = getBlockState().getValue(InventoryScannerBlock.FACING);

				for (int i = 0; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++) {
					BlockPos offsetPos = worldPosition.relative(facing, i);
					BlockState state = level.getBlockState(offsetPos);
					Block block = state.getBlock();

					if (block == SCContent.INVENTORY_SCANNER_FIELD.get())
						level.setBlockAndUpdate(offsetPos, state.setValue(InventoryScannerFieldBlock.HORIZONTAL, bo.get()));
					else if (!state.isAir() && block != SCContent.INVENTORY_SCANNER_FIELD.get() && block != SCContent.INVENTORY_SCANNER.get())
						break;
					else if (block == SCContent.INVENTORY_SCANNER.get() && state.getValue(InventoryScannerBlock.FACING) == facing.getOpposite())
						break;
				}

				connectedScanner.setHorizontal(bo.get());
			}

			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(InventoryScannerBlock.HORIZONTAL, bo.get()));
		}
		else if (option.getName().equals("solidifyField")) {
			BooleanOption bo = (BooleanOption) option;
			InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

			if (connectedScanner != null)
				connectedScanner.setSolidifyField(bo.get());
		}

		super.onOptionChanged(option);
	}

	public void setHorizontal(boolean isHorizontal) {
		if (horizontal.get() != isHorizontal) {
			horizontal.setValue(isHorizontal);
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(InventoryScannerBlock.HORIZONTAL, isHorizontal));
			setChanged();
		}
	}

	public boolean isHorizontal() {
		return horizontal.get();
	}

	public boolean doesFieldSolidify() {
		return solidifyField.get();
	}

	public void setSolidifyField(boolean shouldSolidify) {
		solidifyField.setValue(shouldSolidify);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
		setChanged();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				horizontal, solidifyField
		};
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new InventoryScannerMenu(windowId, level, worldPosition, inv);
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}

	@Override
	public void clearContent() {
		inventoryContents.clear();
	}

	@Override
	public boolean isEmpty() {
		return inventoryContents.isEmpty();
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return inventoryContents.remove(index);
	}
}
