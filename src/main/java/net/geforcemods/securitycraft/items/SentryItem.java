package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SentryItem extends Item
{
	public SentryItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z);
	}

	public InteractionResult onItemUse(Player player, Level world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ)
	{
		boolean replacesTargetedBlock = world.getBlockState(pos).getMaterial().isReplaceable();

		if (!replacesTargetedBlock) {
			pos = pos.relative(facing); //if the block is not replaceable, place sentry next to targeted block
		}

		if(!world.isEmptyBlock(pos) && !replacesTargetedBlock)
			return InteractionResult.PASS;
		else
		{
			BlockPos downPos = pos.below();

			if(world.isEmptyBlock(downPos) || world.noCollision(new AABB(downPos)))
			{
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY.get().getDescriptionId()), Utils.localize("messages.securitycraft:sentry.needsBlockBelow"), ChatFormatting.DARK_RED);
				return InteractionResult.FAIL;
			}
		}

		SentryEntity entity = SCContent.eTypeSentry.create(world);

		entity.setupSentry(player);
		entity.setPos(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);

		if (stack.hasCustomHoverName())
			entity.setCustomName(stack.getHoverName());

		if (replacesTargetedBlock) {
			world.removeBlock(pos, false);
		}

		world.addFreshEntity(entity);
		player.displayClientMessage(Utils.localize(SentryMode.CAMOUFLAGE_HP.getModeKey()).append(Utils.localize(SentryMode.CAMOUFLAGE_HP.getDescriptionKey())), true);

		if(!player.isCreative())
			stack.shrink(1);

		return InteractionResult.SUCCESS;
	}
}
