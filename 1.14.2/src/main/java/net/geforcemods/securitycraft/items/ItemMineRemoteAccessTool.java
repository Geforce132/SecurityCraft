package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class ItemMineRemoteAccessTool extends Item {

	public int listIndex = 0;

	public ItemMineRemoteAccessTool() {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		if(!world.isRemote && player instanceof EntityPlayerMP)
			NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(GuiHandler.MRAT));

		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ());
	}

	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ){

		if(!world.isRemote)
			if(BlockUtils.getBlock(world, pos) instanceof IExplosive){
				if(!isMineAdded(stack, world, pos)){
					int availSlot = getNextAvaliableSlot(stack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.remoteAccessMine.getTranslationKey()), ClientUtils.localize("messages.securitycraft:mrat.noSlots"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(world.getTileEntity(pos) instanceof IOwnable && !((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player)){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.remoteAccessMine.getTranslationKey()), ClientUtils.localize("messages.securitycraft:mrat.cantBind"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(stack.getTag() == null)
						stack.setTag(new NBTTagCompound());

					stack.getTag().putIntArray(("mine" + availSlot), new int[]{BlockUtils.fromPos(pos)[0], BlockUtils.fromPos(pos)[1], BlockUtils.fromPos(pos)[2]});
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (EntityPlayerMP)player), new UpdateNBTTagOnClient(stack));
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.remoteAccessMine.getTranslationKey()), ClientUtils.localize("messages.securitycraft:mrat.bound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(stack, pos, player);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.remoteAccessMine.getTranslationKey()), ClientUtils.localize("messages.securitycraft:mrat.unbound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
				}
			}
			else if(player instanceof EntityPlayerMP)
				NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(GuiHandler.MRAT));

		return EnumActionResult.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack par1ItemStack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if(par1ItemStack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTag().getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					list.add(new TextComponentString("---"));
					continue;
				}
				else
					list.add(new TextComponentString(ClientUtils.localize("tooltip.securitycraft:mine") + " " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2]));
			}
			else
				list.add(new TextComponentString("---"));
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, EntityPlayer player) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTag().putIntArray("mine" + i, new int[]{0, 0, 0});
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (EntityPlayerMP)player), new UpdateNBTTagOnClient(stack));
					return;
				}
			}
			else
				continue;


		return;
	}

	private boolean isMineAdded(ItemStack stack, World world, BlockPos pos) {
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
