package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockChangeDetectorMenu extends AbstractContainerMenu {
	public final BlockEntity be;
	private ContainerLevelAccess containerLevelAccess;

	public BlockChangeDetectorMenu(int windowId, Level level, BlockPos pos, Inventory inventory) {
		super(SCContent.BLOCK_CHANGE_DETECTOR_MENU.get(), windowId);

		containerLevelAccess = ContainerLevelAccess.create(level, pos);
		be = level.getBlockEntity(pos);

		if (be instanceof BlockChangeDetectorBlockEntity blockChangeDetector && blockChangeDetector.isOwnedBy(inventory.player)) {
			addSlot(new Slot(blockChangeDetector, 36, 175, 44) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return ((BlockChangeDetectorBlockEntity) be).isModuleEnabled(ModuleType.SMART) && stack.getItem() instanceof BlockItem;
				}

				@Override
				public int getMaxStackSize() {
					return 1;
				}
			});
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 90));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(inventory, x, 8 + x * 18, 142 + 90));
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
}
