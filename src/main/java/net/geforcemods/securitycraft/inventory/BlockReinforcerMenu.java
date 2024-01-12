package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BlockReinforcerMenu extends Container {
	private ItemStack blockReinforcer;
	private InventoryBasic itemInventory = new InventoryBasic("BlockReinforcer", true, 2);
	public final SlotBlockReinforcer reinforcingSlot;
	public final SlotBlockReinforcer unreinforcingSlot;
	public final boolean isLvl1, isReinforcing;

	public BlockReinforcerMenu(EntityPlayer player, InventoryPlayer inventory, boolean isLvl1) {
		blockReinforcer = player.getHeldItemMainhand().getItem() instanceof UniversalBlockReinforcerItem ? player.getHeldItemMainhand() : player.getHeldItemOffhand();

		//main player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 104 + i * 18));
			}
		}

		//player hotbar
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 162));
		}

		this.isLvl1 = isLvl1;
		this.isReinforcing = UniversalBlockReinforcerItem.isReinforcing(blockReinforcer);
		addSlotToContainer(reinforcingSlot = new SlotBlockReinforcer(itemInventory, 0, 26, 20, true));

		if (!isLvl1)
			addSlotToContainer(unreinforcingSlot = new SlotBlockReinforcer(itemInventory, 1, 26, 45, false));
		else
			unreinforcingSlot = null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);

		if (!itemInventory.getStackInSlot(0).isEmpty()) {
			if (itemInventory.getStackInSlot(0).getCount() > reinforcingSlot.output.getCount()) { //if there's more in the slot than the reinforcer can reinforce (due to durability)
				ItemStack overflowStack = itemInventory.getStackInSlot(0).copy();

				overflowStack.setCount(itemInventory.getStackInSlot(0).getCount() - reinforcingSlot.output.getCount());
				player.dropItem(overflowStack, false);
			}

			player.dropItem(reinforcingSlot.output, false);
			blockReinforcer.damageItem(reinforcingSlot.output.getCount(), player);
		}

		if (!isLvl1 && !itemInventory.getStackInSlot(1).isEmpty()) {
			if (itemInventory.getStackInSlot(1).getCount() > unreinforcingSlot.output.getCount()) {
				ItemStack overflowStack = itemInventory.getStackInSlot(1).copy();

				overflowStack.setCount(itemInventory.getStackInSlot(1).getCount() - unreinforcingSlot.output.getCount());
				player.dropItem(overflowStack, false);
			}

			player.dropItem(unreinforcingSlot.output, false);
			blockReinforcer.damageItem(unreinforcingSlot.output.getCount(), player);
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(id);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();

			slotStackCopy = slotStack.copy();

			if (id >= 36) {
				if (!mergeItemStack(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if (!mergeItemStack(slotStack, 36, fixSlot(38), false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	private int fixSlot(int slot) {
		return isLvl1 ? slot - 1 : slot;
	}

	//edited to check if the item to be merged is valid in that slot
	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
		boolean merged = false;
		int currentIndex = startIndex;

		if (useEndIndex)
			currentIndex = endIndex - 1;

		Slot slot;
		ItemStack slotStack;

		if (stack.isStackable()) {
			while (stack.getCount() > 0 && (!useEndIndex && currentIndex < endIndex || useEndIndex && currentIndex >= startIndex)) {
				slot = inventorySlots.get(currentIndex);
				slotStack = slot.getStack();

				if (!slotStack.isEmpty() && slotStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == slotStack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, slotStack) && slot.isItemValid(stack)) {
					int combinedCount = slotStack.getCount() + stack.getCount();

					if (combinedCount <= stack.getMaxStackSize()) {
						stack.setCount(0);
						slotStack.setCount(combinedCount);
						slot.onSlotChanged();
						merged = true;
					}
					else if (slotStack.getCount() < stack.getMaxStackSize()) {
						stack.shrink(stack.getMaxStackSize() - slotStack.getCount());
						slotStack.setCount(stack.getMaxStackSize());
						slot.onSlotChanged();
						merged = true;
					}
				}

				if (useEndIndex)
					--currentIndex;
				else
					++currentIndex;
			}
		}

		if (stack.getCount() > 0) {
			if (useEndIndex)
				currentIndex = endIndex - 1;
			else
				currentIndex = startIndex;

			while (!useEndIndex && currentIndex < endIndex || useEndIndex && currentIndex >= startIndex) {
				slot = inventorySlots.get(currentIndex);
				slotStack = slot.getStack();

				if (slotStack.isEmpty() && slot.isItemValid(stack)) {
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.setCount(0);
					merged = true;
					break;
				}

				if (useEndIndex)
					--currentIndex;
				else
					++currentIndex;
			}
		}

		return merged;
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack().getItem() instanceof UniversalBlockReinforcerItem)
			return ItemStack.EMPTY;

		return super.slotClick(slot, dragType, clickType, player);
	}

	public class SlotBlockReinforcer extends Slot {
		private final boolean reinforce;
		private ItemStack output = ItemStack.EMPTY;

		public SlotBlockReinforcer(IInventory inventory, int index, int x, int y, boolean reinforce) {
			super(inventory, index, x, y);

			this.reinforce = reinforce;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			//can only reinforce OR unreinforce at once
			if (!itemInventory.getStackInSlot((slotNumber + 1) % 2).isEmpty())
				return false;

			Item item = stack.getItem();
			Block block = Block.getBlockFromItem(item);

			if (reinforce)
				return IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.containsKey(block) || item == Items.CAULDRON;
			else if (block instanceof IReinforcedBlock) {
				NonNullList<ItemStack> subBlocks = NonNullList.create();

				block.getSubBlocks(SecurityCraft.DECORATION_TAB, subBlocks);
				return subBlocks.stream().anyMatch(subBlock -> stack.getMetadata() == subBlock.getMetadata() && stack.getItem() == subBlock.getItem());
			}

			return false;
		}

		@Override
		public void onSlotChanged() {
			ItemStack stack = itemInventory.getStackInSlot(slotNumber % 2);

			if (!stack.isEmpty()) {
				Item itemToConvert = stack.getItem();
				Block blockToConvert = Block.getBlockFromItem(itemToConvert);
				ItemStack newStack = ItemStack.EMPTY;

				if (reinforce) {
					Block convertedBlock = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(blockToConvert);

					if (convertedBlock instanceof IReinforcedBlock)
						newStack = ((IReinforcedBlock) convertedBlock).convertToReinforcedStack(stack, blockToConvert);
					else if (itemToConvert == Items.CAULDRON)
						newStack = new ItemStack(SCContent.reinforcedCauldron);
				}
				else if (blockToConvert instanceof IReinforcedBlock) {
					try {
						newStack = ((IReinforcedBlock) blockToConvert).convertToVanillaStack(stack);
					}
					catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}

				if (!newStack.isEmpty()) {
					boolean isLvl3 = blockReinforcer.getItem() == SCContent.universalBlockReinforcerLvL3;

					newStack.setCount(isLvl3 ? stack.getCount() : Math.min(stack.getCount(), blockReinforcer.getMaxDamage() - blockReinforcer.getItemDamage() + 1));
					output = newStack;
				}
			}
		}

		public ItemStack getOutput() {
			return output;
		}
	}
}
