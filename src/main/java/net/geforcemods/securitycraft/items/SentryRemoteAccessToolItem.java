package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

public class SentryRemoteAccessToolItem extends Item {
	public SentryRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide) {
			updateTagWithNames(stack, level);
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.SENTRY_REMOTE_ACCESS_TOOL, stack.getOrCreateTag()));
		}

		return ActionResult.consume(stack);
	}

	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		PlayerEntity player = ctx.getPlayer();
		List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AxisAlignedBB(pos));
		ItemStack stack = ctx.getItemInHand();

		if (!sentries.isEmpty()) {
			Sentry sentry = sentries.get(0);
			BlockPos sentryPos = sentry.blockPosition();

			if (!isSentryAdded(stack, sentryPos)) {
				int nextAvailableSlot = getNextAvaliableSlot(stack);

				if (nextAvailableSlot == 0) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.noSlots"), TextFormatting.RED);
					return ActionResultType.FAIL;
				}

				if (!sentry.isOwnedBy(player)) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.cantBind"), TextFormatting.RED);
					return ActionResultType.FAIL;
				}

				stack.getOrCreateTag().putIntArray("sentry" + nextAvailableSlot, new int[] {
						sentryPos.getX(), sentryPos.getY(), sentryPos.getZ()
				});

				if (sentry.hasCustomName())
					stack.getTag().putString("sentry" + nextAvailableSlot + "_name", sentry.getCustomName().getString());

				if (!level.isClientSide)
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new UpdateNBTTagOnClient(stack));

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.bound", sentryPos), TextFormatting.GREEN);
			}
			else {
				removeTagFromItemAndUpdate(stack, sentryPos, player);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.unbound", sentryPos), TextFormatting.RED);
			}

			return ActionResultType.SUCCESS;
		}
		else if (!level.isClientSide) {
			updateTagWithNames(stack, level);
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.SENTRY_REMOTE_ACCESS_TOOL, stack.getOrCreateTag()));
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTag().getIntArray("sentry" + i).length > 0) {
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
					tooltip.add(new StringTextComponent(TextFormatting.GRAY + "---"));
				else {
					BlockPos pos = new BlockPos(coords[0], coords[1], coords[2]);
					String nameKey = "sentry" + i + "_name";
					String nameToShow = null;

					if (stack.getTag().contains(nameKey))
						nameToShow = stack.getTag().getString(nameKey);
					else {
						List<Sentry> sentries = Minecraft.getInstance().player.level.getEntitiesOfClass(Sentry.class, new AxisAlignedBB(pos));

						if (!sentries.isEmpty() && sentries.get(0).hasCustomName())
							nameToShow = sentries.get(0).getCustomName().getString();
						else
							nameToShow = Utils.localize("tooltip.securitycraft:sentry", i).getString();
					}

					tooltip.add(new StringTextComponent(TextFormatting.GRAY + nameToShow + ": " + Utils.getFormattedCoordinates(pos).getString()));
				}
			}
			else
				tooltip.add(new StringTextComponent(TextFormatting.GRAY + "---"));
		}
	}

	private void updateTagWithNames(ItemStack stack, World level) {
		if (!stack.hasTag())
			return;

		CompoundNBT tag = stack.getTag();

		for (int i = 1; i <= 12; i++) {
			int[] coords = tag.getIntArray("sentry" + i);
			String nameKey = "sentry" + i + "_name";

			if (coords.length == 3 && !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);

				if (level.isLoaded(sentryPos)) {
					List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AxisAlignedBB(sentryPos));

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

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTag().getIntArray("sentry" + i).length > 0) {
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if (coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()) {
					stack.getTag().putIntArray("sentry" + i, new int[] {
							0, 0, 0
					});

					if (!player.level.isClientSide)
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new UpdateNBTTagOnClient(stack));

					return;
				}
			}
		}
	}

	public static boolean hasSentryAdded(CompoundNBT tag) {
		if (tag == null)
			return false;

		for (int i = 1; i <= 12; i++) {
			int[] coords = tag.getIntArray("sentry" + i);

			if (tag.getIntArray("sentry" + i).length > 0 && (coords[0] != 0 || coords[1] != 0 || coords[2] != 0))
				return true;
		}

		return false;
	}

	public static boolean isSentryAdded(ItemStack stack, BlockPos pos) {
		if (stack.getTag() == null)
			return false;

		for (int i = 1; i <= 12; i++) {
			int[] coords = stack.getTag().getIntArray("sentry" + i);

			if (stack.getTag().getIntArray("sentry" + i).length > 0 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return true;
		}

		return false;
	}

	public static int getNextAvaliableSlot(ItemStack stack) {
		if (stack.getTag() == null)
			return 1;

		for (int i = 1; i <= 12; i++) {
			int[] pos = stack.getTag().getIntArray("sentry" + i);

			if (pos.length == 0 || (pos[0] == 0 && pos[1] == 0 && pos[2] == 0))
				return i;
		}

		return 0;
	}
}
