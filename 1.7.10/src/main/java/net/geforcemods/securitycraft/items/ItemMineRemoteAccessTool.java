package net.geforcemods.securitycraft.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMineRemoteAccessTool extends Item {

	public int listIndex = 0;

	public ItemMineRemoteAccessTool() {
		super();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		if(world.isRemote)
			return stack;
		else{
			player.openGui(SecurityCraft.instance, GuiHandler.MRAT_MENU_ID, world, (int)player.posX, (int)player.posY, (int)player.posZ);
			return stack;
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
			if(world.getBlock(x, y, z) instanceof IExplosive){
				if(!isMineAdded(stack, world, x, y, z)){
					int availSlot = getNextAvaliableSlot(stack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:remoteAccessMine.name"), StatCollector.translateToLocal("messages.securitycraft:mrat.noSlots"), EnumChatFormatting.RED);
						return false;
					}

					if(world.getTileEntity(x, y, z) instanceof IOwnable && !((IOwnable) world.getTileEntity(x, y, z)).getOwner().isOwner(player)){
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:remoteAccessMine.name"), StatCollector.translateToLocal("messages.securitycraft:mrat.cantBind"), EnumChatFormatting.RED);
						return false;
					}

					if(stack.stackTagCompound == null)
						stack.stackTagCompound = new NBTTagCompound();

					stack.stackTagCompound.setIntArray(("mine" + availSlot), new int[]{x, y, z});
					SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP) player);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:remoteAccessMine.name"), StatCollector.translateToLocal("messages.securitycraft:mrat.bound").replace("#", Utils.getFormattedCoordinates(x, y, z)), EnumChatFormatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(stack, x, y, z, player);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:remoteAccessMine.name"), StatCollector.translateToLocal("messages.securitycraft:mrat.unbound").replace("#", Utils.getFormattedCoordinates(x, y, z)), EnumChatFormatting.RED);
				}
			}
			else
				player.openGui(SecurityCraft.instance, GuiHandler.MRAT_MENU_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if(stack.stackTagCompound == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.stackTagCompound.getIntArray("mine" + i).length > 0){
				int[] coords = stack.stackTagCompound.getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					list.add("---");
					continue;
				}
				else
					list.add(StatCollector.translateToLocal("tooltip.securitycraft:mine") + " " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2]);
			}
			else
				list.add("---");
	}


	private void removeTagFromItemAndUpdate(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		if(stack.stackTagCompound == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.stackTagCompound.getIntArray("mine" + i).length > 0){
				int[] coords = stack.stackTagCompound.getIntArray("mine" + i);

				if(coords[0] == x && coords[1] == y && coords[2] == z){
					stack.stackTagCompound.setIntArray("mine" + i, new int[]{0, 0, 0});
					SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP) player);
					return;
				}
			}
			else
				continue;


		return;
	}

	private boolean isMineAdded(ItemStack stack, World world, int x, int y, int z) {
		if(stack.stackTagCompound == null)
			return false;

		for(int i = 1; i <= 6; i++)
			if(stack.stackTagCompound.getIntArray("mine" + i).length > 0){
				int[] coords = stack.stackTagCompound.getIntArray("mine" + i);

				if(coords[0] == x && coords[1] == y && coords[2] == z)
					return true;
			}
			else
				continue;


		return false;
	}

	private int getNextAvaliableSlot(ItemStack stack){
		for(int i = 1; i <= 6; i++)
			if(stack.stackTagCompound == null)
				return 1;
			else if(stack.stackTagCompound.getIntArray("mine" + i).length == 0 || (stack.stackTagCompound.getIntArray("mine" + i)[0] == 0 && stack.stackTagCompound.getIntArray("mine" + i)[1] == 0 && stack.stackTagCompound.getIntArray("mine" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}
}
