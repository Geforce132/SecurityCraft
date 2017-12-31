package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.network.packets.PacketCCreateLGView;
import net.geforcemods.securitycraft.network.packets.PacketCSetCameraLocation;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemCameraMonitor extends ItemMap {

	public ItemCameraMonitor(){
		super();
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(!par3World.isRemote)
			//When the mod is using the LookingGlass system.
			if(SecurityCraft.instance.useLookingGlass()){
				if(par3World.getBlock(par4, par5, par6) instanceof BlockSecurityCamera){
					if(!((TileEntitySecurityCamera) par3World.getTileEntity(par4, par5, par6)).getOwner().isOwner(par2EntityPlayer)){
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.cannotView"), EnumChatFormatting.RED);
						return false;
					}

					if(par1ItemStack.getTagCompound() == null)
						par1ItemStack.setTagCompound(new NBTTagCompound());

					CameraView view = new CameraView(par4, par5, par6, par2EntityPlayer.dimension);

					if(isCameraAdded(par1ItemStack.getTagCompound(), view)){
						par1ItemStack.getTagCompound().removeTag(getTagNameFromPosition(par1ItemStack.getTagCompound(), view));
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(view.x, view.y, view.z)), EnumChatFormatting.RED);
						return true;
					}

					par1ItemStack.getTagCompound().setString("Camera1", view.toNBTString());
					SecurityCraft.network.sendTo(new PacketCCreateLGView(view.x, view.y, view.z, view.dimension), (EntityPlayerMP) par2EntityPlayer);
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(view.x, view.y, view.z)), EnumChatFormatting.GREEN);

					return true;
				}else if(par3World.getBlock(par4, par5, par6) == SCContent.frame){
					if(!par1ItemStack.hasTagCompound() || !hasCameraAdded(par1ItemStack.getTagCompound())) return false;

					CameraView view = getCameraView(par1ItemStack.getTagCompound());
					if(view == null) return true;

					((TileEntityFrame) par3World.getTileEntity(par4, par5, par6)).setCameraLocation(view.x, view.y, view.z, view.dimension);
					SecurityCraft.network.sendToAll(new PacketCSetCameraLocation(par4, par5, par6, view.x, view.y, view.z, view.dimension));
					par1ItemStack.stackSize--;

					return true;
				}else{
					if(!par1ItemStack.hasTagCompound() || !hasCameraAdded(par1ItemStack.getTagCompound())) return false;

					CameraView view = getCameraView(par1ItemStack.getTagCompound());
					if(view == null) return true;

					if(!(par3World.getBlock(view.x, view.y, view.z) instanceof BlockSecurityCamera)){
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.noCamera").replace("#", Utils.getFormattedCoordinates(view.x, view.y, view.z)), EnumChatFormatting.RED);
						return false;
					}

					if(SecurityCraft.instance.useLookingGlass())
						SecurityCraft.network.sendTo(new PacketCCreateLGView(view.x, view.y, view.z, view.dimension), (EntityPlayerMP) par2EntityPlayer);
					else
						par2EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, par3World, par4, par5, par6);

					return false;
				}
			}
			else if(par3World.getBlock(par4, par5, par6) == SCContent.securityCamera){
				if(!((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwner().isOwner(par2EntityPlayer)){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.cannotView"), EnumChatFormatting.RED);
					return true;
				}

				if(par2EntityPlayer.getCurrentEquippedItem().getTagCompound() == null)
					par2EntityPlayer.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());

				CameraView view = new CameraView(par4, par5, par6, par2EntityPlayer.dimension);

				if(isCameraAdded(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), view)){
					par2EntityPlayer.getCurrentEquippedItem().getTagCompound().removeTag(getTagNameFromPosition(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), view));
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(par4, par5, par6)), EnumChatFormatting.RED);
					return true;
				}

				for(int i = 1; i <= 30; i++)
					if (!par2EntityPlayer.getCurrentEquippedItem().getTagCompound().hasKey("Camera" + i)){
						par2EntityPlayer.getCurrentEquippedItem().getTagCompound().setString("Camera" + i, view.toNBTString());
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(par4, par5, par6)), EnumChatFormatting.GREEN);
						break;
					}

				SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP)par2EntityPlayer);

				return true;
			}

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
		if(!par2World.isRemote){
			if(!par1ItemStack.hasTagCompound() || !hasCameraAdded(par1ItemStack.getTagCompound())){
				PlayerUtils.sendMessageToPlayer(par3EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.rightclickToView"), EnumChatFormatting.RED);
				return par1ItemStack;
			}

			if(SecurityCraft.instance.useLookingGlass()){
				CameraView view = getCameraView(par1ItemStack.getTagCompound());

				if(!(par2World.getBlock(view.x, view.y, view.z) instanceof BlockSecurityCamera)){
					PlayerUtils.sendMessageToPlayer(par3EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.noCamera").replace("#", Utils.getFormattedCoordinates(view.x, view.y, view.z)), EnumChatFormatting.RED);
					return par1ItemStack;
				}

				SecurityCraft.network.sendTo(new PacketCCreateLGView(view.x, view.y, view.z, view.dimension), (EntityPlayerMP) par3EntityPlayer);
			}
			else
				par3EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		}

		return par1ItemStack;
	}

	@Override
	public void onUpdate(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if(par1ItemStack.getTagCompound() == null)
			return;

		par3List.add(StatCollector.translateToLocal("tooltip.cameraMonitor") + " " + getNumberOfCamerasBound(par1ItemStack.getTagCompound()) + "/30");
	}

	public CameraView getCameraView(NBTTagCompound nbt){
		for(int i = 1; i <= 30; i++)
			if(nbt.hasKey("Camera" + i)) {
				String[] coords = nbt.getString("Camera" + i).split(" ");

				return new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? Integer.parseInt(coords[3]) : 0));
			}

		return null;
	}

	public boolean hasCameraAdded(NBTTagCompound nbt){
		if(nbt == null) return false;

		for(int i = 1; i <= 30; i++)
			if(nbt.hasKey("Camera" + i))
				return true;

		return false;
	}

	public int getSlotFromPosition(NBTTagCompound nbt, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(nbt.hasKey("Camera" + i)){
				String[] coords = nbt.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return i;
			}

		return -1;
	}

	public static String getTagNameFromPosition(NBTTagCompound nbt, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(nbt.hasKey("Camera" + i)){
				String[] coords = nbt.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return "Camera" + i;
			}

		return "";
	}

	public boolean isCameraAdded(NBTTagCompound nbt, CameraView view){
		for(int i = 1; i <= 30; i++)
			if(nbt.hasKey("Camera" + i)){
				String[] coords = nbt.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return true;
			}

		return false;
	}

	public ArrayList<CameraView> getCameraPositions(NBTTagCompound nbt){
		ArrayList<CameraView> list = new ArrayList<CameraView>();

		for(int i = 1; i <= 30; i++)
			if(nbt != null && nbt.hasKey("Camera" + i)){
				String[] coords = nbt.getString("Camera" + i).split(" ");

				list.add(new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? Integer.parseInt(coords[3]) : 0)));
			}
			else
				list.add(null);

		return list;
	}

	public int getNumberOfCamerasBound(NBTTagCompound nbt) {
		if(nbt == null) return 0;

		for(int i = 1; i <= 31; i++)
			if(nbt.hasKey("Camera" + i))
				continue;
			else
				return i - 1;

		return 0;
	}

}
