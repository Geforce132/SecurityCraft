package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class KeycardReaderContainer extends AbstractContainerMenu
{
	private final SimpleContainer itemInventory = new SimpleContainer(1);
	public final Slot keycardSlot;
	public KeycardReaderTileEntity te;
	private ContainerLevelAccess worldPosCallable;

	public KeycardReaderContainer(int windowId, Inventory inventory, Level world, BlockPos pos)
	{
		super(SCContent.cTypeKeycardReader, windowId);

		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof KeycardReaderTileEntity)
			te = (KeycardReaderTileEntity)tile;

		worldPosCallable = ContainerLevelAccess.create(world, pos);

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 167 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 225));

		keycardSlot = addSlot(new Slot(itemInventory, 0, 35, 86) {
			@Override
			public boolean mayPlace(ItemStack stack)
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
		ItemStack keycard = keycardSlot.getItem();

		if(!keycard.isEmpty())
		{
			CompoundTag tag = keycard.getOrCreateTag();

			tag.putBoolean("linked", true);
			tag.putInt("signature", te.getSignature());
			tag.putString("ownerName", te.getOwner().getName());
			tag.putString("ownerUUID", te.getOwner().getUUID());
		}
	}

	public void setKeycardUses(int uses)
	{
		ItemStack keycard = keycardSlot.getItem();

		if(!keycard.isEmpty())
		{
			CompoundTag tag = keycard.getOrCreateTag();

			if(tag.getBoolean("limited"))
				tag.putInt("uses", uses);
		}
	}

	@Override
	public void removed(Player player)
	{
		super.removed(player);
		clearContainer(player, te.getLevel(), itemInventory);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int id)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(id);

		if(slot != null && slot.hasItem())
		{
			ItemStack slotStack = slot.getItem();

			slotStackCopy = slotStack.copy();

			if(id >= 36)
			{
				if(!moveItemStackTo(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if(id < 36)
				if(!moveItemStackTo(slotStack, 36, 37, false))
					return ItemStack.EMPTY;

			if(slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();

			if(slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(worldPosCallable, player, SCContent.KEYCARD_READER.get());
	}
}
