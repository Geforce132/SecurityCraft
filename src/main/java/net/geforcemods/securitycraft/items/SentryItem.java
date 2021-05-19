package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SentryItem extends Item
{
	public SentryItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ)
	{
		boolean replacesTargetedBlock = world.getBlockState(pos).getMaterial().isReplaceable();

		if (!replacesTargetedBlock) {
			pos = pos.offset(facing); //if the block is not replaceable, place sentry next to targeted block
		}

		if(!world.isAirBlock(pos) && !replacesTargetedBlock)
			return ActionResultType.PASS;
		else
		{
			BlockPos downPos = pos.down();

			if(world.isAirBlock(downPos) || world.hasNoCollisions(new AxisAlignedBB(downPos)))
			{
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY.get().getTranslationKey()), Utils.localize("messages.securitycraft:sentry.needsBlockBelow"), TextFormatting.DARK_RED);
				return ActionResultType.FAIL;
			}
		}

		SentryEntity entity = SCContent.eTypeSentry.create(world);

		entity.setupSentry(player);
		entity.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);

		if (stack.hasDisplayName())
			entity.setCustomName(stack.getDisplayName());

		if (replacesTargetedBlock) {
			world.removeBlock(pos, false);
		}

		world.addEntity(entity);
		player.sendStatusMessage(Utils.localize(SentryMode.CAMOUFLAGE_HP.getModeKey()).appendSibling(Utils.localize(SentryMode.CAMOUFLAGE_HP.getDescriptionKey())), true);

		if(!player.isCreative())
			stack.shrink(1);

		return ActionResultType.SUCCESS;
	}
}
