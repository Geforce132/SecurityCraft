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
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.inventory.ExtractOnlyItemStackHandler;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

public class InventoryScannerBlockEntity extends DisguisableBlockEntity implements Container, MenuProvider, ITickingBlockEntity, ILockable, ContainerListener {
	private BooleanOption horizontal = new BooleanOption("horizontal", false);
	private BooleanOption solidifyField = new BooleanOption("solidifyField", false);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private IntOption signalLength = new SignalLengthOption(60);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack>withSize(37, ItemStack.EMPTY);
	private boolean providePower;
	private int signalCooldown, togglePowerCooldown;
	private LensContainer lens = new LensContainer(1);

	public InventoryScannerBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.INVENTORY_SCANNER_BLOCK_ENTITY.get(), pos, state);
		lens.addListener(this);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (togglePowerCooldown > 0)
			togglePowerCooldown--;

		if (signalCooldown > 0)
			signalCooldown--;
		else if (providePower && signalLength.get() > 0)
			togglePowerOutput();
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, pos, getBlockState(), be -> be.setOwner(getOwner().getUUID(), getOwner().getName()));

		if (connectedScanner != null) {
			connectedScanner.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(connectedScanner.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		ContainerHelper.loadAllItems(tag, inventoryContents, lookupProvider);
		signalCooldown = tag.getInt("cooldown");
		providePower = tag.getBoolean("is_providing_power");
		lens.fromTag(tag.getList("lens", Tag.TAG_COMPOUND), lookupProvider);
		lens.setChanged();
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		ContainerHelper.saveAllItems(tag, inventoryContents, lookupProvider);
		tag.putInt("cooldown", signalCooldown);
		tag.putBoolean("is_providing_power", providePower);
		tag.put("lens", lens.createTag(lookupProvider));
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
		int limit = stackToInsert.getItem().getMaxStackSize(stackToInsert);

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

	public LensContainer getLensContainer() {
		return lens;
	}

	@Override
	public void containerChanged(Container container) {
		if (level == null)
			return;

		InventoryScannerBlockEntity otherScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition, getBlockState(), be -> {
			if (be.getLevel().isClientSide)
				ClientHandler.updateBlockColorAroundPosition(be.getBlockPos());
		});

		if (otherScanner != null)
			otherScanner.getLensContainer().setItemExclusively(0, lens.getItem(0));
	}

	public static IItemHandler getCapability(InventoryScannerBlockEntity be, Direction side) {
		if (BlockUtils.isAllowedToExtractFromProtectedObject(side, be)) {
			return new ExtractOnlyItemStackHandler(be.inventoryContents) {
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					return slot < 10 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate); //don't allow extracting from the prohibited item slots
				}
			};
		}
		else
			return EmptyItemHandler.INSTANCE; //disallow inserting
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
		setChanged();
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
				Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), getContents().get(i));
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

	private void onInsertDisguiseModule(BlockEntity be, ItemStack stack) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.putDisguisedBeRenderer(be, stack);
	}

	private void onRemoveDisguiseModule(BlockEntity be) {
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
		switch (option) {
			case BooleanOption bo when option == horizontal -> {
				modifyFields((offsetPos, state) -> level.setBlockAndUpdate(offsetPos, state.setValue(InventoryScannerFieldBlock.HORIZONTAL, bo.get())), connectedScanner -> connectedScanner.setHorizontal(bo.get()));
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(InventoryScannerBlock.HORIZONTAL, bo.get()));
			}
			case BooleanOption bo when option == solidifyField -> {
				InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

				if (connectedScanner != null)
					connectedScanner.setSolidifyField(bo.get());
			}
			case BooleanOption bo when option == disabled -> {
				if (!bo.get())
					InventoryScannerBlock.checkAndPlaceAppropriately(level, worldPosition, true);
				else
					modifyFields((offsetPos, state) -> level.destroyBlock(offsetPos, false), connectedScanner -> connectedScanner.setDisabled(true));
			}
			case BooleanOption bo when option == ignoreOwner -> {
				InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

				if (connectedScanner != null)
					connectedScanner.setIgnoresOwner(bo.get());
			}
			case IntOption io when option == signalLength -> {
				InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, worldPosition);

				if (connectedScanner != null)
					connectedScanner.setSignalLength(io.get());
			}
			default -> throw new UnsupportedOperationException("Unhandled option synchronization in inventory scanner! " + option.getName());
		}

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
				else if (!state.isAir() && block != SCContent.INVENTORY_SCANNER_FIELD.get() && block != SCContent.INVENTORY_SCANNER.get())
					break;
				else if (block == SCContent.INVENTORY_SCANNER.get() && state.getValue(InventoryScannerBlock.FACING) == facing.getOpposite())
					break;
			}
		}
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
		if (doesFieldSolidify() != shouldSolidify) {
			solidifyField.setValue(shouldSolidify);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
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

	public boolean isConsideredInvisible(LivingEntity entity) {
		return respectInvisibility.isConsideredInvisible(entity);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				horizontal, solidifyField, disabled, ignoreOwner, signalLength, respectInvisibility
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
