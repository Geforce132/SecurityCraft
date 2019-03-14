package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class ItemTaser extends Item {

	public boolean powered;

	public ItemTaser(boolean isPowered){
		super(new Item.Properties().defaultMaxDamage(150).maxStackSize(1).group(isPowered ? null : SecurityCraft.groupSCTechnical));

		powered = isPowered;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if(group == SecurityCraft.groupSCTechnical && powered)
			items.add(new ItemStack(this));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || ((oldStack.getItem() == SCContent.taser && newStack.getItem() == SCContent.taserPowered) || (oldStack.getItem() == SCContent.taserPowered && newStack.getItem() == SCContent.taser));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);

		if(!world.isRemote)
		{
			if(!stack.isDamaged()){
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
						int redstoneSlot = player.inventory.findSlotMatchingUnusedItem(oneRedstone);
						ItemStack redstoneStack = player.inventory.getStackInSlot(redstoneSlot);

						redstoneStack.setCount(redstoneStack.getCount() - 1);
						player.inventory.setInventorySlotContents(redstoneSlot, redstoneStack);
						player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(SCContent.taserPowered, 1));
					}

					return ActionResult.newResult(EnumActionResult.PASS, stack);
				}

				WorldUtils.addScheduledTask(world, () -> world.spawnEntity(new EntityTaserBullet(world, player, powered)));
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(player.posX, player.posY, player.posZ, SCSounds.TASERFIRED.path, 1.0F, "player"));

				if(!player.isCreative())
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
	public void inventoryTick(ItemStack par1ItemStack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(!world.isRemote)
			if(par1ItemStack.getDamage() >= 1)
				par1ItemStack.setDamage(par1ItemStack.getDamage() - 1);
	}

}
