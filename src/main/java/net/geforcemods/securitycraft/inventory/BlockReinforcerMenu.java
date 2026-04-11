package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockReinforcerMenu extends AbstractContainerMenu {
	private final ItemStack blockReinforcer;
	private final SimpleContainer itemInventory = new SimpleContainer(1);
	public final SlotBlockReinforcer blockReinforcerSlot;
	public final boolean isLvl1, isReinforcing;

	public BlockReinforcerMenu(int windowId, Inventory inventory, boolean isLvl1) {
		super(SCContent.BLOCK_REINFORCER_MENU.get(), windowId);

		Player player = inventory.player;

		blockReinforcer = player.getMainHandItem().getItem() instanceof UniversalBlockReinforcerItem ? player.getMainHandItem() : player.getOffhandItem();
		this.isLvl1 = isLvl1;
		this.isReinforcing = UniversalBlockReinforcerItem.isReinforcing(blockReinforcer);

		//main player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 104 + i * 18));
			}
		}

		//player hotbar
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(inventory, i, 8 + i * 18, 162));
		}

		addSlot(blockReinforcerSlot = new SlotBlockReinforcer(itemInventory, 0, 26, 20));
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);

		if (!player.isAlive() || player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected()) {
			for (int slot = 0; slot < itemInventory.getContainerSize(); ++slot) {
				player.drop(itemInventory.removeItemNoUpdate(slot), false);
			}

			return;
		}

		if (!itemInventory.getItem(0).isEmpty()) {
			if (itemInventory.getItem(0).getCount() > blockReinforcerSlot.output.getCount()) { //if there's more in the slot than the reinforcer can reinforce (due to durability)
				ItemStack overflowStack = itemInventory.getItem(0).copy();

				overflowStack.setCount(itemInventory.getItem(0).getCount() - blockReinforcerSlot.output.getCount());
				player.drop(overflowStack, false);
			}

			player.drop(blockReinforcerSlot.output, false);
			blockReinforcer.hurtAndBreak(blockReinforcerSlot.output.getCount(), player, player.getUsedItemHand().asEquipmentSlot());
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int id) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(id);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();

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

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public void clicked(int slot, int dragType, ContainerInput containerInput, Player player) {
		if (!(slot >= 0 && getSlot(slot).getItem().getItem() instanceof UniversalBlockReinforcerItem))
			super.clicked(slot, dragType, containerInput, player);
	}

	public class SlotBlockReinforcer extends Slot {
		private ItemStack output = ItemStack.EMPTY;

		public SlotBlockReinforcer(Container inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			Block inputBlock = Block.byItem(stack.getItem());

			return IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.containsKey(inputBlock) || (!isLvl1 && IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.containsKey(inputBlock));
		}

		@Override
		public void setChanged() {
			ItemStack stack = getItem();

			if (!stack.isEmpty()) {
				Block inputBlock = Block.byItem(stack.getItem());
				Block outputBlock = (!isLvl1 && IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.containsKey(inputBlock) ? IReinforcedBlock.SECURITYCRAFT_TO_VANILLA : IReinforcedBlock.VANILLA_TO_SECURITYCRAFT).get(inputBlock);

				if (outputBlock != null) {
					output = new ItemStack(outputBlock);
					output.setCount(blockReinforcer.isDamageableItem() ? Math.min(stack.getCount(), blockReinforcer.getMaxDamage() - blockReinforcer.getDamageValue()) : stack.getCount());
				}
			}
		}

		public ItemStack getOutput() {
			return output;
		}
	}
}
