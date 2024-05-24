package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.inventory.ExtractOnlyItemStackHandler;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class InventoryScannerBlockEntity extends DisguisableBlockEntity implements IInventory, INamedContainerProvider, ITickableTileEntity, ILockable, IInventoryChangedListener {
	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> EmptyHandler.INSTANCE);
	private BooleanOption horizontal = new BooleanOption("horizontal", false);
	private BooleanOption solidifyField = new BooleanOption("solidifyField", false);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private IntOption signalLength = new SignalLengthOption(this::getBlockPos, 60);
	private LazyOptional<IItemHandler> storageHandler;
	private NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack>withSize(37, ItemStack.EMPTY);
	private boolean providePower;
	private int signalCooldown, togglePowerCooldown;
	private LensContainer lens = new LensContainer(1);

	public InventoryScannerBlockEntity() {
		super(SCContent.INVENTORY_SCANNER_BLOCK_ENTITY.get());
		lens.addListener(this);
	}

	@Override
	public void tick() {
		if (togglePowerCooldown > 0)
			togglePowerCooldown--;

		if (signalCooldown > 0)
			signalCooldown--;
		else if (providePower && signalLength.get() > 0)
			togglePowerOutput();
	}

	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player) {
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, pos, getBlockState(), be -> be.setOwner(getOwner().getUUID(), getOwner().getName()));

		if (connectedScanner != null) {
			connectedScanner.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(connectedScanner.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player);
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		ListNBT list = tag.getList("Items", 10);

		super.load(state, tag);
		inventoryContents = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i) {
			CompoundNBT stackTag = list.getCompound(i);
			int slot = stackTag.getByte("Slot") & 255;

			if (slot < inventoryContents.size())
				inventoryContents.set(slot, ItemStack.of(stackTag));
		}

		signalCooldown = tag.getInt("cooldown");
		providePower = tag.getBoolean("is_providing_power");
		lens.fromTag(tag.getList("lens", Constants.NBT.TAG_COMPOUND));
		lens.setChanged();
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		ListNBT list = new ListNBT();

		for (int i = 0; i < inventoryContents.size(); ++i) {
			if (!inventoryContents.get(i).isEmpty()) {
				CompoundNBT stackTag = new CompoundNBT();
				stackTag.putByte("Slot", (byte) i);
				inventoryContents.get(i).save(stackTag);
				list.add(stackTag);
			}
		}

		tag.put("Items", list);
		tag.putInt("cooldown", signalCooldown);
		tag.putBoolean("is_providing_power", providePower);
		tag.put("lens", lens.createTag());
		return tag;
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

	public List<ItemStack> getAllProhibitedItems() {
		List<ItemStack> stacks = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			if (!inventoryContents.get(i).isEmpty())
				stacks.add(inventoryContents.get(i));
		}

		return stacks;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		inventoryContents.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());

		setChanged();
	}

	public ItemStack addItemToStorage(ItemStack stack) {
		ItemStack remainder = stack;

		for (int i = 10; i < getContents().size(); i++) {
			remainder = insertItem(i, remainder);

			if (remainder.isEmpty())
				break;
		}

		return remainder;
	}

	public ItemStack insertItem(int slot, ItemStack stackToInsert) {
		if (stackToInsert.isEmpty() || slot < 0 || slot >= getContents().size())
			return stackToInsert;

		ItemStack slotStack = getStackInSlot(slot);
		int limit = stackToInsert.getItem().getItemStackLimit(stackToInsert);

		if (slotStack.isEmpty()) {
			setItem(slot, stackToInsert);
			return ItemStack.EMPTY;
		}
		else if (InventoryScannerFieldBlock.areItemStacksEqual(slotStack, stackToInsert) && slotStack.getCount() < limit) {
			if (limit - slotStack.getCount() >= stackToInsert.getCount()) {
				slotStack.setCount(slotStack.getCount() + stackToInsert.getCount());
				return ItemStack.EMPTY;
			}
			else {
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.split((slotStack.getCount() + stackToInsert.getCount()) - limit); //this is the remaining stack that could not be inserted

				slotStack.setCount(slotStack.getCount() + toInsert.getCount());
				return toReturn;
			}
		}

		return stackToInsert;
	}

	public LensContainer getLensContainer() {
		return lens;
	}

	@Override
	public void containerChanged(IInventory container) {
		if (level == null)
			return;

		InventoryScannerBlockEntity otherScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition, getBlockState(), be -> {
			if (be.getLevel().isClientSide)
				ClientHandler.updateBlockColorAroundPosition(be.getBlockPos());
		});

		if (otherScanner != null)
			otherScanner.getLensContainer().setItemExclusively(0, lens.getItem(0));
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(side, this) ? getExtractionHandler().cast() : EMPTY_INVENTORY.cast(); //disallow inserting
		else
			return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		if (storageHandler != null)
			storageHandler.invalidate();

		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		storageHandler = null; //recreated in getExtractionHandler
		super.reviveCaps();
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
	public boolean stillValid(PlayerEntity player) {
		return true;
	}

	@Override
	public void startOpen(PlayerEntity player) {}

	@Override
	public void stopOpen(PlayerEntity player) {}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return false;
	}

	public boolean isProvidingPower() {
		return isModuleEnabled(ModuleType.REDSTONE) && providePower;
	}

	public boolean wantsToProvidePower() {
		return providePower;
	}

	public void togglePowerOutput() {
		int signalLength = this.signalLength.get();
		boolean shouldTurnOffAgain = signalLength > 0;

		if (!shouldTurnOffAgain && togglePowerCooldown > 0)
			togglePowerCooldown = 5;
		else {
			if (!shouldTurnOffAgain || signalCooldown <= 0) {
				togglePowerCooldown = 5;
				providePower = !providePower;
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.INVENTORY_SCANNER.get());
				setChanged();
			}

			if (providePower && shouldTurnOffAgain)
				signalCooldown = signalLength;
		}
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

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

		if (connectedScanner != null && (toggled ? !connectedScanner.isModuleEnabled(module) : !connectedScanner.hasModule(module)))
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

		if (connectedScanner != null && (toggled ? connectedScanner.isModuleEnabled(module) : connectedScanner.hasModule(module)))
			connectedScanner.removeModule(module, toggled);

		if (module == ModuleType.STORAGE) {
			//first 10 slots (0-9) are the prohibited slots
			for (int i = 10; i < getContainerSize(); i++) {
				InventoryHelper.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), getContents().get(i));
			}

			if (connectedScanner != null) {
				for (int i = 10; i < connectedScanner.getContainerSize(); i++) {
					connectedScanner.getContents().set(i, ItemStack.EMPTY);
				}
			}
		}
		else if (module == ModuleType.DISGUISE) {
			onRemoveDisguiseModule(this);

			if (connectedScanner != null)
				onRemoveDisguiseModule(connectedScanner);
		}
	}

	private void onInsertDisguiseModule(TileEntity be, ItemStack stack) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.putDisguisedBeRenderer(be, stack);
	}

	private void onRemoveDisguiseModule(TileEntity be) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);
	}

	@Override
	public boolean shouldDropModules() {
		return InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition) == null;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.SMART, ModuleType.STORAGE, ModuleType.DISGUISE, ModuleType.REDSTONE
		};
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option instanceof BooleanOption) {
			BooleanOption bo = (BooleanOption) option;

			if (option == horizontal) {
				modifyFields((offsetPos, state) -> level.setBlockAndUpdate(offsetPos, state.setValue(InventoryScannerFieldBlock.HORIZONTAL, bo.get())), connectedScanner -> connectedScanner.setHorizontal(bo.get()));
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(InventoryScannerBlock.HORIZONTAL, bo.get()));
			}
			else if (option == solidifyField) {
				InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

				if (connectedScanner != null)
					connectedScanner.setSolidifyField(bo.get());
			}
			else if (option == disabled) {
				if (!bo.get())
					InventoryScannerBlock.checkAndPlaceAppropriately(level, worldPosition, true);
				else
					modifyFields((offsetPos, state) -> level.destroyBlock(offsetPos, false), connectedScanner -> connectedScanner.setDisabled(true));
			}
			else if (option == ignoreOwner) {
				InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

				if (connectedScanner != null)
					connectedScanner.setIgnoresOwner(bo.get());
			}
			else
				throw new UnsupportedOperationException("Unhandled option synchronization in inventory scanner! " + option.getName());
		}
		else if (option instanceof IntOption && option == signalLength) {
			InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

			if (connectedScanner != null)
				connectedScanner.setSignalLength(((IntOption) option).get());
		}
		else
			throw new UnsupportedOperationException("Unhandled option synchronization in inventory scanner! " + option.getName());

		super.onOptionChanged(option);
	}

	private void modifyFields(BiConsumer<BlockPos, BlockState> blockSetter, Consumer<InventoryScannerBlockEntity> connectedScannerModifier) {
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

		if (connectedScanner != null) {
			Direction facing = getBlockState().getValue(InventoryScannerBlock.FACING);

			connectedScannerModifier.accept(connectedScanner);

			for (int i = 0; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++) {
				BlockPos offsetPos = worldPosition.relative(facing, i);
				BlockState state = level.getBlockState(offsetPos);
				Block block = state.getBlock();

				if (block == SCContent.INVENTORY_SCANNER_FIELD.get())
					blockSetter.accept(offsetPos, state);
				else if (!state.isAir(level, offsetPos) && block != SCContent.INVENTORY_SCANNER_FIELD.get() && block != SCContent.INVENTORY_SCANNER.get())
					break;
				else if (block == SCContent.INVENTORY_SCANNER.get() && state.getValue(InventoryScannerBlock.FACING) == facing.getOpposite())
					break;
			}
		}
	}

	public void setHorizontal(boolean isHorizontal) {
		if (isHorizontal() != isHorizontal) {
			horizontal.setValue(isHorizontal);
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(InventoryScannerBlock.HORIZONTAL, isHorizontal));
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
			solidifyField.setValue(shouldSolidify);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
		}
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public void setDisabled(boolean disabled) {
		if (isDisabled() != disabled) {
			this.disabled.setValue(disabled);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public void setIgnoresOwner(boolean ignoresOwner) {
		if (ignoresOwner() != ignoresOwner) {
			ignoreOwner.setValue(ignoresOwner);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public void setSignalLength(int signalLength) {
		this.signalLength.setValue(signalLength);
		togglePowerCooldown = 5;
		providePower = false;
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
		setChanged();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				horizontal, solidifyField, disabled, ignoreOwner, signalLength
		};
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new InventoryScannerMenu(windowId, level, worldPosition, inv);
	}

	@Override
	public ITextComponent getDisplayName() {
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
