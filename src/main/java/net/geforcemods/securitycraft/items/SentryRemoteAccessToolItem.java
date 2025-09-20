package net.geforcemods.securitycraft.items;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

public class SentryRemoteAccessToolItem extends Item {
	public static final int MAX_SENTRIES = 12;
	public static final NamedPositions DEFAULT_NAMED_POSITIONS = NamedPositions.sized(MAX_SENTRIES);

	public SentryRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			updateComponentWithNames(stack, level);
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(DataType.SENTRY_REMOTE_ACCESS_TOOL));
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Player player = ctx.getPlayer();
		List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(pos));
		ItemStack stack = ctx.getItemInHand();

		if (!sentries.isEmpty()) {
			Sentry sentry = sentries.get(0);
			BlockPos sentryPos = sentry.blockPosition();

			if (!sentry.isOwnedBy(player)) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.cantBind"), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}

			NamedPositions positions = stack.get(SCContent.BOUND_SENTRIES);

			if (positions != null) {
				GlobalPos globalPos = new GlobalPos(level.dimension(), pos);

				if (positions.remove(SCContent.BOUND_SENTRIES, stack, globalPos))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.unbound", sentryPos), ChatFormatting.RED);
				else if (positions.add(SCContent.BOUND_SENTRIES, stack, globalPos, sentry.hasCustomName() ? Optional.of(sentry.getCustomName().getString()) : Optional.empty()))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.bound", sentryPos), ChatFormatting.GREEN);
				else {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.noSlots"), ChatFormatting.RED);
					return InteractionResult.FAIL;
				}

				return InteractionResult.SUCCESS;
			}
		}
		else if (!level.isClientSide()) {
			updateComponentWithNames(stack, level);
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(DataType.SENTRY_REMOTE_ACCESS_TOOL));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		DataComponentType<NamedPositions> type = SCContent.BOUND_SENTRIES.get();
		NamedPositions positions = stack.get(type);

		if (positions != null && display.shows(type) && !positions.isEmpty()) {
			List<NamedPositions.Entry> entries = positions.positions();

			for (int i = 0; i < SentryRemoteAccessToolItem.MAX_SENTRIES; i++) {
				NamedPositions.Entry entry = entries.get(i);

				if (entry == null)
					tooltipAdder.accept(Component.literal(ChatFormatting.GRAY + "---"));
				else {
					BlockPos pos = entry.globalPos().pos();
					String nameToShow = null;

					if (entry.name().isPresent())
						nameToShow = entry.name().get();
					else {
						List<Sentry> sentries = Minecraft.getInstance().player.level().getEntitiesOfClass(Sentry.class, new AABB(pos));

						if (!sentries.isEmpty() && sentries.get(0).hasCustomName())
							nameToShow = sentries.get(0).getCustomName().getString();
						else
							nameToShow = Utils.localize("tooltip.securitycraft:sentry", i).getString();
					}

					tooltipAdder.accept(Component.literal(ChatFormatting.GRAY + nameToShow + ": " + Utils.getFormattedCoordinates(pos).getString()));
				}
			}
		}
	}

	public static void updateComponentWithNames(ItemStack stack, Level level) {
		NamedPositions.updateComponentWithNames(SCContent.BOUND_SENTRIES, stack, entry -> {
			GlobalPos globalPos = entry.globalPos();

			if (level.dimension().equals(globalPos.dimension()) && level.isLoaded(globalPos.pos())) {
				List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(globalPos.pos()));

				if (!sentries.isEmpty())
					return sentries.get(0);
			}

			return null;
		});
	}
}
