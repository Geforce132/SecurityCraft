package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BlockPocketManagerMenu extends AbstractContainerMenu {
	public final BlockPocketManagerBlockEntity be;
	private ContainerLevelAccess worldPosCallable;
	public final boolean hasStorageModule;
	public final boolean isOwner;

	public BlockPocketManagerMenu(int windowId, Level level, BlockPos pos, Inventory inventory) {
		super(SCContent.BLOCK_POCKET_MANAGER_MENU.get(), windowId);

		this.be = (BlockPocketManagerBlockEntity) level.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(level, pos);
		isOwner = be.isOwnedBy(inventory.player);
		hasStorageModule = be.isModuleEnabled(ModuleType.STORAGE) && isOwner;

		if (hasStorageModule) {
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; ++x) {
					addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 74));
				}
			}

			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(inventory, x, 8 + x * 18, 142 + 74));
			}

			IItemHandler handler = new BlockPocketManagerBlockEntity.ValidityCheckItemStackHandler(be.getStorage());
			int slotId = 0;

			for (int y = 0; y < 8; y++) {
				for (int x = 0; x < 7; x++) {
					addSlot(new SlotItemHandler(handler, slotId++, 124 + x * 18, 8 + y * 18) {
						@Override
						public void initialize(ItemStack stack) {
							set(stack);
						}
					});
				}
			}
		}
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		be.setChanged();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();

			copy = slotStack.copy();

			if (index >= 36) { //block pocket manager slots
				if (!moveItemStackTo(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
			}
			else if (index >= 0 && index <= 35 && !moveItemStackTo(slotStack, 36, slots.size(), false)) //main inventory and hotbar
				return ItemStack.EMPTY;

			if (slotStack.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return copy;
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, SCContent.BLOCK_POCKET_MANAGER.get());
	}
}
