package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiHandler;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCameraMonitor extends Item {

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing side, float par8, float par9, float par10){
		if(!par3World.isRemote){
			if(BlockUtils.getBlock(par3World, pos) == SCContent.securityCamera){
				if(!((IOwnable) par3World.getTileEntity(pos)).getOwner().isOwner(par2EntityPlayer)){
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.cannotView"), EnumChatFormatting.RED);
					return true;
				}

				if(par2EntityPlayer.getCurrentEquippedItem().getTagCompound() == null)
					par2EntityPlayer.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());

				CameraView view = new CameraView(pos, par2EntityPlayer.dimension);

				if(isCameraAdded(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), view)){
					par2EntityPlayer.getCurrentEquippedItem().getTagCompound().removeTag(getTagNameFromPosition(par2EntityPlayer.getCurrentEquippedItem().getTagCompound(), view));
					PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(pos)), EnumChatFormatting.RED);
					return true;
				}

				for(int i = 1; i <= 30; i++)
					if (!par2EntityPlayer.getCurrentEquippedItem().getTagCompound().hasKey("Camera" + i)){
						par2EntityPlayer.getCurrentEquippedItem().getTagCompound().setString("Camera" + i, view.toNBTString());
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(pos)), EnumChatFormatting.GREEN);
						break;
					}

				SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP)par2EntityPlayer);

				return true;
			}
		}else if(par3World.isRemote && BlockUtils.getBlock(par3World, pos) != SCContent.securityCamera){
			if(par2EntityPlayer.ridingEntity != null && par2EntityPlayer.ridingEntity instanceof EntitySecurityCamera) return true;

			if(par1ItemStack.getTagCompound() == null || par1ItemStack.getTagCompound().hasNoTags()) {
				PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.rightclickToView"), EnumChatFormatting.RED);
				return true;
			}

			par2EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, par3World, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if (par2World.isRemote) {
			if(par3EntityPlayer.ridingEntity != null && par3EntityPlayer.ridingEntity instanceof EntitySecurityCamera) return par1ItemStack;

			if(!par1ItemStack.hasTagCompound() || !hasCameraAdded(par1ItemStack.getTagCompound())) {
				PlayerUtils.sendMessageToPlayer(par3EntityPlayer, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.rightclickToView"), EnumChatFormatting.RED);
				return par1ItemStack;
			}

			par3EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		}

		return par1ItemStack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if(par1ItemStack.getTagCompound() == null)
			return;

		par3List.add(StatCollector.translateToLocal("tooltip.cameraMonitor") + " " + getNumberOfCamerasBound(par1ItemStack.getTagCompound()) + "/30");
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

	public int getSlotFromPosition(NBTTagCompound nbt, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(nbt.hasKey("Camera" + i)){
				String[] coords = nbt.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return i;
			}

		return -1;
	}

	public boolean hasCameraAdded(NBTTagCompound nbt){
		if(nbt == null) return false;

		for(int i = 1; i <= 30; i++)
			if(nbt.hasKey("Camera" + i))
				return true;

		return false;
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