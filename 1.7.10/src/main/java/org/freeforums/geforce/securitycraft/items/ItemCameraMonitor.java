package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketCSetCameraLocation;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCameraMonitor extends ItemMap {
	
	public ItemCameraMonitor(){
		super();
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(par3World.getBlock(par4, par5, par6) instanceof BlockSecurityCamera){
				if(par1ItemStack.getTagCompound() == null){
					par1ItemStack.setTagCompound(new NBTTagCompound());
		    	}
				
				par1ItemStack.getTagCompound().setString("Camera", par4 + " " + par5 + " " + par6);
				PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Bound camera at" + Utils.getFormattedCoordinates(par4, par5, par6) + " to monitor.", EnumChatFormatting.GREEN);
				
				return true;
			}
			
			if(par3World.getBlock(par4, par5, par6) == mod_SecurityCraft.frame){
				if(!par1ItemStack.hasTagCompound() || !par1ItemStack.getTagCompound().hasKey("Camera")){ return false; }
				
				((TileEntityFrame) par3World.getTileEntity(par4, par5, par6)).setCameraLocation(Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera").split(" ")[0]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera").split(" ")[1]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera").split(" ")[2]));
				mod_SecurityCraft.network.sendToAll(new PacketCSetCameraLocation(par4, par5, par6, Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera").split(" ")[0]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera").split(" ")[1]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera").split(" ")[2])));
				
				return true;
			}
			
			return false;
		}
		
		return false;
	}
	
    public void onUpdate(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	if(par1ItemStack.getTagCompound() == null){
    		return;
    	}
    	
    	if(par1ItemStack.getTagCompound().hasKey("Camera")){
			par3List.add("Camera: " + par1ItemStack.getTagCompound().getString("Camera"));
    	}
    }
	
	public int[] getCameraCoordinates(NBTTagCompound nbt){
		if(nbt.hasKey("Camera")){
			String[] coords = nbt.getString("Camera").split(" ");

			return new int[]{Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])};
		}
		
		return null;
	}
	
	public boolean hasCameraAdded(NBTTagCompound nbt){
		return nbt != null && nbt.hasKey("Camera");	
	}
	
	public boolean isCameraAdded(NBTTagCompound nbt, int par2, int par3, int par4){
		if(nbt.hasKey("Camera")){
			String[] coords = nbt.getString("Camera").split(" ");
			
			if(coords[0].matches(par2 + "") && coords[1].matches(par3 + "") && coords[2].matches(par4 + "")){
				return true;
			}
		}
		
		return false;
	}
	
}
