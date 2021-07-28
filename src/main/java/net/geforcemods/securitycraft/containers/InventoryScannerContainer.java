package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InventoryScannerContainer extends AbstractContainerMenu {

	private final int numRows;
	public final InventoryScannerTileEntity te;
	private ContainerLevelAccess worldPosCallable;

	public InventoryScannerContainer(int windowId, Level world, BlockPos pos, Inventory inventory){
		super(SCContent.cTypeInventoryScanner, windowId);
		te = (InventoryScannerTileEntity)world.getBlockEntity(pos);
		numRows = te.getContainerSize() / 9;
		worldPosCallable = ContainerLevelAccess.create(world, pos);

		//prohibited items
		for(int i = 0; i < 10; i++)
			addSlot(new OwnerRestrictedSlot(te, te, i, (6 + (i * 18)), 16, true));

		//inventory scanner storage
		if(te.getOwner().isOwner(inventory.player) && te.hasModule(ModuleType.STORAGE))
			for(int i = 0; i < 9; i++)
				for(int j = 0; j < 3; j++)
					addSlot(new Slot(te, 10 + ((i * 3) + j), 188 + (j * 18), 29 + i * 18));

		//inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, j + i * 9 + 9, 15 + j * 18, 115 + i * 18));

		//hotbar
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 15 + i * 18, 173));
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot != null && slot.hasItem())
		{
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if (index < numRows * 9)
			{
				if (!moveItemStackTo(slotStack, numRows * 9, slots.size(), true))
					return ItemStack.EMPTY;
			}
			else if (!moveItemStackTo(slotStack, 0, numRows * 9, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return slotStackCopy;
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void removed(Player player)
	{
		super.removed(player);

		Utils.setISinTEAppropriately(te.getLevel(), te.getBlockPos(), te.getContents());
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, SCContent.INVENTORY_SCANNER.get());
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickType, Player player)
	{
		if(slotId >= 0 && slotId < 10 && getSlot(slotId) instanceof OwnerRestrictedSlot slot && slot.isGhostSlot())
		{
			if(te.getOwner().isOwner(player))
			{
				ItemStack pickedUpStack = getCarried().copy();

				pickedUpStack.setCount(1);
				te.getContents().set(slotId, pickedUpStack);
			}

			return;
		}
		else
			super.clicked(slotId, dragType, clickType, player);
	}
}
