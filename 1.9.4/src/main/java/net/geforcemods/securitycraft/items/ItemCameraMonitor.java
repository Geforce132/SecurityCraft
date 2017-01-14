package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCameraMonitor extends Item {
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing side, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(BlockUtils.getBlock(par3World, pos) == mod_SecurityCraft.securityCamera){
				if(!((IOwnable) par3World.getTileEntity(pos)).getOwner().isOwner(par2EntityPlayer)){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.cameraMonitor.name"), I18n.translateToLocal("messages.cameraMonitor.cannotView"), TextFormatting.RED);
					return true;
				}

				if(par2EntityPlayer.inventory.getCurrentItem().getTagCompound() == null){
					par2EntityPlayer.inventory.getCurrentItem().setTagCompound(new NBTTagCompound());
				}

				CameraView view = new CameraView(pos, par2EntityPlayer.dimension);
				
				if(isCameraAdded(par2EntityPlayer.inventory.getCurrentItem().getTagCompound(), view)){
					par2EntityPlayer.inventory.getCurrentItem().getTagCompound().removeTag(getTagNameFromPosition(par2EntityPlayer.inventory.getCurrentItem().getTagCompound(), view));
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.cameraMonitor.name"), I18n.translateToLocal("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
					return true;
				}

				for(int i = 1; i <= 30; i++){
					if (!par2EntityPlayer.inventory.getCurrentItem().getTagCompound().hasKey("Camera" + i)){
						par2EntityPlayer.inventory.getCurrentItem().getTagCompound().setString("Camera" + i, view.toNBTString());
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.cameraMonitor.name"), I18n.translateToLocal("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
						break;
					}
				}

				mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP)par2EntityPlayer);

				return true;
			}
		}else if(par3World.isRemote && BlockUtils.getBlock(par3World, pos) != mod_SecurityCraft.securityCamera){
			if(par2EntityPlayer.getRidingEntity() != null && par2EntityPlayer.getRidingEntity() instanceof EntitySecurityCamera) return true; 
			
			if(par1ItemStack.getTagCompound() == null || par1ItemStack.getTagCompound().hasNoTags()) {
				PlayerUtils.sendMessageToPlayer(par2EntityPlayer, I18n.translateToLocal("item.cameraMonitor.name"), I18n.translateToLocal("messages.cameraMonitor.rightclickToView"), TextFormatting.RED);
				return true;
			}

			par2EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, par3World, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if (par2World.isRemote) {
			if(par3EntityPlayer.getRidingEntity() != null && par3EntityPlayer.getRidingEntity() instanceof EntitySecurityCamera) return par1ItemStack; 
			
			if(!par1ItemStack.hasTagCompound() || !hasCameraAdded(par1ItemStack.getTagCompound())) {
				PlayerUtils.sendMessageToPlayer(par3EntityPlayer, I18n.translateToLocal("item.cameraMonitor.name"), I18n.translateToLocal("messages.cameraMonitor.rightclickToView"), TextFormatting.RED);
			    return par1ItemStack;
			}

			par3EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		}

		return par1ItemStack;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if(par1ItemStack.getTagCompound() == null){
			return;
		}

		par3List.add(I18n.translateToLocal("tooltip.cameraMonitor") + " " + getNumberOfCamerasBound(par1ItemStack.getTagCompound()) + "/30");
	}

	public String getTagNameFromPosition(NBTTagCompound nbt, CameraView view) {
		for(int i = 1; i <= 30; i++){
			if(nbt.hasKey("Camera" + i)){
				String[] coords = nbt.getString("Camera" + i).split(" ");
								
				if(view.checkCoordinates(coords)){
					return "Camera" + i;
				}
			}
		}

		return "";
	}

	public int getSlotFromPosition(NBTTagCompound nbt, CameraView view) {
		for(int i = 1; i <= 30; i++){
			if(nbt.hasKey("Camera" + i)){
				String[] coords = nbt.getString("Camera" + i).split(" ");
								
				if(view.checkCoordinates(coords)){
					return i;
				}
			}
		}

		return -1;
	}

	public boolean hasCameraAdded(NBTTagCompound nbt){
		if(nbt == null) return false;
		
		for(int i = 1; i <= 30; i++) {
			if(nbt.hasKey("Camera" + i)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isCameraAdded(NBTTagCompound nbt, CameraView view){
		for(int i = 1; i <= 30; i++){			
			if(nbt.hasKey("Camera" + i)){
				String[] coords = nbt.getString("Camera" + i).split(" ");
			    
				if(view.checkCoordinates(coords)) {
					return true;
				}
			}
		}

		return false;
	}

	public ArrayList<CameraView> getCameraPositions(NBTTagCompound nbt){
		ArrayList<CameraView> list = new ArrayList<CameraView>();

		for(int i = 1; i <= 30; i++){
			if(nbt != null && nbt.hasKey("Camera" + i)){								
				String[] coords = nbt.getString("Camera" + i).split(" ");

				list.add(new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? Integer.parseInt(coords[3]) : 0)));
			}
			else
				list.add(null);
		}

		return list;
	}
	
	public int getNumberOfCamerasBound(NBTTagCompound nbt) {
		if(nbt == null) return 0;
		
		for(int i = 1; i <= 31; i++) {
			if(nbt.hasKey("Camera" + i)) {
				continue;
			}
			else
			{
				return i - 1;
			}
		}
		
		return 0;
	}
	
}