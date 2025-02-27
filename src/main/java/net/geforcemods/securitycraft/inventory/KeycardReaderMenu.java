package net.geforcemods.securitycraft.inventory;

import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.components.KeycardData;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class KeycardReaderMenu extends AbstractContainerMenu {
	private final SimpleContainer itemInventory = new SimpleContainer(1);
	public final Slot keycardSlot;
	public final KeycardReaderBlockEntity be;
	private ContainerLevelAccess worldPosCallable;

	public KeycardReaderMenu(int windowId, Inventory inventory, Level level, BlockPos pos) {
		super(SCContent.KEYCARD_READER_MENU.get(), windowId);

		be = (KeycardReaderBlockEntity) level.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(level, pos);

		//main player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 167 + i * 18));
			}
		}

		//player hotbar
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(inventory, i, 8 + i * 18, 225));
		}

		keycardSlot = addSlot(new Slot(itemInventory, 0, 35, 86) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				//only allow keycards
				//do not allow limited use keycards as they are only crafting components
				if (!(stack.getItem() instanceof KeycardItem) || stack.getItem() == SCContent.LIMITED_USE_KEYCARD.get())
					return false;

				if (!stack.has(SCContent.KEYCARD_DATA) || !stack.has(SCContent.OWNER_DATA))
					return true;

				Owner keycardOwner = stack.getOrDefault(SCContent.OWNER_DATA, OwnerData.DEFAULT).toOwner();
				String keycardOwnerUUID = keycardOwner.getUUID();

				//only allow keycards that have been linked to a keycard reader with the same owner as this keycard reader
				return keycardOwnerUUID.isEmpty() || TeamUtils.areOnSameTeam(be.getOwner(), keycardOwner) || keycardOwnerUUID.equals(be.getOwner().getUUID());
			}
		});
	}

	public void link(Optional<String> usableBy) {
		ItemStack keycard = keycardSlot.getItem();

		if (!keycard.isEmpty()) {
			keycard.update(SCContent.KEYCARD_DATA, KeycardData.DEFAULT, oldData -> oldData.setSignatureAndUsableBy(be.getSignature(), usableBy));
			keycard.set(SCContent.OWNER_DATA, OwnerData.fromOwner(be.getOwner(), false));
		}
	}

	public void setKeycardUsesLeft(int usesLeft) {
		ItemStack keycard = keycardSlot.getItem();

		if (!keycard.isEmpty()) {
			KeycardData keycardData = keycard.getOrDefault(SCContent.KEYCARD_DATA, KeycardData.DEFAULT);

			if (keycardData.limited())
				keycard.set(SCContent.KEYCARD_DATA, keycardData.setUsesLeft(usesLeft));
		}
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		clearContainer(player, itemInventory);
		be.setChanged();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int id) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(id);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();

			slotStackCopy = slotStack.copy();

			if (id >= 36) {
				if (!moveItemStackTo(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if (!moveItemStackTo(slotStack, 36, 37, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, SCContent.KEYCARD_READER.get());
	}
}
