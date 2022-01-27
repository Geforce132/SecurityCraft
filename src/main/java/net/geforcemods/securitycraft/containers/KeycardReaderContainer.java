package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeycardReaderContainer extends Container {
	private final Inventory itemInventory = new Inventory(1);
	public final Slot keycardSlot;
	public KeycardReaderTileEntity te;
	private IWorldPosCallable worldPosCallable;

	public KeycardReaderContainer(int windowId, PlayerInventory inventory, World world, BlockPos pos) {
		super(SCContent.cTypeKeycardReader, windowId);

		TileEntity tile = world.getBlockEntity(pos);

		if (tile instanceof KeycardReaderTileEntity) {
			te = (KeycardReaderTileEntity) tile;
		}

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
				String keycardOwnerUUID = tag.getString("ownerUUID");
				String keycardOwnerName = tag.getString("ownerName");

				//only allow keycards that have been linked to a keycard reader with the same owner as this keycard reader
				return keycardOwnerUUID.isEmpty() || ((ConfigHandler.SERVER.enableTeamOwnership.get() && PlayerUtils.areOnSameTeam(te.getOwner().getName(), keycardOwnerName)) || keycardOwnerUUID.equals(te.getOwner().getUUID()));
			}
		});
	}

	public void link() {
		ItemStack keycard = keycardSlot.getItem();

		if (!keycard.isEmpty()) {
			CompoundNBT tag = keycard.getOrCreateTag();

			tag.putBoolean("linked", true);
			tag.putInt("signature", te.getSignature());
			tag.putString("ownerName", te.getOwner().getName());
			tag.putString("ownerUUID", te.getOwner().getUUID());
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
		clearContainer(player, te.getLevel(), itemInventory);
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
			else if (id < 36) {
				if (!moveItemStackTo(slotStack, 36, 37, false))
					return ItemStack.EMPTY;
			}

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
