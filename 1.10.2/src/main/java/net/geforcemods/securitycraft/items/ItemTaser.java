package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
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
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || ((oldStack.getItem() == SCContent.taser && newStack.getItem() == SCContent.taserPowered) || (oldStack.getItem() == SCContent.taserPowered && newStack.getItem() == SCContent.taser));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand){
		if(!world.isRemote)
		{
			if(!stack.isItemDamaged()){
				if(player.isSneaking() && (player.isCreative() || !powered))
				{
					ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

					if(player.isCreative())
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
						player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(SCContent.taserPowered, 1));
					}

					return ActionResult.newResult(EnumActionResult.PASS, stack);
				}

				WorldUtils.addScheduledTask(world, () -> world.spawnEntity(new EntityTaserBullet(world, player, powered)));
				SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(player.posX, player.posY, player.posZ, SCSounds.TASERFIRED.path, 1.0F, "player"));

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
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected){
		if(!world.isRemote)
			if(stack.getItemDamage() >= 1)
				stack.setItemDamage(stack.getItemDamage() - 1);
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
