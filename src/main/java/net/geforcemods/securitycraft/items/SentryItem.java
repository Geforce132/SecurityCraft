package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
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

public class SentryItem extends Item {
	public SentryItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		boolean replacesTargetedBlock = level.getBlockState(pos).getMaterial().isReplaceable();

		if (!replacesTargetedBlock) {
			Direction facing = ctx.getClickedFace();
			BlockState stateAtPlacePos;

			pos = pos.relative(facing); //if the block is not replaceable, place sentry next to targeted block
			stateAtPlacePos = level.getBlockState(pos);

			if (!stateAtPlacePos.isAir() && !(stateAtPlacePos.getBlock() instanceof FlowingFluidBlock))
				return ActionResultType.PASS;
		}

		BlockPos downPos = pos.below();
		PlayerEntity player = ctx.getPlayer();

		if (level.isEmptyBlock(downPos) || level.noCollision(new AxisAlignedBB(downPos))) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY.get().getDescriptionId()), Utils.localize("messages.securitycraft:sentry.needsBlockBelow"), TextFormatting.DARK_RED);
			return ActionResultType.FAIL;
		}

		Sentry entity = SCContent.SENTRY_ENTITY.get().create(level);
		ItemStack stack = ctx.getItemInHand();

		entity.setPos(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		entity.setUpSentry(player);

		if (stack.hasCustomHoverName())
			entity.setCustomName(stack.getHoverName());

		if (replacesTargetedBlock)
			level.removeBlock(pos, false);

		level.addFreshEntity(entity);
		player.displayClientMessage(Utils.localize(SentryMode.CAMOUFLAGE_HP.getModeKey()).append(Utils.localize(SentryMode.CAMOUFLAGE_HP.getDescriptionKey())), true);

		if (!player.isCreative())
			stack.shrink(1);

		return ActionResultType.SUCCESS;
	}
}
