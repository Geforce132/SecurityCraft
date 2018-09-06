package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMineRemoteAccessTool extends Item {

	public int listIndex = 0;

	public ItemMineRemoteAccessTool() {
		super();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote)
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		else{
			player.openGui(SecurityCraft.instance, GuiHandler.MRAT_MENU_ID, world, (int)player.posX, (int)player.posY, (int)player.posZ);
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = player.getHeldItem(hand);

		if(!world.isRemote)
			if(BlockUtils.getBlock(world, pos) instanceof IExplosive){
				if(!isMineAdded(stack, world, pos)){
					int availSlot = getNextAvaliableSlot(stack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:remoteAccessMine.name"), ClientUtils.localize("messages.securitycraft:mrat.noSlots"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(world.getTileEntity(pos) instanceof IOwnable && !((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player)){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:remoteAccessMine.name"), ClientUtils.localize("messages.securitycraft:mrat.cantBind"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(stack.getTagCompound() == null)
						stack.setTagCompound(new NBTTagCompound());

					stack.getTagCompound().setIntArray(("mine" + availSlot), new int[]{BlockUtils.fromPos(pos)[0], BlockUtils.fromPos(pos)[1], BlockUtils.fromPos(pos)[2]});
					SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP) player);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:remoteAccessMine.name"), ClientUtils.localize("messages.securitycraft:mrat.bound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(stack, pos, player);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:remoteAccessMine.name"), ClientUtils.localize("messages.securitycraft:mrat.unbound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
				}
			}
			else
				player.openGui(SecurityCraft.instance, GuiHandler.MRAT_MENU_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);

		return EnumActionResult.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, World world, List<String> list, ITooltipFlag flag) {
		if(par1ItemStack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					list.add("---");
					continue;
				}
				else
					list.add(ClientUtils.localize("tooltip.securitycraft:mine") + " " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2]);
			}
			else
				list.add("---");
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, EntityPlayer player) {
		if(stack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTagCompound().setIntArray("mine" + i, new int[]{0, 0, 0});
					SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP) player);
					return;
				}
			}
			else
				continue;


		return;
	}

	private boolean isMineAdded(ItemStack stack, World world, BlockPos pos) {
		if(stack.getTagCompound() == null)
			return false;

		for(int i = 1; i <= 6; i++)
			if(stack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
					return true;
			}
			else
				continue;


		return false;
	}

	private int getNextAvaliableSlot(ItemStack stack){
		for(int i = 1; i <= 6; i++)
			if(stack.getTagCompound() == null)
				return 1;
			else if(stack.getTagCompound().getIntArray("mine" + i).length == 0 || (stack.getTagCompound().getIntArray("mine" + i)[0] == 0 && stack.getTagCompound().getIntArray("mine" + i)[1] == 0 && stack.getTagCompound().getIntArray("mine" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}

}
