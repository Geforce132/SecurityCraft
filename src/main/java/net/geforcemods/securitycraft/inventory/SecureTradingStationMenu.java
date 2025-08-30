package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecureTradingStationBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
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

public class SecureTradingStationMenu extends AbstractContainerMenu {
	public final SimpleUpdatingContainer paymentInput = new SimpleUpdatingContainer(5, this);
	public final SecureTradingStationBlockEntity be;
	private final boolean hasSmartModule;
	public final boolean hasStorageModule;
	public final boolean withStorageAccess;
	private final ContainerLevelAccess worldPosCallable;
	public int paymentLimitedTransactions = 0;

	public SecureTradingStationMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory) {
		super(SCContent.SECURE_TRADING_STATION_MENU.get(), windowId);
		be = (SecureTradingStationBlockEntity) level.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(level, pos);

		boolean isOwner = be.isOwnedBy(playerInventory.player);

		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		hasStorageModule = be.isModuleEnabled(ModuleType.STORAGE);
		withStorageAccess = hasStorageModule && isOwner;

		//payment item ghost slots: 0-1
		for (int i = 0; i < 2; i++) {
			addSlot(new OwnerRestrictedSlot(this, be, be, i, 65 + i * 18, 18, isOwner, true));
		}

		//payment input slots: 2-6
		for (int i = 0; i < 5; i++) {
			addSlot(new Slot(paymentInput, i, 17 + i * 18, 43));
		}

		//reward item ghost slots: 7-8
		for (int i = 0; i < 2; i++) {
			addSlot(new OwnerRestrictedSlot(this, be, be, 2 + i, 65 + i * 18, 93, withStorageAccess, true));
		}

		if (withStorageAccess) {
			//payment storage slots: 9-16
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 4; j++) {
					addSlot(new Slot(be, 4 + j + i * 4, 13 + j * 18, 117 + i * 18));
				}
			}

			//reward storage slots: 17-24
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 4; j++) {
					addSlot(new Slot(be, 12 + j + i * 4, 93 + j * 18, 117 + i * 18));
				}
			}
		}

		//main player inventory: 25-51 in storage version, 9-35 otherwise
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerInventory, 9 + j + i * 9, 8 + j * 18, 167 + i * 18));
			}
		}

		//player hotbar: 52-60 in storage version, 36-43 otherwise
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 225));
		}
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		
		paymentLimitedTransactions = be.getReferenceLimitedTransactions(paymentInput, 0, 4, be.getPaymentPerTransaction(hasSmartModule), hasSmartModule);
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
		int playerInvStartIndex = withStorageAccess ? 25 : 9;

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			toReturn = slotStack.copy();

			//payment input
			if (index >= 2 && index <= 6) {
				//try to move it to the player's inventory
				if (!moveItemStackTo(slotStack, playerInvStartIndex, slots.size(), true))
					return ItemStack.EMPTY;
			}
			//payment and reward storage
			else if (withStorageAccess && index <= 26) {
				//try to move it to the player's inventory
				if (!moveItemStackTo(slotStack, playerInvStartIndex, slots.size(), true))
					return ItemStack.EMPTY;
			}
			//player's main inventory (minus hotbar)
			else if (index >= playerInvStartIndex && index <= playerInvStartIndex + 27) {
				//try to move it to the reward storage if available, then payment input
				if ((!withStorageAccess || !moveItemStackTo(slotStack, 17, 25, false)) && !moveItemStackTo(slotStack, 2, 7, false))
					return ItemStack.EMPTY;
			}
			//hotbar; try to move it to reward storage if available, then payment input
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
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, SCContent.SECURE_TRADING_STATION.get());
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
