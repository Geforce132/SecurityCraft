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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

public class MineRemoteAccessToolItem extends Item {

	private static final Style GRAY_STYLE = Style.EMPTY.setFormatting(TextFormatting.GRAY);

	public MineRemoteAccessToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand){
		SecurityCraft.proxy.displayMRATGui(player.getHeldItem(hand));
		return ActionResult.resultConsume(player.getHeldItem(hand));
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx)
	{
		return onItemUseFirst(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), stack, ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z);
	}

	public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		if(world.getBlockState(pos).getBlock() instanceof IExplosive){
			if(!isMineAdded(stack, pos)){
				int availSlot = getNextAvaliableSlot(stack);

				if(availSlot == 0){
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_MINE.get().getTranslationKey()), Utils.localize("messages.securitycraft:mrat.noSlots"), TextFormatting.RED);
					return ActionResultType.FAIL;
				}

				if(world.getTileEntity(pos) instanceof IOwnable && !((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player))
				{
					SecurityCraft.proxy.displayMRATGui(stack);
					return ActionResultType.SUCCESS;
				}

				if(stack.getTag() == null)
					stack.setTag(new CompoundNBT());

				stack.getTag().putIntArray(("mine" + availSlot), BlockUtils.posToIntArray(pos));

				if (!world.isRemote)
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack));

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_MINE.get().getTranslationKey()), Utils.localize("messages.securitycraft:mrat.bound", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
				return ActionResultType.SUCCESS;
			}else{
				removeTagFromItemAndUpdate(stack, pos, player);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.REMOTE_ACCESS_MINE.get().getTranslationKey()), Utils.localize("messages.securitycraft:mrat.unbound", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					list.add(new StringTextComponent(TextFormatting.GRAY + "---"));
					continue;
				}
				else
					list.add(Utils.localize("tooltip.securitycraft:mine").appendSibling(new StringTextComponent(" " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2])).setStyle(GRAY_STYLE));
			}
			else
				list.add(new StringTextComponent(TextFormatting.GRAY + "---"));
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTag().putIntArray("mine" + i, new int[]{0, 0, 0});
					if (!player.world.isRemote)
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack));
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