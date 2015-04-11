package org.freeforums.geforce.securitycraft.items;

import java.util.Scanner;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.Utils;
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
				
				for(int i = 1; i <= 10; i++){
					if(!par2EntityPlayer.getCurrentEquippedItem().getTagCompound().hasKey("Camera" + i)){
						par2EntityPlayer.getCurrentEquippedItem().getTagCompound().setString("Camera" + i, pos.getX() + " " + pos.getY() + " " + pos.getZ());
						HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "Bound camera (at " + Utils.getFormattedCoordinates(pos) + ") to monitor.", EnumChatFormatting.GREEN);
						break;
					}
				}
			}
		}
		
		return false;
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

}
