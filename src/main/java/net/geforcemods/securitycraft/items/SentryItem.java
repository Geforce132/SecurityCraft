package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class SentryItem extends Item {
	public SentryItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		boolean replacesTargetedBlock = level.getBlockState(pos).getMaterial().isReplaceable();

		if (!replacesTargetedBlock) {
			pos = pos.relative(ctx.getClickedFace()); //if the block is not replaceable, place sentry next to targeted block

			BlockState stateAtPlacePos = level.getBlockState(pos);

			if (!stateAtPlacePos.isAir() && !(stateAtPlacePos.getBlock() instanceof LiquidBlock))
				return InteractionResult.PASS;
		}

		Player player = ctx.getPlayer();
		BlockPos downPos = pos.below();

		if (level.isEmptyBlock(downPos) || level.noCollision(new AABB(downPos))) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY.get().getDescriptionId()), Utils.localize("messages.securitycraft:sentry.needsBlockBelow"), ChatFormatting.DARK_RED);
			return InteractionResult.FAIL;
		}

		ItemStack stack = ctx.getItemInHand();
		Sentry entity = SCContent.SENTRY_ENTITY.get().create(level);

		entity.setPos(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
		entity.setUpSentry(player);

		if (stack.hasCustomHoverName())
			entity.setCustomName(stack.getHoverName());

		if (replacesTargetedBlock)
			level.removeBlock(pos, false);

		level.addFreshEntity(entity);
		entity.gameEvent(GameEvent.ENTITY_PLACE, player);
		player.displayClientMessage(Utils.localize(SentryMode.CAMOUFLAGE_HP.getModeKey()).append(Utils.localize(SentryMode.CAMOUFLAGE_HP.getDescriptionKey())), true);

		if (!player.isCreative())
			stack.shrink(1);

		return InteractionResult.SUCCESS;
	}
}
