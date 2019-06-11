package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemSentry extends Item
{
	public ItemSentry()
	{
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.func_221532_j().x, ctx.func_221532_j().y, ctx.func_221532_j().z);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ)
	{
		if(!world.isRemote)
		{
			pos = pos.offset(facing); //get sentry position

			if(world.isAirBlock(pos.down()))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.sentry.getTranslationKey()), ClientUtils.localize("messages.securitycraft:sentry.needsBlockBelow"), TextFormatting.DARK_RED);
				return ActionResultType.SUCCESS;
			}

			for(Direction horizontal : Direction.Plane.HORIZONTAL)
			{
				if(world.isAirBlock(pos.offset(horizontal)))
				{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.sentry.getTranslationKey()), ClientUtils.localize("messages.securitycraft:sentry.needsBlocksAround"), TextFormatting.DARK_RED);
					return ActionResultType.SUCCESS;
				}
			}

			EntitySentry entity = SCContent.eTypeSentry.create(world);

			entity.setupSentry(player);
			entity.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
			world.addEntity(entity);

			if(!player.isCreative())
				stack.shrink(1);
		}

		return ActionResultType.SUCCESS;
	}
}
