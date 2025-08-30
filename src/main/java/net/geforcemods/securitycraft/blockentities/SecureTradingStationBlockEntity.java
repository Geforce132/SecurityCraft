package net.geforcemods.securitycraft.blockentities;

import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.SecureTradingStation;
import net.geforcemods.securitycraft.inventory.InsertOnlySidedInvWrapper;
import net.geforcemods.securitycraft.inventory.SecureTradingStationMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.InventoryUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class SecureTradingStationBlockEntity extends DisguisableBlockEntity implements WorldlyContainer, MenuProvider {
	private static final int[] SLOTS_FOR_UP_AND_SIDES = new int[] {12, 13, 14, 15, 16, 17, 18, 19};
	private static final int[] SLOTS_FOR_DOWN = new int[] {4, 5, 6, 7, 8, 9, 10, 11};
	private IntOption signalLength = new SignalLengthOption(60);
	private final DisabledOption disabled = new DisabledOption(false);
	private final NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack>withSize(20, ItemStack.EMPTY); //2 for payment reference, 2 for reward reference, 8 for payment storage, 8 for reward storage
	public int rewardLimitedTransactions = 0;

	public SecureTradingStationBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.SECURE_TRADING_STATION_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void loadAdditional(CompoundTag tag, Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		ContainerHelper.loadAllItems(tag, inventoryContents, lookupProvider);
	}

	@Override
	public void saveAdditional(CompoundTag tag, Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		ContainerHelper.saveAllItems(tag, inventoryContents, lookupProvider);
	}

	@Override
	public void setChanged() {
		boolean hasSmartModule = isModuleEnabled(ModuleType.SMART);

		rewardLimitedTransactions = getReferenceLimitedTransactions(this, 4, getContainerSize() - 1, getRewardPerTransaction(hasSmartModule), hasSmartModule);
		super.setChanged();
	}

	public void doTransaction(Player player, int requestedTransactions) {
		if (player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof SecureTradingStationMenu menu) {
			int signalLengthOption = signalLength.get();
			boolean skipPaymentCheck = isOwnedBy(player) || isAllowed(player);
			boolean hasSmartModule = isModuleEnabled(ModuleType.SMART);
			int transactions = skipPaymentCheck ? requestedTransactions : Math.min(menu.paymentLimitedTransactions, requestedTransactions);

			if (transactions == 0) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:secure_trading_station.transaction_failed"), ChatFormatting.RED);
				return;
			}

			Map<ItemStack, Integer> unifiedPaymentRequestItems = getPaymentPerTransaction(hasSmartModule);

			for (Map.Entry<ItemStack, Integer> paymentRequest : unifiedPaymentRequestItems.entrySet()) {
				ItemStack paymentStackToMatch = paymentRequest.getKey();
				int quantityPerTransaction = paymentRequest.getValue();
				int totalQuantity = quantityPerTransaction * transactions;

				InventoryUtils.checkInventoryForItem(menu.paymentInput.items, paymentStackToMatch, totalQuantity, hasSmartModule, true, this::handleConsumedPaymentItem, menu.paymentInput::setItem);
			}

			if (hasRewardReferenceStacks()) {
				Map<ItemStack, Integer> rewardItems = getRewardPerTransaction(hasSmartModule);

				for (Map.Entry<ItemStack, Integer> rewardEntry : rewardItems.entrySet()) {
					ItemStack rewardStackToMatch = rewardEntry.getKey();
					int quantityPerTransaction = rewardEntry.getValue();
					int totalQuantity = quantityPerTransaction * transactions;

					InventoryUtils.checkInventoryForItem(inventoryContents, 12, 19, rewardStackToMatch, totalQuantity, hasSmartModule, true, stack -> DefaultDispenseItemBehavior.spawnItem(level, stack, 2, Direction.DOWN, Vec3.atCenterOf(getBlockPos()).relative(blockState.getValue(SecureTradingStation.FACING), 0.7)), inventoryContents::set);
				}
			}

			level.setBlockAndUpdate(worldPosition, blockState.cycle(SecureTradingStation.POWERED));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.SECURE_TRADING_STATION.get());

			if (signalLengthOption > 0)
				level.scheduleTick(worldPosition, SCContent.SECURE_TRADING_STATION.get(), signalLengthOption);
		}
	}

	private void handleConsumedPaymentItem(ItemStack paymentStack) {
		ItemStack remainder = paymentStack;

		if (isModuleEnabled(ModuleType.STORAGE))
			remainder = InventoryUtils.addItemToStorage(this, 4, 11, paymentStack); //This operation will set paymentStack to be empty if the stack was successfully placed into the slots

		if (!remainder.isEmpty())
			DefaultDispenseItemBehavior.spawnItem(level, remainder, 0, Direction.DOWN, Vec3.atCenterOf(getBlockPos()).relative(blockState.getValue(SecureTradingStation.FACING).getOpposite(), 0.7));
	}

	public int getReferenceLimitedTransactions(Container slotsToSearch, int start, int endInclusive, Map<ItemStack, Integer> itemReference, boolean hasSmartModule) {
		int possibleTransactions = -1;

		if (itemReference.isEmpty())
			return 1; //If no reference items are set, default to 1 transaction per button push

		for (Map.Entry<ItemStack, Integer> paymentRequest : itemReference.entrySet()) {
			ItemStack paymentStackToMatch = paymentRequest.getKey();
			int quantityPerTransaction = paymentRequest.getValue();
			int paymentItemsInInputSlots = InventoryUtils.countItemsInSlotRange(slotsToSearch, paymentStackToMatch, start, endInclusive, hasSmartModule);
			int transactions = paymentItemsInInputSlots / quantityPerTransaction;

			if (possibleTransactions == -1 || possibleTransactions > transactions)
				possibleTransactions = transactions;
		}

		return Math.max(possibleTransactions, 0);
	}

	public NonNullList<ItemStack> getContents() {
		return inventoryContents;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.DISGUISE, ModuleType.STORAGE, ModuleType.SMART
		}; //TODO: Once the block has a block model, add the field to the list of disguisable blocks in ClientHandler
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				signalLength, disabled
		};
	}

	public void setSignalLength(int signalLength) {
		if (getSignalLength() != signalLength) {
			this.signalLength.setValue(signalLength);
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, false));
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public void setDisabled(boolean disabled) {
		if (isDisabled() != disabled) {
			this.disabled.setValue(disabled);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public int getContainerSize() {
		return inventoryContents.size();
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

	@Override
	public void setItem(int index, ItemStack stack) {
		inventoryContents.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());

		setChanged();
	}

	public Map<ItemStack, Integer> getPaymentPerTransaction(boolean exactComponentCheck) {
		return getReferencePerTransaction(0, 1, exactComponentCheck);
	}

	public Map<ItemStack, Integer> getRewardPerTransaction(boolean exactComponentCheck) {
		return getReferencePerTransaction(2, 3, exactComponentCheck);
	}

	private Map<ItemStack, Integer> getReferencePerTransaction(int start, int endInclusive, boolean exactComponentCheck) {
		Map<ItemStack, Integer> payment = new HashMap<>();
		ItemStack firstPaymentReference = getItem(start);
		ItemStack secondPaymentReference = getItem(endInclusive);

		if (BlockUtils.areItemsEqual(firstPaymentReference, secondPaymentReference, exactComponentCheck)) {
			if (!firstPaymentReference.isEmpty())
				payment.put(firstPaymentReference.copyWithCount(1), firstPaymentReference.getCount() + secondPaymentReference.getCount());
		}
		else {
			if (!firstPaymentReference.isEmpty())
				payment.put(firstPaymentReference.copyWithCount(1), firstPaymentReference.getCount());

			if (!secondPaymentReference.isEmpty())
				payment.put(secondPaymentReference.copyWithCount(1), secondPaymentReference.getCount());
		}

		return payment;
	}

	public boolean hasRewardReferenceStacks() {
		return !getItem(2).isEmpty() || !getItem(3).isEmpty();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	public static IItemHandler getCapability(SecureTradingStationBlockEntity be, Direction side) {
		return BlockUtils.isAllowedToExtractFromProtectedObject(side, be) ? new SidedInvWrapper(be, side) : new InsertOnlySidedInvWrapper(be, side);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return switch (side) {
			case null -> new int[0];
			case UP, NORTH, SOUTH, WEST, EAST -> SLOTS_FOR_UP_AND_SIDES;
			case DOWN -> SLOTS_FOR_DOWN;
		};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		return true;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.STORAGE) {
			dropContents();
			inventoryContents.set(2, ItemStack.EMPTY);
			inventoryContents.set(3, ItemStack.EMPTY);
		}
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);

		if (state.getValue(SecureTradingStation.POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(SecureTradingStation.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, state.getBlock());
		}

		dropContents();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new SecureTradingStationMenu(windowId, level, worldPosition, inv);
	}

	public void dropContents() {
		//First 4 slots (0-3) are the payment & reward slots
		for (int i = 4; i < getContainerSize(); i++) {
			Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), getContents().get(i));
		}
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

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		MenuProvider.super.writeClientSideData(menu, buffer);
		buffer.writeBlockPos(worldPosition);
	}
}
