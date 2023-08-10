package net.geforcemods.securitycraft.inventory;

import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LaserBlockMenu extends Container {
	public final LaserBlockBlockEntity be;
	public final Map<Direction, Boolean> sideConfig;
	private IWorldPosCallable containerLevelAccess;

	public LaserBlockMenu(int windowId, World level, BlockPos pos, Map<Direction, Boolean> sideConfig, PlayerInventory inventory) {
		super(SCContent.LASER_BLOCK_MENU.get(), windowId);

		containerLevelAccess = IWorldPosCallable.create(level, pos);
		be = (LaserBlockBlockEntity) level.getBlockEntity(pos);
		this.sideConfig = sideConfig;

		if (be.isOwnedBy(inventory.player)) {
			Inventory container = be.getLensContainer();

			for (int i = 0; i < 6; i++) {
				addSlot(new LensSlot(container, i, 15, i * 22 + 27));
			}
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 174 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(inventory, x, 8 + x * 18, 232));
		}
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if (index < 6) {
				if (!moveItemStackTo(slotStack, 6, 42, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if (!moveItemStackTo(slotStack, 0, 6, false))
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
	public boolean stillValid(PlayerEntity player) {
		return stillValid(containerLevelAccess, player, be.getBlockState().getBlock());
	}
}
