package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		ItemStack itemStackIn = playerIn.getHeldItem(hand);

		if(worldIn.isRemote)
			return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
		else{
			playerIn.openGui(mod_SecurityCraft.instance, GuiHandler.MRAT_MENU_ID, worldIn, (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ);
			return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = playerIn.getHeldItem(hand);

		if(!worldIn.isRemote)
			if(BlockUtils.getBlock(worldIn, pos) instanceof IExplosive){
				if(!isMineAdded(stack, worldIn, pos)){
					int availSlot = getNextAvaliableSlot(stack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.remoteAccessMine.name"), ClientUtils.localize("messages.mrat.noSlots"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(worldIn.getTileEntity(pos) instanceof IOwnable && !((IOwnable) worldIn.getTileEntity(pos)).getOwner().isOwner(playerIn)){
						PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.remoteAccessMine.name"), ClientUtils.localize("messages.mrat.cantBind"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(stack.getTagCompound() == null)
						stack.setTagCompound(new NBTTagCompound());

					stack.getTagCompound().setIntArray(("mine" + availSlot), new int[]{BlockUtils.fromPos(pos)[0], BlockUtils.fromPos(pos)[1], BlockUtils.fromPos(pos)[2]});
					mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP) playerIn);
					PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.remoteAccessMine.name"), ClientUtils.localize("messages.mrat.bound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(stack, pos, playerIn);
					PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.remoteAccessMine.name"), ClientUtils.localize("messages.mrat.unbound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
				}
			}
			else
				playerIn.openGui(mod_SecurityCraft.instance, GuiHandler.MRAT_MENU_ID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);

		return EnumActionResult.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, World world, List<String> par3List, ITooltipFlag flagIn) {
		if(par1ItemStack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					par3List.add("---");
					continue;
				}
				else
					par3List.add(ClientUtils.localize("tooltip.mine") + " " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2]);
			}
			else
				par3List.add("---");
	}

	private void removeTagFromItemAndUpdate(ItemStack par1ItemStack, BlockPos pos, EntityPlayer par5EntityPlayer) {
		if(par1ItemStack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					par1ItemStack.getTagCompound().setIntArray("mine" + i, new int[]{0, 0, 0});
					mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par5EntityPlayer);
					return;
				}
			}
			else
				continue;


		return;
	}

	private boolean isMineAdded(ItemStack par1ItemStack, World par2World, BlockPos pos) {
		if(par1ItemStack.getTagCompound() == null)
			return false;

		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
					return true;
			}
			else
				continue;


		return false;
	}

	private int getNextAvaliableSlot(ItemStack par1ItemStack){
		for(int i = 1; i <= 6; i++)
			if(par1ItemStack.getTagCompound() == null)
				return 1;
			else if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length == 0 || (par1ItemStack.getTagCompound().getIntArray("mine" + i)[0] == 0 && par1ItemStack.getTagCompound().getIntArray("mine" + i)[1] == 0 && par1ItemStack.getTagCompound().getIntArray("mine" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}

}
