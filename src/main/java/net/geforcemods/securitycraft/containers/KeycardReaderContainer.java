package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeycardReaderContainer extends Container
{
	private final Inventory itemInventory = new Inventory(1);
	public final Slot keycardSlot;
	public KeycardReaderTileEntity te;

	public KeycardReaderContainer(int windowId, PlayerInventory inventory, World world, BlockPos pos)
	{
		super(SCContent.cTypeKeycardReader, windowId);

		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof KeycardReaderTileEntity)
			te = (KeycardReaderTileEntity)tile;

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 167 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 225));

		keycardSlot = addSlot(new Slot(itemInventory, 0, 35, 86) {
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				//only allow keycards
				//do not allow limited use keycards as they are only crafting components
				if(!(stack.getItem() instanceof KeycardItem) || stack.getItem() == SCContent.LIMITED_USE_KEYCARD.get())
					return false;

				boolean hasTag = stack.hasTag();
				String ownerUUID = hasTag ? stack.getTag().getString("ownerUUID") : "";

				//only allow keycards that have been linked to a keycard reader with the same owner as this keycard reader
				return !hasTag || ownerUUID.isEmpty() || ownerUUID.equals(te.getOwner().getUUID());
			}
		});
	}

	public void link()
	{
		ItemStack keycard = keycardSlot.getStack();

		if(!keycard.isEmpty())
		{
			CompoundNBT tag = keycard.getOrCreateTag();

			tag.putBoolean("linked", true);
			tag.putInt("signature", te.getSignature());
			tag.putString("ownerName", te.getOwner().getName());
			tag.putString("ownerUUID", te.getOwner().getUUID());
		}
	}

	public void setKeycardUses(int uses)
	{
		ItemStack keycard = keycardSlot.getStack();

		if(!keycard.isEmpty())
		{
			CompoundNBT tag = keycard.getOrCreateTag();

			if(tag.getBoolean("limited"))
				tag.putInt("uses", uses);
		}
	}

	@Override
	public void onContainerClosed(PlayerEntity player)
	{
		super.onContainerClosed(player);
		clearContainer(player, te.getWorld(), itemInventory);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int id)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(id);

		if(slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();

			slotStackCopy = slotStack.copy();

			if(id >= 36)
			{
				if(!mergeItemStack(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if(id < 36)
				if(!mergeItemStack(slotStack, 36, 37, false))
					return ItemStack.EMPTY;

			if(slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if(slotStack.getCount() == slotStackCopy.getCount())
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
