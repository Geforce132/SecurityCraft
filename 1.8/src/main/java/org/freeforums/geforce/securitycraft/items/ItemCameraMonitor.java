package org.freeforums.geforce.securitycraft.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.gui.GuiCameraMonitorOverlay;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class ItemCameraMonitor extends Item {
	
	public ItemCameraMonitor(){
		super();
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing facing, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(par3World.getBlockState(pos).getBlock() == mod_SecurityCraft.securityCamera){
				if(par2EntityPlayer.getCurrentEquippedItem().getTagCompound() == null){
					par2EntityPlayer.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());				
				}
				
				if(this.isCameraAdded(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), pos)){ return false; }
				
				for(int i = 1; i <= 10; i++){
					if(!par2EntityPlayer.getCurrentEquippedItem().getTagCompound().hasKey("Camera" + i)){
						par2EntityPlayer.getCurrentEquippedItem().getTagCompound().setString("Camera" + i, pos.getX() + " " + pos.getY() + " " + pos.getZ());
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Bound camera (at " + Utils.getFormattedCoordinates(pos) + ") to monitor.", EnumChatFormatting.GREEN);
						break;
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){  	    
    	if(!par2World.isRemote && par3EntityPlayer.isSneaking()){
    		Minecraft.getMinecraft().displayGuiScreen(new GuiCameraMonitorOverlay((ItemCameraMonitor) par1ItemStack.getItem(), par1ItemStack.getTagCompound()));
    		//par3EntityPlayer.openGui(mod_SecurityCraft.instance, 17, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
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
	
	private boolean isCameraAdded(NBTTagCompound nbt, BlockPos pos){
		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");	
				String[] coords = new String[]{scanner.next(), scanner.next(), scanner.next()};
				
				if(coords[0].matches(pos.getX() + "") && coords[1].matches(pos.getY() + "") && coords[2].matches(pos.getZ() + "")){
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
	public ArrayList<BlockPos> getCameraPositions(NBTTagCompound nbt){
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();

		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");	
				String[] coords = new String[]{scanner.next(), scanner.next(), scanner.next()};
				
				list.add(new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])));
			}
		}
		
		return list;
	}

}
