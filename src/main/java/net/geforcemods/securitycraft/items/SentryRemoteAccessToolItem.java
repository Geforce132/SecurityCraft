package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.network.client.OpenSRATGui;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
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
import net.minecraft.util.Direction;
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

	public SentryRemoteAccessToolItem() {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote)
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new OpenSRATGui((player.getServer().getPlayerList().getViewDistance() - 1) * 16));

		return ActionResult.resultPass(stack);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		List<SentryEntity> sentries = world.getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(pos));

		if(!world.isRemote){
			if(!sentries.isEmpty()) {
				BlockPos pos2 = sentries.get(0).getPosition();

				if(!isSentryAdded(stack, world, pos2)){
					int availSlot = getNextAvaliableSlot(stack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:srat.noSlots"), TextFormatting.RED);
						return ActionResultType.FAIL;
					}

					if(world.getTileEntity(pos2) instanceof IOwnable && !((IOwnable) world.getTileEntity(pos2)).getOwner().isOwner(player)){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:srat.cantBind"), TextFormatting.RED);
						return ActionResultType.FAIL;
					}

					if(stack.getTag() == null)
						stack.setTag(new CompoundNBT());

					stack.getTag().putIntArray(("sentry" + availSlot), BlockUtils.fromPos(pos2));
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack));
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:srat.bound").replace("#", Utils.getFormattedCoordinates(pos2)), TextFormatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(stack, pos2, player);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:srat.unbound").replace("#", Utils.getFormattedCoordinates(pos2)), TextFormatting.RED);
				}
			}
			else
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new OpenSRATGui((player.getServer().getPlayerList().getViewDistance() - 1) * 16));
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 12; i++)
			if(stack.getTag().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					tooltip.add(new StringTextComponent(TextFormatting.GRAY + "---"));
					continue;
				}
				else
				{
					BlockPos pos = new BlockPos(coords[0], coords[1], coords[2]);
					List<SentryEntity> sentries = Minecraft.getInstance().player.world.getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(pos));
					String nameToShow;

					if(!sentries.isEmpty() && sentries.get(0).hasCustomName())
						nameToShow = sentries.get(0).getCustomName().getFormattedText();
					else
						nameToShow = ClientUtils.localize("tooltip.securitycraft:sentry") + " " + i;

					tooltip.add(new StringTextComponent(TextFormatting.GRAY + nameToShow + ": " + Utils.getFormattedCoordinates(pos)));
				}
			}
			else
				tooltip.add(new StringTextComponent(TextFormatting.GRAY + "---"));
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 12; i++)
			if(stack.getTag().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTag().putIntArray("sentry" + i, new int[]{0, 0, 0});
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack));
					return;
				}
			}
			else
				continue;

		return;
	}

	private boolean isSentryAdded(ItemStack stack, World world, BlockPos pos) {
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
			if(stack.getTag() == null)
				return 1;
			else if(stack.getTag().getIntArray("sentry" + i).length == 0 || (stack.getTag().getIntArray("sentry" + i)[0] == 0 && stack.getTag().getIntArray("sentry" + i)[1] == 0 && stack.getTag().getIntArray("sentry" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}
}
