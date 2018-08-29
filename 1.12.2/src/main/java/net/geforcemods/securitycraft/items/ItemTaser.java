package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		ItemStack itemStackIn = playerIn.getHeldItem(hand);

		if(!worldIn.isRemote)
		{
			if(!itemStackIn.isItemDamaged()){
				if(playerIn.isSneaking() && (playerIn.isCreative() || !powered))
				{
					ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

					if(playerIn.isCreative())
					{
						if(playerIn.inventory.getCurrentItem().getItem() == SCContent.taser)
							playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, new ItemStack(SCContent.taserPowered, 1));
						else
							playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, new ItemStack(SCContent.taser, 1));
					}
					else if(playerIn.inventory.hasItemStack(oneRedstone))
					{
						int redstoneSlot = playerIn.inventory.findSlotMatchingUnusedItem(oneRedstone);
						ItemStack redstoneStack = playerIn.inventory.getStackInSlot(redstoneSlot);

						redstoneStack.setCount(redstoneStack.getCount() - 1);
						playerIn.inventory.setInventorySlotContents(redstoneSlot, redstoneStack);
						playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, new ItemStack(SCContent.taserPowered, 1));
					}

					return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
				}

				WorldUtils.addScheduledTask(worldIn, () -> worldIn.spawnEntity(new EntityTaserBullet(worldIn, playerIn, powered)));
				SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(playerIn.posX, playerIn.posY, playerIn.posZ, SCSounds.TASERFIRED.path, 1.0F, "player"));

				if(!playerIn.capabilities.isCreativeMode)
				{
					if(powered)
					{
						ItemStack taser = new ItemStack(SCContent.taser, 1);

						taser.damageItem(150, playerIn);
						playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, taser);
					}
					else
						itemStackIn.damageItem(150, playerIn);
				}
			}
		}

		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5){
		if(!par2World.isRemote)
			if(par1ItemStack.getItemDamage() >= 1)
				par1ItemStack.setItemDamage(par1ItemStack.getItemDamage() - 1);
	}

}
