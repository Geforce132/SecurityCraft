package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSentry extends Item
{
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			switch(side)
			{
				case 0: y--; break;
				case 1: y++; break;
				case 2: z--; break;
				case 3: z++; break;
				case 4: x--; break;
				case 5: x++; break;
			}

			if(world.isAirBlock(x, y - 1, z))
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:sentry.name"), StatCollector.translateToLocal("messages.securitycraft:sentry.needsBlockBelow"), EnumChatFormatting.DARK_RED);
				return true;
			}

			if(world.isAirBlock(x - 1, y, z))
				return fail(player);
			else if(world.isAirBlock(x + 1, y, z))
				return fail(player);
			else if(world.isAirBlock(x, y, z - 1))
				return fail(player);
			else if(world.isAirBlock(x, y, z + 1))
				return fail(player);

			Entity entity = new EntitySentry(world, player);

			entity.setPosition(x + 0.5F, y, z + 0.5F);
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

	private boolean fail(EntityPlayer player)
	{
		PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:sentry.name"), StatCollector.translateToLocal("messages.securitycraft:sentry.needsBlocksAround"), EnumChatFormatting.DARK_RED);
		return true;
	}
}
