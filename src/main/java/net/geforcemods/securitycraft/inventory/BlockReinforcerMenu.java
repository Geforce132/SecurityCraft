package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockReinforcerMenu extends AbstractContainerMenu {
	private final ItemStack blockReinforcer;
	private final SimpleContainer itemInventory = new SimpleContainer(2);
	private final ReinforcerInputSlot inputSlot;
	private final ReinforcerResultSlot resultSlot;
	public final boolean isLvl1, isReinforcing;

	public BlockReinforcerMenu(int windowId, Inventory inventory, boolean isLvl1) {
		super(SCContent.BLOCK_REINFORCER_MENU.get(), windowId);

		Player player = inventory.player;

		blockReinforcer = player.getMainHandItem().getItem() instanceof UniversalBlockReinforcerItem ? player.getMainHandItem() : player.getOffhandItem();
		this.isLvl1 = isLvl1;
		this.isReinforcing = UniversalBlockReinforcerItem.isReinforcing(blockReinforcer);

		addStandardInventorySlots(inventory, 8, 104);
		addSlot(inputSlot = new ReinforcerInputSlot(itemInventory, 0, 26, 20));
		addSlot(resultSlot = new ReinforcerResultSlot(itemInventory, 1, 134, 20));
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		resultSlot.set(ItemStack.EMPTY);
		clearContainer(player, itemInventory);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int id) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(id);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			ItemStack itemsTaken;

			slotStackCopy = slotStack.copy();

			if (id >= 36 && !moveItemStackTo(slotStack, 0, 36, true))
				return ItemStack.EMPTY;
			else if (id < 36 && !moveItemStackTo(slotStack, 36, 37, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.setByPlayer(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			itemsTaken = slotStackCopy.copyWithCount(slotStackCopy.getCount() - slotStack.getCount());
			slot.onTake(player, itemsTaken); //The second parameter is different from vanilla, but this makes implementation easier
		}

		return slotStackCopy;
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack carried, Slot target) {
		return target != resultSlot && super.canTakeItemForPickAll(carried, target);
	}

	public ItemStack getResult() {
		return resultSlot.getItem();
	}

	@Override
	public void clicked(int slot, int dragType, ClickType clickType, Player player) {
		if (!(slot >= 0 && getSlot(slot).getItem().getItem() instanceof UniversalBlockReinforcerItem))
			super.clicked(slot, dragType, clickType, player);
	}

	public class ReinforcerInputSlot extends Slot {
		public ReinforcerInputSlot(Container inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			Block inputBlock = Block.byItem(stack.getItem());

			return IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.containsKey(inputBlock) || (!isLvl1 && IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.containsKey(inputBlock));
		}

		@Override
		public void setChanged() {
			super.setChanged();

			ItemStack stack = getItem();

			if (stack.isEmpty())
				resultSlot.set(ItemStack.EMPTY);
			else {
				Block inputBlock = Block.byItem(stack.getItem());
				Block outputBlock = (!isLvl1 && IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.containsKey(inputBlock) ? IReinforcedBlock.SECURITYCRAFT_TO_VANILLA : IReinforcedBlock.VANILLA_TO_SECURITYCRAFT).get(inputBlock);
				int resultCount = blockReinforcer.isDamageableItem() ? Math.min(stack.getCount(), blockReinforcer.getMaxDamage() - blockReinforcer.getDamageValue()) : stack.getCount();

				resultSlot.set(new ItemStack(outputBlock, resultCount));
			}
		}
	}

	public class ReinforcerResultSlot extends Slot {
		public ReinforcerResultSlot(Container inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}

		@Override
		public void onTake(Player player, ItemStack itemsTaken) {
			super.onTake(player, itemsTaken);
			inputSlot.getItem().shrink(itemsTaken.getCount());
			blockReinforcer.hurtAndBreak(itemsTaken.getCount(), player, player.getUsedItemHand().asEquipmentSlot());

			if (blockReinforcer.isEmpty()) //Ran out of durability
				player.closeContainer();
		}
	}
}
