package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;

public class DisguiseModuleMenu extends StateSelectorAccessMenu {
	private ModuleItemContainer inventory;

	public DisguiseModuleMenu(int windowId, PlayerInventory playerInventory, ModuleItemContainer moduleInventory) {
		super(SCContent.DISGUISE_MODULE_MENU.get(), windowId);
		inventory = moduleInventory;
		moduleInventory.setMenu(this);
		addSlot(new AddonSlot(inventory, 0, 80, 20));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if (index < 1) {
				if (!moveItemStackTo(slotStack, 1, 37, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if (!moveItemStackTo(slotStack, 0, 1, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
			broadcastChanges();
		}

		return slotStackCopy;
	}

	@Override
	public ItemStack clicked(int slot, int dragType, ClickType clickType, PlayerEntity player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem().getItem() == SCContent.DISGUISE_MODULE.get())
			return ItemStack.EMPTY;

		return super.clicked(slot, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack getStateStack() {
		return slots.get(0).getItem();
	}

	@Override
	public BlockState getSavedState() {
		return NBTUtil.readBlockState(inventory.getModule().getOrCreateTag().getCompound("SavedState"));
	}

	@Override
	public StandingOrWallType getStandingOrWallType() {
		return StandingOrWallType.values()[inventory.getModule().getOrCreateTag().getInt("StandingOrWall")];
	}

	public ModuleItemContainer getInventory() {
		return inventory;
	}

	public static class AddonSlot extends Slot {
		public AddonSlot(ModuleItemContainer inventory, int index, int xPos, int yPos) {
			super(inventory, index, xPos, yPos);
		}

		@Override
		public boolean mayPlace(ItemStack itemStack) {
			return itemStack.getItem() instanceof BlockItem;
		}

		@Override
		public int getMaxStackSize() {
			return 1;
		}
	}
}
