package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class MineRemoteAccessToolItem extends Item {

	private static final Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);

	public MineRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		SecurityCraft.proxy.displayMRATGui(player.getItemInHand(hand));
		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx)
	{
		return onItemUseFirst(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), stack, ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z);
	}

	public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		if(world.getBlockState(pos).getBlock() instanceof IExplosive){
			if(!isMineAdded(stack, pos)){
				int availSlot = getNextAvaliableSlot(stack);

				if(availSlot == 0){
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_MINE.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.noSlots"), ChatFormatting.RED);
					return InteractionResult.FAIL;
				}

				if(world.getBlockEntity(pos) instanceof IOwnable && !((IOwnable) world.getBlockEntity(pos)).getOwner().isOwner(player))
				{
					SecurityCraft.proxy.displayMRATGui(stack);
					return InteractionResult.SUCCESS;
				}

				if(stack.getTag() == null)
					stack.setTag(new CompoundTag());

				stack.getTag().putIntArray(("mine" + availSlot), BlockUtils.posToIntArray(pos));

				if (!world.isClientSide)
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new UpdateNBTTagOnClient(stack));

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_MINE.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.bound", Utils.getFormattedCoordinates(pos)), ChatFormatting.GREEN);
				return InteractionResult.SUCCESS;
			}else{
				removeTagFromItemAndUpdate(stack, pos, player);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_MINE.get().getDescriptionId()), Utils.localize("messages.securitycraft:mrat.unbound", Utils.getFormattedCoordinates(pos)), ChatFormatting.RED);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flag) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					list.add(new TextComponent(ChatFormatting.GRAY + "---"));
					continue;
				}
				else
					list.add(Utils.localize("tooltip.securitycraft:mine").append(new TextComponent(" " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2])).setStyle(GRAY_STYLE));
			}
			else
				list.add(new TextComponent(ChatFormatting.GRAY + "---"));
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, Player player) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTag().putIntArray("mine" + i, new int[]{0, 0, 0});
					if (!player.level.isClientSide)
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new UpdateNBTTagOnClient(stack));
					return;
				}
			}
			else
				continue;

		return;
	}

	private boolean isMineAdded(ItemStack stack, BlockPos pos) {
		if(stack.getTag() == null)
			return false;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
					return true;
			}
			else
				continue;

		return false;
	}

	private int getNextAvaliableSlot(ItemStack stack){
		for(int i = 1; i <= 6; i++)
			if(stack.getTag() == null)
				return 1;
			else if(stack.getTag().getIntArray("mine" + i).length == 0 || (stack.getTag().getIntArray("mine" + i)[0] == 0 && stack.getTag().getIntArray("mine" + i)[1] == 0 && stack.getTag().getIntArray("mine" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}
}