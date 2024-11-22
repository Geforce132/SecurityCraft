package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeycardReaderMenu extends Container {
	private final Inventory itemInventory = new Inventory(1);
	public final Slot keycardSlot;
	public final KeycardReaderBlockEntity be;
	private final IWorldPosCallable worldPosCallable;

	public KeycardReaderMenu(int windowId, PlayerInventory inventory, World world, BlockPos pos) {
		super(SCContent.KEYCARD_READER_MENU.get(), windowId);

		be = (KeycardReaderBlockEntity) world.getBlockEntity(pos);
		worldPosCallable = IWorldPosCallable.create(world, pos);

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

				if (!stack.hasTag())
					return true;

				CompoundNBT tag = stack.getTag();
				Owner keycardOwner = new Owner(tag.getString("ownerName"), tag.getString("ownerUUID"));
				String keycardOwnerUUID = keycardOwner.getUUID();

				//only allow keycards that have been linked to a keycard reader with the same owner as this keycard reader
				return keycardOwnerUUID.isEmpty() || TeamUtils.areOnSameTeam(be.getOwner(), keycardOwner) || keycardOwnerUUID.equals(be.getOwner().getUUID());
			}
		});
	}

	public void link(String usableBy) {
		ItemStack keycard = keycardSlot.getItem();

		if (!keycard.isEmpty()) {
			CompoundNBT tag = keycard.getOrCreateTag();

			tag.putBoolean("linked", true);
			tag.putInt("signature", be.getSignature());
			tag.putString("ownerName", be.getOwner().getName());
			tag.putString("ownerUUID", be.getOwner().getUUID());

			if (usableBy != null && !usableBy.isEmpty())
				tag.putString("usable_by", usableBy);
			else
				tag.remove("usable_by");
		}
	}

	public void setKeycardUses(int uses) {
		ItemStack keycard = keycardSlot.getItem();

		if (!keycard.isEmpty()) {
			CompoundNBT tag = keycard.getOrCreateTag();

			if (tag.getBoolean("limited"))
				tag.putInt("uses", uses);
		}
	}

	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);
		clearContainer(player, be.getLevel(), itemInventory);
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int id) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(id);

		if (slot != null && slot.hasItem()) {
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
	public boolean stillValid(PlayerEntity player) {
		return stillValid(worldPosCallable, player, SCContent.KEYCARD_READER.get());
	}
}
