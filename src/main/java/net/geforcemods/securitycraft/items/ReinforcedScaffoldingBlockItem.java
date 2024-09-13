package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedScaffoldingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ScaffoldingItem;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ReinforcedScaffoldingBlockItem extends ScaffoldingItem {
	public ReinforcedScaffoldingBlockItem(Item.Properties properties) {
		super(SCContent.REINFORCED_SCAFFOLDING.get(), properties);
	}

	@Override
	public BlockItemUseContext updatePlacementContext(BlockItemUseContext ctx) {
		BlockPos pos = ctx.getClickedPos();
		World level = ctx.getLevel();
		BlockState state = level.getBlockState(pos);
		Block block = getBlock();

		if (!state.is(block))
			return ReinforcedScaffoldingBlock.getDistance(level, pos) == 7 ? null : ctx;
		else {
			PlayerEntity player = ctx.getPlayer();
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IOwnable && !((IOwnable) te).isOwnedBy(player))
				return ctx;

			Direction direction;
			int distance = 0;
			int maxBuildHeight = level.getMaxBuildHeight();
			BlockPos.Mutable mutable;

			if (ctx.isSecondaryUseActive())
				direction = ctx.isInside() ? ctx.getClickedFace().getOpposite() : ctx.getClickedFace();
			else
				direction = ctx.getClickedFace() == Direction.UP ? ctx.getHorizontalDirection() : Direction.UP;

			mutable = pos.mutable().move(direction);

			while (distance < 7) {
				if (!level.isClientSide && !World.isInWorldBounds(mutable)) {
					if (player instanceof ServerPlayerEntity && mutable.getY() >= maxBuildHeight)
						((ServerPlayerEntity) player).connection.send(new SChatPacket(new TranslationTextComponent("build.tooHigh", maxBuildHeight).withStyle(TextFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID));

					break;
				}

				state = level.getBlockState(mutable);

				if (!state.is(block)) {
					if (state.canBeReplaced(ctx))
						return BlockItemUseContext.at(ctx, mutable, direction);

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
