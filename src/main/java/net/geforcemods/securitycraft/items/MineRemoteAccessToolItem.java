package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.components.IndexedPositions;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class MineRemoteAccessToolItem extends Item {
	public MineRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide)
			ClientHandler.displayMRATScreen(player.getItemInHand(hand));

		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();

		if (level.getBlockState(pos).getBlock() instanceof IExplosive) {
			Player player = ctx.getPlayer();

			if (level.getBlockEntity(pos) instanceof IOwnable ownable && !ownable.isOwnedBy(player)) {
				if (level.isClientSide)
					ClientHandler.displayMRATScreen(stack);

				return InteractionResult.SUCCESS;
			}

			IndexedPositions positions = stack.get(SCContent.INDEXED_POSITIONS);

			if (positions != null && positions.size() < IndexedPositions.MAX_MINES) {
				GlobalPos globalPos = new GlobalPos(level.dimension(), pos);

				if (positions.remove(stack, globalPos))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.MINE_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.unbound", Utils.getFormattedCoordinates(pos)), ChatFormatting.RED);
				else if (positions.add(stack, globalPos, IndexedPositions.MAX_MINES))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.MINE_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.bound", Utils.getFormattedCoordinates(pos)), ChatFormatting.GREEN);
				else {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.MINE_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.noSlots"), ChatFormatting.RED);
					return InteractionResult.FAIL;
				}

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
		IndexedPositions positions = stack.get(SCContent.INDEXED_POSITIONS);

		if (positions != null && !positions.isEmpty()) {
			List<IndexedPositions.Entry> sortedEntries = positions.filledOrderedList(IndexedPositions.MAX_MINES);

			for (IndexedPositions.Entry entry : sortedEntries) {
				if (entry == null)
					list.add(Component.literal(ChatFormatting.GRAY + "---"));
				else
					list.add(Utils.localize("tooltip.securitycraft:mine", entry.index(), Utils.getFormattedCoordinates(entry.globalPos().pos())).setStyle(Utils.GRAY_STYLE));
			}
		}
	}
}