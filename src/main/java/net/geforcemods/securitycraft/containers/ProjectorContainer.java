package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProjectorContainer extends Container {

	public static final int SIZE = 1;

	public ProjectorTileEntity te;

	public ProjectorContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory)
	{
		super(SCContent.cTypeProjector, windowId);

		if(world.getTileEntity(pos) instanceof ProjectorTileEntity)
			te = (ProjectorTileEntity) world.getTileEntity(pos);

		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; ++x)
				addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 59));

		for(int x = 0; x < 9; x++)
			addSlot(new Slot(inventory, x, 8 + x * 18, 142 + 59));

		// A custom slot that prevents non-Block items from being inserted into the projector
		addSlot(new Slot(te, 36, 79, 23)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return stack.getItem() instanceof BlockItem;
			}
		});
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if(index == 36) {
				if(!mergeItemStack(slotStack, 0, 36, false))
					return ItemStack.EMPTY;
			}
			else {
				if(!mergeItemStack(slotStack, 36, 37, false))
					return ItemStack.EMPTY;
			}

			if(slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if(slotStack.getCount() == slotStack.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return true;
	}

}
