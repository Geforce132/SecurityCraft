package net.geforcemods.securitycraft.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.PayBlockBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PayBlockMenu extends AbstractContainerMenu {
	public final SimpleUpdatingContainer paymentInput = new SimpleUpdatingContainer(5, this);
	public final PayBlockBlockEntity be;
	private final boolean hasSmartModule;
	public final boolean withStorageAccess;
	private final ContainerLevelAccess worldPosCallable;

	public boolean givesOutRewardItems;
	public boolean sufficientInputForTransaction;

	public int inputLimitedTransactions = 0;
	public int rewardLimitedTransactions = 0;
	public boolean notEnoughSupply;

	public PayBlockMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory) {
		super(SCContent.PAY_BLOCK_MENU.get(), windowId);
		be = (PayBlockBlockEntity) level.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(level, pos);

		boolean isOwner = be.isOwnedBy(playerInventory.player);

		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		withStorageAccess = isOwner && be.isModuleEnabled(ModuleType.STORAGE);
		givesOutRewardItems = !be.getItem(2).isEmpty() || !be.getItem(3).isEmpty();

		//payment item reference: 0-1
		for (int i = 0; i < 2; i++) {
			addSlot(new OwnerRestrictedSlot(be, be, i, 65 + i * 18, 18, isOwner, true));
		}

		//payment input slots: 2-6
		for (int i = 0; i < 5; i++) {
			addSlot(new Slot(paymentInput, i, 17 + i * 18, 43));
		}

		if (withStorageAccess) {
			//reward item reference: 7-8
			for (int i = 0; i < 2; i++) {
				addSlot(new OwnerRestrictedSlot(be, be, 2 + i, 65 + i * 18, 93, isOwner, true));
			}

			//pay block payment storage slots: 9-16
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 4; j++) {
					addSlot(new Slot(be, 4 + j + i * 4, 13 + j * 18, 117 + i * 18));
				}
			}

			//pay block reward storage slots: 17-24
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 4; j++) {
					addSlot(new Slot(be, 12 + j + i * 4, 93 + j * 18, 117 + i * 18));
				}
			}
		}

		//main player inventory: 25-51 in storage version, 7-33 otherwise
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerInventory, 9 + j + i * 9, 8 + j * 18, 167 + i * 18));
			}
		}

		//player hotbar: 52-60  in storage version, 34-42 otherwise
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 225));
		}
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);

		notEnoughSupply = false;
		givesOutRewardItems = !be.getItem(2).isEmpty() || !be.getItem(3).isEmpty();

		int inputLimitedTransactions = getMaxPossibleTransactions(paymentInput, hasSmartModule);
		int rewardLimitedTransactions = 1;

		if (givesOutRewardItems) {
			rewardLimitedTransactions = -1;

			for (int i = 0; i < 2; i++) {
				ItemStack rewardItem = getSlot(7 + i).getItem();

				if (rewardItem.isEmpty())
					continue;

				int outputItemCount = BlockUtils.countItemsBetween(be, rewardItem, 4, be.getContainerSize() - 1, hasSmartModule);
				int supplyTransactionCount = outputItemCount / rewardItem.getCount();

				notEnoughSupply |= supplyTransactionCount < inputLimitedTransactions;

				if (rewardLimitedTransactions == -1 || rewardLimitedTransactions > supplyTransactionCount)
					rewardLimitedTransactions = Math.min(supplyTransactionCount, inputLimitedTransactions);
			}
		}

		this.inputLimitedTransactions = inputLimitedTransactions;
		this.rewardLimitedTransactions = Math.max(rewardLimitedTransactions, 0);
	}

	public int getMaxPossibleTransactions(Container paymentInput, boolean hasSmartModule) {
		int possibleTransactions = -1;
		Map<ItemStack, Integer> unifiedPaymentRequestItems = new HashMap<>();

		for (int i = 0; i < 2; i++) {
			ItemStack paymentItem = be.getItem(i);
			Optional<ItemStack> identical = unifiedPaymentRequestItems.keySet().stream().filter(stack -> BlockUtils.areItemsEqual(paymentItem, stack, hasSmartModule)).findFirst();

			if (identical.isPresent())
				unifiedPaymentRequestItems.put(paymentItem, paymentItem.getCount() + identical.get().getCount());
			else
				unifiedPaymentRequestItems.put(paymentItem, paymentItem.getCount());
		}

		for (Entry<ItemStack, Integer> paymentRequest : unifiedPaymentRequestItems.entrySet()) {
			ItemStack paymentItem = paymentRequest.getKey();
			int quantity = paymentRequest.getValue();

			if (paymentItem.isEmpty())
				continue;

			int inputItemCount = BlockUtils.countItemsBetween(paymentInput, paymentItem, 0, 4, hasSmartModule);
			int transactionCount = inputItemCount / quantity;

			if (possibleTransactions == -1 || possibleTransactions > transactionCount)
				possibleTransactions = transactionCount;
		}

		return Math.max(possibleTransactions, 0);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		clearContainer(player, paymentInput);
		be.setChanged();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack toReturn = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		int playerInvStartIndex = withStorageAccess ? 25 : 7;

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			toReturn = slotStack.copy();

			//payment input
			if (index >= 2 && index <= 6) {
				//try to move it to the player's inventory
				if (!moveItemStackTo(slotStack, playerInvStartIndex, slots.size(), true))
					return ItemStack.EMPTY;
			}
			//pay block storage
			else if (withStorageAccess && index <= 26) {
				//try to move it to the player's inventory
				if (!moveItemStackTo(slotStack, playerInvStartIndex, slots.size(), true))
					return ItemStack.EMPTY;
			}
			//player's main inventory (minus hotbar)
			else if (index >= playerInvStartIndex && index <= playerInvStartIndex + 27) {
				//try to move it to the pay block reward storage if available, then payment input
				if ((!withStorageAccess || !moveItemStackTo(slotStack, 17, 25, false)) && !moveItemStackTo(slotStack, 2, 7, false))
					return ItemStack.EMPTY;
			}
			//hotbar; try to move it to pay block reward storage if available, then payment input
			else if ((!withStorageAccess || !moveItemStackTo(slotStack, 17, 25, false)) && !moveItemStackTo(slotStack, 2, 7, false))
				return ItemStack.EMPTY;

			if (slotStack.isEmpty())
				slot.setByPlayer(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return toReturn;
	}

	@Override
	public boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		return super.moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, SCContent.PAY_BLOCK.get());
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
		if (slotId >= 0 && slotId <= 8 && clickType != ClickType.CLONE && getSlot(slotId) instanceof OwnerRestrictedSlot slot && slot.isGhostSlot()) {
			if (be.isOwnedBy(player)) {
				ItemStack pickedUpStack = getCarried().copy();

				be.getContents().set(slotId >= 7 ? slotId - 5 : slotId, pickedUpStack);
				be.setChanged();
			}
		}
		else
			super.clicked(slotId, dragType, clickType, player);
	}
}
