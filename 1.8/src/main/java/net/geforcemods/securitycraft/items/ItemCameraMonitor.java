package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiCameraMonitor;
import net.geforcemods.securitycraft.main.Utils;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCameraMonitor extends Item {
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing side, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(BlockUtils.getBlock(par3World, pos) == mod_SecurityCraft.securityCamera){
				if(!BlockUtils.isOwnerOfBlock((IOwnable) par3World.getTileEntity(pos), par2EntityPlayer)){
					Utils.PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.cannotView"), EnumChatFormatting.RED);
					return true;
				}

				if(par2EntityPlayer.getCurrentEquippedItem().getTagCompound() == null){
					par2EntityPlayer.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());
				}

				if(isCameraAdded(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), pos.getX(), pos.getY(), pos.getZ())){
					par2EntityPlayer.getCurrentEquippedItem().getTagCompound().removeTag(getTagNameFromPosition(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), pos.getX(), pos.getY(), pos.getZ()));
					Utils.PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(pos)), EnumChatFormatting.RED);
					return true;
				}

				for(int i = 1; i <= 10; i++){
					if (!par2EntityPlayer.getCurrentEquippedItem().getTagCompound().hasKey("Camera" + i)){
						par2EntityPlayer.getCurrentEquippedItem().getTagCompound().setString("Camera" + i, pos.getX() + " " + pos.getY() + " " + pos.getZ());
						Utils.PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(pos)), EnumChatFormatting.GREEN);
						break;
					}
				}

				mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP)par2EntityPlayer);

				return true;
			}
		}else if((par3World.isRemote) && (BlockUtils.getBlock(par3World, pos) != mod_SecurityCraft.securityCamera)){
			openMonitorGUI(par1ItemStack, par2EntityPlayer);
			return true;
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if (par2World.isRemote) {
			openMonitorGUI(par1ItemStack, par3EntityPlayer);
		}

		return par1ItemStack;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if(par1ItemStack.getTagCompound() == null){
			return;
		}

		for(int i = 1; i <= 10; i++){
			if(par1ItemStack.getTagCompound().hasKey("Camera" + i)){
				par3List.add("Camera #" + i + ": " + par1ItemStack.getTagCompound().getString(new StringBuilder().append("Camera").append(i).toString()));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void openMonitorGUI(ItemStack par1ItemStack, EntityPlayer player){
		if(player.ridingEntity != null && player.ridingEntity instanceof EntitySecurityCamera){ return; }
		
		Minecraft.getMinecraft().displayGuiScreen(new GuiCameraMonitor((ItemCameraMonitor)par1ItemStack.getItem(), par1ItemStack.getTagCompound()));
	}

	public String getTagNameFromPosition(NBTTagCompound nbt, int par2, int par3, int par4) {
		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");
				String[] coords = { scanner.next(), scanner.next(), scanner.next() };
				scanner.close();
				
				if((coords[0].matches(par2 + "")) && (coords[1].matches(par3 + "")) && (coords[2].matches(par4 + ""))){
					return "Camera" + i;
				}
			}
		}

		return "";
	}

	public int getSlotFromPosition(NBTTagCompound nbt, int par2, int par3, int par4) {
		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");
				String[] coords = { scanner.next(), scanner.next(), scanner.next() };
				scanner.close();
				
				if((coords[0].matches(par2 + "")) && (coords[1].matches(par3 + "")) && (coords[2].matches(par4 + ""))){
					return i;
				}
			}
		}

		return -1;
	}

	public boolean isCameraAdded(NBTTagCompound nbt, int par2, int par3, int par4) {
		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");
				String[] coords = { scanner.next(), scanner.next(), scanner.next() };
				scanner.close();
				
				if((coords[0].matches(par2 + "")) && (coords[1].matches(par3 + "")) && (coords[2].matches(par4 + ""))){
					return true;
				}
			}
		}

		return false;
	}

	public ArrayList<int[]> getCameraPositions(NBTTagCompound nbt){
		ArrayList list = new ArrayList();

		for(int i = 0; i <= 10; i++){
			if(nbt != null && nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");
				String[] coords = { scanner.next(), scanner.next(), scanner.next() };
				scanner.close();
				
				list.add(new int[] { Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]) });
			}
		}

		return list;
	}
	
}