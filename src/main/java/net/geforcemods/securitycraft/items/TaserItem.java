package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.TaserBulletEntity;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class TaserItem extends Item {

	public boolean powered;

	public TaserItem(boolean isPowered){
		super(new Item.Properties().defaultMaxDamage(151).group(isPowered ? null : SecurityCraft.groupSCTechnical));

		powered = isPowered;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if((group == SecurityCraft.groupSCTechnical || group == ItemGroup.SEARCH) && !powered)
			items.add(new ItemStack(this));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || ((oldStack.getItem() == SCContent.taser && newStack.getItem() == SCContent.taserPowered) || (oldStack.getItem() == SCContent.taserPowered && newStack.getItem() == SCContent.taser));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getHeldItem(hand);

		if(!world.isRemote)
		{
			if(!stack.isDamaged()){
				if(player.func_225608_bj_() && (player.isCreative() || !powered)) //isCrouching
				{
					ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

					if(player.isCreative())
					{
						if(player.getHeldItem(hand).getItem() == SCContent.taser)
							setSlotBasedOnHand(player, hand, new ItemStack(SCContent.taserPowered, 1));
						else
							setSlotBasedOnHand(player, hand, new ItemStack(SCContent.taser, 1));
					}
					else if(player.inventory.hasItemStack(oneRedstone))
					{
						int redstoneSlot = player.inventory.findSlotMatchingUnusedItem(oneRedstone);
						ItemStack redstoneStack = player.inventory.getStackInSlot(redstoneSlot);

						redstoneStack.setCount(redstoneStack.getCount() - 1);
						player.inventory.setInventorySlotContents(redstoneSlot, redstoneStack);
						setSlotBasedOnHand(player, hand, new ItemStack(SCContent.taserPowered, 1));
					}

					return ActionResult.func_226250_c_(stack); //pass
				}

				WorldUtils.addScheduledTask(world, () -> world.addEntity(new TaserBulletEntity(world, player, powered)));
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(player.getPosX(), player.getPosY(), player.getPosZ(), SCSounds.TASERFIRED.path, 1.0F, "players"));

				if(!player.isCreative())
				{
					if(powered)
					{
						ItemStack taser = new ItemStack(SCContent.taser, 1);

						taser.damageItem(150, player, p -> p.sendBreakAnimation(hand));
						setSlotBasedOnHand(player, hand, taser);
					}
					else
						stack.damageItem(150, player, p -> p.sendBreakAnimation(hand));
				}
			}
		}

		return ActionResult.func_226250_c_(stack); //pass
	}

	private void setSlotBasedOnHand(PlayerEntity player, Hand hand, ItemStack taser)
	{
		if(hand == Hand.MAIN_HAND)
			player.setItemStackToSlot(EquipmentSlotType.MAINHAND, taser);
		else
			player.setItemStackToSlot(EquipmentSlotType.OFFHAND, taser);
	}

	@Override
	public void inventoryTick(ItemStack par1ItemStack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(!world.isRemote)
			if(par1ItemStack.getDamage() >= 1)
				par1ItemStack.setDamage(par1ItemStack.getDamage() - 1);
	}

}
