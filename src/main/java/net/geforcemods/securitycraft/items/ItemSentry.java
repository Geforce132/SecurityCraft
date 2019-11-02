package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSentry extends Item
{
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			pos = pos.offset(facing); //get sentry position

			if(world.isAirBlock(pos.down()))
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:sentry.name"), StatCollector.translateToLocal("messages.securitycraft:sentry.needsBlockBelow"), EnumChatFormatting.DARK_RED);
				return true;
			}

			for(EnumFacing horizontal : EnumFacing.HORIZONTALS)
			{
				if(world.isAirBlock(pos.offset(horizontal)))
				{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:sentry.name"), StatCollector.translateToLocal("messages.securitycraft:sentry.needsBlocksAround"), EnumChatFormatting.DARK_RED);
					return true;
				}
			}

			Entity entity = new EntitySentry(world, player);

			entity.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
			world.spawnEntityInWorld(entity);

			if(!player.capabilities.isCreativeMode)
			{
				player.getHeldItem().stackSize--;

				if(player.getHeldItem().stackSize <= 0)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}
		}

		return true;
	}
}
