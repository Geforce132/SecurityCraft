package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraftforge.network.PacketDistributor;

public class SentryRemoteAccessToolItem extends Item {
	public SentryRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide) {
			updateTagWithNames(stack, level);
			SecurityCraft.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.SENTRY_REMOTE_ACCESS_TOOL, stack.getOrCreateTag()));
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

			if (!isSentryAdded(stack, sentryPos)) {
				int nextAvailableSlot = getNextAvailableSlot(stack);

				if (nextAvailableSlot == 0) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.noSlots"), ChatFormatting.RED);
					return InteractionResult.FAIL;
				}

				if (!sentry.isOwnedBy(player)) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.cantBind"), ChatFormatting.RED);
					return InteractionResult.FAIL;
				}

				stack.getOrCreateTag().putIntArray("sentry" + nextAvailableSlot, new int[] {
						sentryPos.getX(), sentryPos.getY(), sentryPos.getZ()
				});

				if (sentry.hasCustomName())
					stack.getTag().putString("sentry" + nextAvailableSlot + "_name", sentry.getCustomName().getString());

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.bound", sentryPos), ChatFormatting.GREEN);
			}
			else {
				removeSentry(stack, sentryPos);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.unbound", sentryPos), ChatFormatting.RED);
			}

			return InteractionResult.SUCCESS;
		}
		else if (!level.isClientSide) {
			updateTagWithNames(stack, level);
			SecurityCraft.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.SENTRY_REMOTE_ACCESS_TOOL, stack.getOrCreateTag()));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			int[] coords = stack.getTag().getIntArray("sentry" + i);

			if (coords.length != 3)
				tooltip.add(new TextComponent(ChatFormatting.GRAY + "---"));
			else {
				BlockPos pos = new BlockPos(coords[0], coords[1], coords[2]);
				String nameKey = "sentry" + i + "_name";
				String nameToShow = null;

				if (stack.getTag().contains(nameKey))
					nameToShow = stack.getTag().getString(nameKey);
				else {
					List<Sentry> sentries = Minecraft.getInstance().player.level.getEntitiesOfClass(Sentry.class, new AABB(pos));

					if (!sentries.isEmpty() && sentries.get(0).hasCustomName())
						nameToShow = sentries.get(0).getCustomName().getString();
					else
						nameToShow = Utils.localize("tooltip.securitycraft:sentry", i).getString();
				}

				tooltip.add(new TextComponent(ChatFormatting.GRAY + nameToShow + ": " + Utils.getFormattedCoordinates(pos).getString()));
			}
		}
	}

	private void updateTagWithNames(ItemStack stack, Level level) {
		if (!stack.hasTag())
			return;

		CompoundTag tag = stack.getTag();

		for (int i = 1; i <= 12; i++) {
			int[] coords = tag.getIntArray("sentry" + i);
			String nameKey = "sentry" + i + "_name";

			if (coords.length == 3) {
				BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);

				if (level.isLoaded(sentryPos)) {
					List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(sentryPos));

					if (!sentries.isEmpty()) {
						Sentry sentry = sentries.get(0);

						if (sentry.hasCustomName()) {
							tag.putString(nameKey, sentry.getCustomName().getString());
							continue;
						}
					}
				}
				else
					continue;
			}

			tag.remove(nameKey);
		}
	}

	private void removeSentry(ItemStack stack, BlockPos pos) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			int[] coords = stack.getTag().getIntArray("sentry" + i);

			if (coords.length == 3 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()) {
				stack.getTag().remove("sentry" + i);
				return;
			}
		}
	}

	public static boolean hasSentryAdded(CompoundTag tag) {
		if (tag == null)
			return false;

		for (int i = 1; i <= 12; i++) {
			if (tag.contains("sentry" + i))
				return true;
		}

		return false;
	}

	public static boolean isSentryAdded(ItemStack stack, BlockPos pos) {
		if (stack.getTag() == null)
			return false;

		for (int i = 1; i <= 12; i++) {
			int[] coords = stack.getTag().getIntArray("sentry" + i);

			if (coords.length == 3 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return true;
		}

		return false;
	}

	public static int getNextAvailableSlot(ItemStack stack) {
		if (stack.getTag() == null)
			return 1;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTag().getIntArray("sentry" + i).length != 3)
				return i;
		}

		return 0;
	}
}
