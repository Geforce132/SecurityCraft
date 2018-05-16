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
		setMaxDamage(151);
	}

	@Override
	public boolean isFull3D(){
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
		if(!par2World.isRemote)
		{
			if(!par1ItemStack.isItemDamaged()){
				if(par3EntityPlayer.isSneaking() && (par3EntityPlayer.capabilities.isCreativeMode || !powered))
				{
					ItemStack oneRedstone = new ItemStack(Items.redstone, 1);

					if(par3EntityPlayer.capabilities.isCreativeMode)
					{
						if(par3EntityPlayer.inventory.getCurrentItem().getItem() == SCContent.taser)
							par3EntityPlayer.inventory.setInventorySlotContents(par3EntityPlayer.inventory.currentItem, new ItemStack(SCContent.taserPowered, 1));
						else
							par3EntityPlayer.inventory.setInventorySlotContents(par3EntityPlayer.inventory.currentItem, new ItemStack(SCContent.taser, 1));
					}
					else if(par3EntityPlayer.inventory.hasItemStack(oneRedstone))
					{
						int redstoneSlot = findSlotMatchingUnusedItem(par3EntityPlayer.inventory, oneRedstone);
						ItemStack redstoneStack = par3EntityPlayer.inventory.getStackInSlot(redstoneSlot);

						redstoneStack.stackSize -= 1;
						par3EntityPlayer.inventory.setInventorySlotContents(redstoneSlot, redstoneStack);
						SecurityCraft.network.sendTo(new PacketCChangeStackSize(redstoneSlot, -1), (EntityPlayerMP)par3EntityPlayer);
						par3EntityPlayer.inventory.setInventorySlotContents(par3EntityPlayer.inventory.currentItem, new ItemStack(SCContent.taserPowered, 1));
					}

					return par1ItemStack;
				}

				par2World.spawnEntityInWorld(new EntityTaserBullet(par2World, par3EntityPlayer, powered));
				SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(par3EntityPlayer.posX, par3EntityPlayer.posY, par3EntityPlayer.posZ, SCSounds.TASERFIRED.path, 1.0F));

				if(!par3EntityPlayer.capabilities.isCreativeMode)
				{
					if(powered)
					{
						ItemStack taser = new ItemStack(SCContent.taser, 1);

						taser.damageItem(150, par3EntityPlayer);
						par3EntityPlayer.inventory.setInventorySlotContents(par3EntityPlayer.inventory.currentItem, taser);
					}
					else
						par1ItemStack.damageItem(150, par3EntityPlayer);
				}
			}
		}

		return par1ItemStack;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5){
		if(!par2World.isRemote)
			if(par1ItemStack.getItemDamage() >= 1)
				par1ItemStack.setItemDamage(par1ItemStack.getItemDamage() - 1);
	}

	public int findSlotMatchingUnusedItem(InventoryPlayer inventory, ItemStack stack)
	{
		for (int i = 0; i < inventory.mainInventory.length; ++i)
		{
			ItemStack itemstack = inventory.mainInventory[i];

			if (inventory.mainInventory[i] != null && stackEqualExact(stack, inventory.mainInventory[i]) && !inventory.mainInventory[i].isItemDamaged() && !itemstack.isItemEnchanted() && !itemstack.hasDisplayName())
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
