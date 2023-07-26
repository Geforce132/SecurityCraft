package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TrophySystemMenu extends AbstractContainerMenu {
	public final TrophySystemBlockEntity be;
	private ContainerLevelAccess containerLevelAccess;

	public TrophySystemMenu(int windowId, Level level, BlockPos pos, Inventory inventory) {
		super(SCContent.TROPHY_SYSTEM_MENU.get(), windowId);

		containerLevelAccess = ContainerLevelAccess.create(level, pos);
		be = (TrophySystemBlockEntity) level.getBlockEntity(pos);

		if (be instanceof TrophySystemBlockEntity trophySystem && trophySystem.isOwnedBy(inventory.player))
			addSlot(new ItemRestrictedSlot(trophySystem.getContainer(), 0, 154, 6, stack -> stack.is(SCContent.LENS.get())) {
				@Override
				public int getMaxStackSize() {
					return 1;
				}
			});

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 167 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(inventory, x, 8 + x * 18, 225));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
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
			else if (index >= 1) {
				if (!moveItemStackTo(slotStack, 0, 1, false))
					return ItemStack.EMPTY;
			}

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
	public boolean stillValid(Player player) {
		return stillValid(containerLevelAccess, player, be.getBlockState().getBlock());
	}
}
