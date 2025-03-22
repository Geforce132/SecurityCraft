package net.geforcemods.securitycraft.items;

import java.util.List;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class MineRemoteAccessToolItem extends Item {
	public static final int MAX_MINES = 6;

	public MineRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide)
			ClientHandler.displayMRATScreen(player.getItemInHand(hand));

		return InteractionResult.CONSUME;
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

			GlobalPositions positions = stack.get(SCContent.BOUND_MINES);

			if (positions != null) {
				GlobalPos globalPos = new GlobalPos(level.dimension(), pos);

				if (positions.remove(SCContent.BOUND_MINES, stack, globalPos))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.MINE_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.unbound", Utils.getFormattedCoordinates(pos)), ChatFormatting.RED);
				else if (positions.add(SCContent.BOUND_MINES, stack, globalPos))
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
	public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		GlobalPositions positions = stack.get(SCContent.BOUND_MINES);

		if (positions != null && !positions.isEmpty()) {
			List<GlobalPos> globalPositions = positions.positions();

			for (int i = 0; i < globalPositions.size(); i++) {
				GlobalPos globalPos = globalPositions.get(i);

				if (globalPos == null)
					tooltipAdder.accept(Component.literal(ChatFormatting.GRAY + "---"));
				else
					tooltipAdder.accept(Utils.localize("tooltip.securitycraft:mine", i, Utils.getFormattedCoordinates(globalPos.pos())).setStyle(Utils.GRAY_STYLE));
			}
		}
	}
}