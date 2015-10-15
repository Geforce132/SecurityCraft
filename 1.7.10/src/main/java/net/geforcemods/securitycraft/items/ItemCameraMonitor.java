package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.gui.GuiCameraMonitor;
import net.geforcemods.securitycraft.main.Utils;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCCreateLGView;
import net.geforcemods.securitycraft.network.packets.PacketCOpenMonitorGUI;
import net.geforcemods.securitycraft.network.packets.PacketCSetCameraLocation;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemCameraMonitor extends ItemMap {
	
	public ItemCameraMonitor(){
		super();
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(par3World.getBlock(par4, par5, par6) instanceof BlockSecurityCamera){
				if(BlockUtils.isOwnerOfBlock((TileEntitySecurityCamera) par3World.getTileEntity(par4, par5, par6), par2EntityPlayer)){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Camera Monitor", "You can't view a camera that doesn't belong to you", EnumChatFormatting.RED);
					return false;
				}
				
				if(par1ItemStack.getTagCompound() == null){
					par1ItemStack.setTagCompound(new NBTTagCompound());
		    	}
				
				par1ItemStack.getTagCompound().setString("Camera1", par4 + " " + par5 + " " + par6);
				PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Camera Monitor", "Bound camera at" + Utils.getFormattedCoordinates(par4, par5, par6) + " to monitor.", EnumChatFormatting.GREEN);
				
				return true;
			}else if(par3World.getBlock(par4, par5, par6) == mod_SecurityCraft.frame){
				if(!par1ItemStack.hasTagCompound() || !par1ItemStack.getTagCompound().hasKey("Camera1")){ return false; }

				((TileEntityFrame) par3World.getTileEntity(par4, par5, par6)).setCameraLocation(Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[0]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[1]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[2]));
				mod_SecurityCraft.network.sendToAll(new PacketCSetCameraLocation(par4, par5, par6, Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[0]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[1]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[2])));
				par1ItemStack.stackSize--;
				
				return true;
			}else{
				if(!par1ItemStack.hasTagCompound() || !par1ItemStack.getTagCompound().hasKey("Camera1")){ 
					return false;
				}
				
				int[] camCoords = {Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[0]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[1]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[2])};
				
	    		if(!(par3World.getBlock(camCoords[0], camCoords[1], camCoords[2]) instanceof BlockSecurityCamera)){
	    			PlayerUtils.sendMessageToPlayer(par2EntityPlayer, "Camera Monitor", "There is no camera at the location" + Utils.getFormattedCoordinates(camCoords[0], camCoords[1], camCoords[2]) + ", right-click a new camera to reset the monitor.", EnumChatFormatting.RED);
					return false;
	    		}
				
				if(Loader.isModLoaded("LookingGlass") && mod_SecurityCraft.configHandler.useLookingGlass){
					mod_SecurityCraft.network.sendTo(new PacketCCreateLGView(camCoords[0], camCoords[1], camCoords[2], 0), (EntityPlayerMP) par2EntityPlayer);
				}else{
	    			mod_SecurityCraft.network.sendTo(new PacketCOpenMonitorGUI(par1ItemStack), (EntityPlayerMP) par2EntityPlayer);
				}
				
				return false;
			}
		}
		
		return false;
	}
	
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){  	   
    	if(!par2World.isRemote){
    		if(!par1ItemStack.hasTagCompound() || !par1ItemStack.getTagCompound().hasKey("Camera1")){ 
				PlayerUtils.sendMessageToPlayer(par3EntityPlayer, "Camera Monitor", "Right-click a security camera to view it.", EnumChatFormatting.RED);
				return par1ItemStack;
			}
    		
    		int[] camCoords = {Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[0]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[1]), Integer.parseInt(par1ItemStack.getTagCompound().getString("Camera1").split(" ")[2])};
			
    		if(!(par2World.getBlock(camCoords[0], camCoords[1], camCoords[2]) instanceof BlockSecurityCamera)){
    			PlayerUtils.sendMessageToPlayer(par3EntityPlayer, "Camera Monitor", "There is no camera at the location" + Utils.getFormattedCoordinates(camCoords[0], camCoords[1], camCoords[2]) + ", right-click a new camera to reset the monitor.", EnumChatFormatting.RED);
				return par1ItemStack;
    		}
    		
    		if(Loader.isModLoaded("LookingGlass") && mod_SecurityCraft.configHandler.useLookingGlass){
    			mod_SecurityCraft.network.sendTo(new PacketCCreateLGView(camCoords[0], camCoords[1], camCoords[2], 0), (EntityPlayerMP) par3EntityPlayer);
    		}else{
    			mod_SecurityCraft.network.sendTo(new PacketCOpenMonitorGUI(par1ItemStack), (EntityPlayerMP) par3EntityPlayer);
    		}
    	}
    	
		return par1ItemStack;
    }
	
    public void onUpdate(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	if(par1ItemStack.getTagCompound() == null){
    		return;
    	}
    	
    	if(par1ItemStack.getTagCompound().hasKey("Camera1")){
    		par3List.add("Camera: " + par1ItemStack.getTagCompound().getString("Camera1"));
    	}
	}

	@SideOnly(Side.CLIENT)
	public void openMonitorGUI(ItemStack par1ItemStack){
		Minecraft.getMinecraft().displayGuiScreen(new GuiCameraMonitor((ItemCameraMonitor)par1ItemStack.getItem(), par1ItemStack.getTagCompound()));
	}

	public int[] getCameraCoordinates(NBTTagCompound nbt){
		if(nbt.hasKey("Camera1")){
			String[] coords = nbt.getString("Camera1").split(" ");

			return new int[]{Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])};
		}
		
		return null;
	}
	
	public boolean hasCameraAdded(NBTTagCompound nbt){
		return nbt != null && nbt.hasKey("Camera1");	
	}
	
	public int getSlotFromPosition(NBTTagCompound nbt, int par2, int par3, int par4) {
		for(int i = 0; i <= 10; i++){
			if(nbt.hasKey("Camera1" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera1" + i)).useDelimiter(" ");
				String[] coords = { scanner.next(), scanner.next(), scanner.next() };
				scanner.close();
				
				if((coords[0].matches(par2 + "")) && (coords[1].matches(par3 + "")) && (coords[2].matches(par4 + ""))){
					return i;
				}
			}
		}

		return -1;
	}
	
	public boolean isCameraAdded(NBTTagCompound nbt, int par2, int par3, int par4){
		if(nbt.hasKey("Camera1")){
			String[] coords = nbt.getString("Camera1").split(" ");
			
			if(coords[0].matches(par2 + "") && coords[1].matches(par3 + "") && coords[2].matches(par4 + "")){
				return true;
			}
		}
		
		return false;
	}

	@SuppressWarnings({"rawtypes", "resource", "unchecked"})
	public ArrayList<int[]> getCameraPositions(NBTTagCompound nbt){
		ArrayList list = new ArrayList();

		for(int i = 1; i <= 10; i++){
			if(nbt.hasKey("Camera" + i)){
				Scanner scanner = new Scanner(nbt.getString("Camera" + i)).useDelimiter(" ");
				String[] coords = { scanner.next(), scanner.next(), scanner.next() };

				list.add(new int[] { Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]) });
			}
		}

		return list;
	}

}
