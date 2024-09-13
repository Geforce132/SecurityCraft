package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedScaffoldingBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ScaffoldingBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedScaffoldingBlockItem extends ScaffoldingBlockItem {
	public ReinforcedScaffoldingBlockItem(Item.Properties properties) {
		super(SCContent.REINFORCED_SCAFFOLDING.get(), properties);
	}

	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext ctx) {
		BlockPos pos = ctx.getClickedPos();
		Level level = ctx.getLevel();
		BlockState state = level.getBlockState(pos);
		Block block = getBlock();

		if (!state.is(block))
			return ReinforcedScaffoldingBlock.getDistance(level, pos) == 7 ? null : ctx;
		else {
			Player player = ctx.getPlayer();

			if (level.getBlockEntity(pos) instanceof IOwnable scaffolding && !scaffolding.isOwnedBy(player))
				return ctx;

			Direction direction;
			int distance = 0;
			int maxBuildHeight = level.getMaxBuildHeight();
			BlockPos.MutableBlockPos mutable;

			if (ctx.isSecondaryUseActive())
				direction = ctx.isInside() ? ctx.getClickedFace().getOpposite() : ctx.getClickedFace();
			else
				direction = ctx.getClickedFace() == Direction.UP ? ctx.getHorizontalDirection() : Direction.UP;

			mutable = pos.mutable().move(direction);

			while (distance < 7) {
				if (!level.isClientSide && !level.isInWorldBounds(mutable)) {
					if (player instanceof ServerPlayer serverPlayer && mutable.getY() >= maxBuildHeight)
						serverPlayer.sendMessage(new TranslatableComponent("build.tooHigh", maxBuildHeight - 1).withStyle(ChatFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);

					break;
				}

				state = level.getBlockState(mutable);

				if (!state.is(block)) {
					if (state.canBeReplaced(ctx))
						return BlockPlaceContext.at(ctx, mutable, direction);

					break;
				}

				mutable.move(direction);

				if (direction.getAxis().isHorizontal())
					distance++;
			}

			return null;
		}
	}
}
