package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.network.client.OpenSRATGui;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class SentryRemoteAccessToolItem extends Item {

	public SentryRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);

		if (!world.isClientSide)
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new OpenSRATGui((player.getServer().getPlayerList().getViewDistance() - 1) * 16));

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z);
	}

	public InteractionResult onItemUse(Player player, Level world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		List<SentryEntity> sentries = world.getEntitiesOfClass(SentryEntity.class, new AABB(pos));

		if(!sentries.isEmpty()) {
			SentryEntity sentry = sentries.get(0);
			BlockPos pos2 = sentry.blockPosition();

			if(!isSentryAdded(stack, pos2)){
				int availSlot = getNextAvaliableSlot(stack);

				if(availSlot == 0){
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.noSlots"), ChatFormatting.RED);
					return InteractionResult.FAIL;
				}

				if(!sentry.getOwner().isOwner(player)){
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.cantBind"), ChatFormatting.RED);
					return InteractionResult.FAIL;
				}

				if(stack.getTag() == null)
					stack.setTag(new CompoundTag());

				stack.getTag().putIntArray(("sentry" + availSlot), BlockUtils.posToIntArray(pos2));

				if (!world.isClientSide)
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new UpdateNBTTagOnClient(stack));

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.bound", pos2), ChatFormatting.GREEN);
			}else{
				removeTagFromItemAndUpdate(stack, pos2, player);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getDescriptionId()), Utils.localize("messages.securitycraft:srat.unbound", pos2), ChatFormatting.RED);
			}

			return InteractionResult.SUCCESS;
		}
		else if (!world.isClientSide)
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new OpenSRATGui((player.getServer().getPlayerList().getViewDistance() - 1) * 16));

		return InteractionResult.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 12; i++)
			if(stack.getTag().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					tooltip.add(new TextComponent(ChatFormatting.GRAY + "---"));
					continue;
				}
				else
				{
					BlockPos pos = new BlockPos(coords[0], coords[1], coords[2]);
					List<SentryEntity> sentries = Minecraft.getInstance().player.level.getEntitiesOfClass(SentryEntity.class, new AABB(pos));
					String nameToShow;

					if(!sentries.isEmpty() && sentries.get(0).hasCustomName())
						nameToShow = sentries.get(0).getCustomName().getString();
					else
						nameToShow = Utils.localize("tooltip.securitycraft:sentry").getString() + " " + i;

					tooltip.add(new TextComponent(ChatFormatting.GRAY + nameToShow + ": " + Utils.getFormattedCoordinates(pos).getString()));
				}
			}
			else
				tooltip.add(new TextComponent(ChatFormatting.GRAY + "---"));
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, Player player) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 12; i++)
			if(stack.getTag().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTag().putIntArray("sentry" + i, new int[]{0, 0, 0});
					if (!player.level.isClientSide)
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new UpdateNBTTagOnClient(stack));

					return;
				}
			}
			else
				continue;

		return;
	}

	private boolean isSentryAdded(ItemStack stack, BlockPos pos) {
		if(stack.getTag() == null)
			return false;

		for(int i = 1; i <= 12; i++)
			if(stack.getTag().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
					return true;
			}
			else
				continue;

		return false;
	}

	private int getNextAvaliableSlot(ItemStack stack){
		for(int i = 1; i <= 12; i++)
		{
			if(stack.getTag() == null)
				return 1;

			int[] pos = stack.getTag().getIntArray("sentry" + i);

			if(pos.length == 0 || (pos[0] == 0 && pos[1] == 0 && pos[2] == 0))
				return i;
		}

		return 0;
	}
}
