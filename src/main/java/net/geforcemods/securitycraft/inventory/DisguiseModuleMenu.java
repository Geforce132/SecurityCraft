package net.geforcemods.securitycraft.inventory;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.SavedBlockState;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DisguiseModuleMenu extends StateSelectorAccessMenu {
	private ModuleItemContainer inventory;

	public DisguiseModuleMenu(int windowId, Inventory playerInventory, ModuleItemContainer moduleInventory) {
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
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			int size = inventory.getContainerSize();

			slotStackCopy = slotStack.copy();

			if (index < size) {
				if (!moveItemStackTo(slotStack, size, 37, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if (!moveItemStackTo(slotStack, 0, size, false))
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
	public void clicked(int slot, int dragType, ClickType clickType, Player player) {
		if (!(slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem().getItem() == SCContent.DISGUISE_MODULE.get()))
			super.clicked(slot, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack getStateStack() {
		DefaultVertexFormat.POSITION_COLOR;
		return slots.get(0).getItem();
	}

	@Override
	public BlockState getSavedState() {
		return inventory.getModule().getOrDefault(SCContent.SAVED_BLOCK_STATE, SavedBlockState.EMPTY).state();
	}

	@Override
	public StandingOrWallType getStandingOrWallType() {
		return inventory.getModule().getOrDefault(SCContent.SAVED_BLOCK_STATE, SavedBlockState.EMPTY).standingOrWallType();
	}

	public ModuleItemContainer getInventory() {
		return inventory;
	}

	@Override
	public void removed(Player player) {
		inventory.stopOpen(player);
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
