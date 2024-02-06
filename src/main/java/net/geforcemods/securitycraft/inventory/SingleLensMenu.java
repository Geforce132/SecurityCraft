package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SingleLensMenu extends AbstractContainerMenu {
	public final BlockEntity be;
	private ContainerLevelAccess containerLevelAccess;

	public SingleLensMenu(int windowId, Level level, BlockPos pos, Inventory inventory) {
		super(SCContent.SINGLE_LENS_MENU.get(), windowId);

		containerLevelAccess = ContainerLevelAccess.create(level, pos);
		be = level.getBlockEntity(pos);

		if (((IOwnable) be).isOwnedBy(inventory.player))
			addSlot(new LensSlot(((SingleLensContainer) be).getLensContainer(), 0, 80, 20));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(inventory, x, 8 + x * 18, 142));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if (index < 1) {
				if (!moveItemStackTo(slotStack, 1, 37, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if (index >= 1 && !moveItemStackTo(slotStack, 0, 1, false))
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
	public boolean stillValid(Player player) {
		return stillValid(containerLevelAccess, player, be.getBlockState().getBlock());
	}

	public interface SingleLensContainer {
		public Container getLensContainer();
	}
}
