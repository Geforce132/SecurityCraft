package org.freeforums.geforce.securitycraft.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.gui.GuiCameraMonitor;
import org.freeforums.geforce.securitycraft.interfaces.IOwnable;
import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateNBTTag;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCameraMonitor extends Item {
	
	public ItemCameraMonitor(){
		super();
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(par3World.getBlock(par4, par5, par6) == mod_SecurityCraft.securityCamera){
				if(((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerUUID() != null && !((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerUUID().matches(par2EntityPlayer.getGameProfile().getId().toString())){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "I'm sorry, you can not view this camera. This camera is owned by " + ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwnerName() + ".", EnumChatFormatting.RED);
					return true;
				}
				
				
				if(par2EntityPlayer.getCurrentEquippedItem().getTagCompound() == null){
					par2EntityPlayer.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());				
				}
				
				if(this.isCameraAdded(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), par4, par5, par6)){ 
					par2EntityPlayer.getCurrentEquippedItem().getTagCompound().removeTag(this.getTagNameFromPosition(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), par4, par5, par6));
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Unbound camera (at X: " + par4 + " Y: " + par5 + " Z: " + par6 + ") to monitor.", EnumChatFormatting.RED);
					return true; 
				}
				
				for(int i = 1; i <= 10; i++){
					if(!par2EntityPlayer.getCurrentEquippedItem().getTagCompound().hasKey("Camera" + i)){
						par2EntityPlayer.getCurrentEquippedItem().getTagCompound().setString("Camera" + i, par4 + " " + par5 + " " + par6);
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Bound camera (at X: " + par4 + " Y: " + par5 + " Z: " + par6 + ") to monitor.", EnumChatFormatting.GREEN);
						break;
					}
				}
				
				mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par2EntityPlayer);
				
				return true;
			}
		}else if(par3World.isRemote && par3World.getBlock(par4, par5, par6) != mod_SecurityCraft.securityCamera){
			this.openMonitorGUI(par1ItemStack, par2EntityPlayer);
			return true;
		}
		
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){  	    
    	if(par2World.isRemote){
    		this.openMonitorGUI(par1ItemStack, par3EntityPlayer);
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
				par3List.add("Camera #" + i + ": " + par1ItemStack.getTagCompound().getString("Camera" + i));
			}
    	}
    }
	
	@SideOnly(Side.CLIENT)
	public void openMonitorGUI(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer){
		if(!par1ItemStack.hasTagCompound() || !par1ItemStack.stackTagCompound.hasKey("Camera1")){ 
			PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Add cameras to this monitor by right-clicking them!", EnumChatFormatting.RED);
			return; 
		}
		
		Minecraft.getMinecraft().displayGuiScreen(new GuiCameraMonitor((ItemCameraMonitor) par1ItemStack.getItem(), par1ItemStack.getTagCompound()));
	}
	
	public String getTagNameFromPosition(NBTTagCompound nbt, int par2, int par3, int par4){
		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");	
				String[] coords = new String[]{scanner.next(), scanner.next(), scanner.next()};
				
				if(coords[0].matches(par2 + "") && coords[1].matches(par3 + "") && coords[2].matches(par4 + "")){
					return "Camera" + i;
				}
			}
		}
		
		return "";
	}
	
	public int getSlotFromPosition(NBTTagCompound nbt, int par2, int par3, int par4){
		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");	
				String[] coords = new String[]{scanner.next(), scanner.next(), scanner.next()};
				
				if(coords[0].matches(par2 + "") && coords[1].matches(par3 + "") && coords[2].matches(par4 + "")){
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public boolean isCameraAdded(NBTTagCompound nbt, int par2, int par3, int par4){
		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");	
				String[] coords = new String[]{scanner.next(), scanner.next(), scanner.next()};
				
				if(coords[0].matches(par2 + "") && coords[1].matches(par3 + "") && coords[2].matches(par4 + "")){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Gets all the camera positions bound to this monitor.
	 * 
	 * @param nbt The monitor's NBTTagCompound.
	 * @return All camera positions.
	 */
	public ArrayList<int[]> getCameraPositions(NBTTagCompound nbt){
		ArrayList<int[]> list = new ArrayList<int[]>();

		for(int i = 0; i <= 10; i++){
			if(nbt != null && nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");	
				String[] coords = new String[]{scanner.next(), scanner.next(), scanner.next()};
				
				list.add(new int[]{Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])});
			}
		}
		
		return list;
	}

}
