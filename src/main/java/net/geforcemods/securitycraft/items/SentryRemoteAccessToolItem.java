package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.SentryPositions;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

public class SentryRemoteAccessToolItem extends Item {
	public SentryRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide) {
			updateTagWithNames(stack, level);
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(DataType.SENTRY_REMOTE_ACCESS_TOOL));
		}

		return InteractionResultHolder.consume(stack);
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

			SentryPositions positions = stack.get(SCContent.SENTRY_POSITIONS);

			if (positions != null && positions.size() < SentryPositions.MAX_SENTRIES) {
				GlobalPos globalPos = new GlobalPos(level.dimension(), pos);

				if (positions.remove(stack, globalPos))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.unbound", sentryPos), ChatFormatting.RED);
				else if (positions.add(stack, globalPos, SentryPositions.MAX_SENTRIES, sentry.hasCustomName() ? sentry.getCustomName().getString() : ""))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.bound", sentryPos), ChatFormatting.GREEN);
				else {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.noSlots"), ChatFormatting.RED);
					return InteractionResult.FAIL;
				}

				return InteractionResult.SUCCESS;
			}
		}
		else if (!level.isClientSide) {
			updateTagWithNames(stack, level);
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(DataType.SENTRY_REMOTE_ACCESS_TOOL));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
		SentryPositions positions = stack.get(SCContent.SENTRY_POSITIONS);

		if (positions != null && !positions.isEmpty()) {
			List<SentryPositions.Entry> sortedEntries = positions.filledOrderedList(SentryPositions.MAX_SENTRIES);

			for (SentryPositions.Entry entry : sortedEntries) {
				if (entry == null)
					tooltip.add(Component.literal(ChatFormatting.GRAY + "---"));
				else {
					BlockPos pos = entry.globalPos().pos();
					String nameToShow = null;

					if (!entry.name().isEmpty())
						nameToShow = entry.name();
					else {
						List<Sentry> sentries = Minecraft.getInstance().player.level().getEntitiesOfClass(Sentry.class, new AABB(pos));

						if (!sentries.isEmpty() && sentries.get(0).hasCustomName())
							nameToShow = sentries.get(0).getCustomName().getString();
						else
							nameToShow = Utils.localize("tooltip.securitycraft:sentry", entry.index()).getString();
					}

					tooltip.add(Component.literal(ChatFormatting.GRAY + nameToShow + ": " + Utils.getFormattedCoordinates(pos).getString()));
				}
			}
		}
	}

	private void updateTagWithNames(ItemStack stack, Level level) {
		SentryPositions positions = stack.get(SCContent.SENTRY_POSITIONS);

		if (positions != null && !positions.isEmpty()) {
			List<SentryPositions.Entry> newEntries = new ArrayList<>(positions.positions());
			boolean changed = false;

			for (int i = 0; i < newEntries.size(); i++) {
				SentryPositions.Entry entry = newEntries.get(i);
				GlobalPos globalPos = entry.globalPos();

				if (level.dimension().equals(globalPos.dimension()) && level.isLoaded(globalPos.pos())) {
					List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(globalPos.pos()));

					if (!sentries.isEmpty()) {
						Sentry sentry = sentries.get(0);

						if (sentry.hasCustomName()) {
							newEntries.set(i, new SentryPositions.Entry(entry.index(), globalPos, sentry.getCustomName().getString()));
							changed = true;
							continue;
						}
					}
				}
				else
					continue;

				newEntries.set(i, new SentryPositions.Entry(entry.index(), globalPos, ""));
				changed = true;
			}

			if (changed)
				stack.set(SCContent.SENTRY_POSITIONS, new SentryPositions(newEntries));
		}
	}
}
