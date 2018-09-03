package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCChangeStackSize;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTaser extends Item {

	public boolean powered;

	public ItemTaser(boolean isPowered){
		super();
		powered = isPowered;
		setMaxDurability(151);
	}

	@Override
	public boolean isFull3D(){
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		if(!world.isRemote)
			if(!stack.isItemDamaged()){
				if(player.isSneaking() && (player.capabilities.isCreativeMode || !powered))
				{
					ItemStack oneRedstone = new ItemStack(Items.redstone, 1);

					if(player.capabilities.isCreativeMode)
					{
						if(player.inventory.getCurrentItem().getItem() == SCContent.taser)
							player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(SCContent.taserPowered, 1));
						else
							player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(SCContent.taser, 1));
					}
					else if(player.inventory.hasItemStack(oneRedstone))
					{
						int redstoneSlot = findSlotMatchingUnusedItem(player.inventory, oneRedstone);
						ItemStack redstoneStack = player.inventory.getStackInSlot(redstoneSlot);

						redstoneStack.stackSize -= 1;
						player.inventory.setInventorySlotContents(redstoneSlot, redstoneStack);
						SecurityCraft.network.sendTo(new PacketCChangeStackSize(redstoneSlot, -1), (EntityPlayerMP)player);
						player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(SCContent.taserPowered, 1));
					}

					return stack;
				}

				world.spawnEntityInWorld(new EntityTaserBullet(world, player, powered));
				SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(player.posX, player.posY, player.posZ, SCSounds.TASERFIRED.path, 1.0F));

				if(!player.capabilities.isCreativeMode)
				{
					if(powered)
					{
						ItemStack taser = new ItemStack(SCContent.taser, 1);

						taser.damageItem(150, player);
						player.inventory.setInventorySlotContents(player.inventory.currentItem, taser);
					}
					else
						stack.damageItem(150, player);
				}
			}

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean update){
		if(!world.isRemote)
			if(stack.getMetadata() >= 1)
				stack.setMetadata(stack.getMetadata() - 1);
	}

	public int findSlotMatchingUnusedItem(InventoryPlayer inventory, ItemStack stack)
	{
		for (int i = 0; i < inventory.mainInventory.length; ++i)
		{
			ItemStack itemstack = inventory.mainInventory[i];

			if (itemstack != null && stackEqualExact(stack, itemstack) && !itemstack.isItemDamaged() && !itemstack.isItemEnchanted() && !itemstack.hasDisplayName())
			{
				return i;
			}
		}

		return -1;
	}

	private boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
	{
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}
}
