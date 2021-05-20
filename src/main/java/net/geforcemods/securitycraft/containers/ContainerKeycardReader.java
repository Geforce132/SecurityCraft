package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.ItemKeycard;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerKeycardReader extends Container
{
	private final InventoryBasic itemInventory = new InventoryBasic("", false, 1);
	public final Slot keycardSlot;
	public TileEntityKeycardReader te;

	public ContainerKeycardReader(InventoryPlayer inventory, TileEntityKeycardReader tile)
	{
		te = tile;

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 167 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 225));

		keycardSlot = addSlotToContainer(new Slot(itemInventory, 0, 35, 86) {
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				//only allow keycards
				//do not allow limited use keycards as they are only crafting components
				if(!(stack.getItem() instanceof ItemKeycard) || stack.getItem() == SCContent.limitedUseKeycard)
					return false;

				boolean hasTag = stack.hasTagCompound();
				String ownerUUID = hasTag ? stack.getTagCompound().getString("ownerUUID") : "";

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
			boolean hasTag = keycard.hasTagCompound();
			NBTTagCompound tag = hasTag ? keycard.getTagCompound() : new NBTTagCompound();

			tag.setBoolean("linked", true);
			tag.setInteger("signature", te.getSignature());
			tag.setString("ownerName", te.getOwner().getName());
			tag.setString("ownerUUID", te.getOwner().getUUID());

			if(!hasTag)
				keycard.setTagCompound(tag);
		}
	}

	public void setKeycardUses(int uses)
	{
		ItemStack keycard = keycardSlot.getStack();

		if(!keycard.isEmpty())
		{
			boolean hasTag = keycard.hasTagCompound();
			NBTTagCompound tag = hasTag ? keycard.getTagCompound() : new NBTTagCompound();

			if(tag.getBoolean("limited"))
				tag.setInteger("uses", uses);

			if(!hasTag)
				keycard.setTagCompound(tag);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		clearContainer(player, te.getWorld(), itemInventory);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id)
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
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
}
