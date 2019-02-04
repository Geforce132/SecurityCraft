package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemSentry extends Item
{
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			pos = pos.offset(facing); //get sentry position

			if(world.isAirBlock(pos.down()))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:sentry.name"), ClientUtils.localize("messages.securitycraft:sentry.needsBlockBelow"), TextFormatting.DARK_RED);
				return EnumActionResult.SUCCESS;
			}

			for(EnumFacing horizontal : EnumFacing.HORIZONTALS)
			{
				if(world.isAirBlock(pos.offset(horizontal)))
				{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:sentry.name"), ClientUtils.localize("messages.securitycraft:sentry.needsBlocksAround"), TextFormatting.DARK_RED);
					return EnumActionResult.SUCCESS;
				}
			}

			Entity entity = new EntitySentry(world, player);

			entity.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
			world.spawnEntity(entity);

			if(!player.isCreative())
			{
				player.getHeldItem(hand).stackSize--;

				if(player.getHeldItem(hand).stackSize <= 0)
					player.setHeldItem(hand, null);
			}
		}

		return EnumActionResult.SUCCESS;
	}
}
