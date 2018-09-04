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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote){
			if(BlockUtils.getBlock(world, pos) == SCContent.securityCamera){
				if(!((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player)){
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:cameraMonitor.name"), StatCollector.translateToLocal("messages.securitycraft:cameraMonitor.cannotView"), EnumChatFormatting.RED);
					return true;
				}

				if(player.getCurrentEquippedItem().getTagCompound() == null)
					player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());

				CameraView view = new CameraView(pos, player.dimension);

				if(isCameraAdded(player.getCurrentEquippedItem().getTagCompound(), view)){
					player.getCurrentEquippedItem().getTagCompound().removeTag(getTagNameFromPosition(player.getCurrentEquippedItem().getTagCompound(), view));
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:cameraMonitor.name"), StatCollector.translateToLocal("messages.securitycraft:cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(pos)), EnumChatFormatting.RED);
					return true;
				}

				for(int i = 1; i <= 30; i++)
					if (!player.getCurrentEquippedItem().getTagCompound().hasKey("Camera" + i)){
						player.getCurrentEquippedItem().getTagCompound().setString("Camera" + i, view.toNBTString());
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:cameraMonitor.name"), StatCollector.translateToLocal("messages.securitycraft:cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(pos)), EnumChatFormatting.GREEN);
						break;
					}

				SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP)player);

				return true;
			}
		}else if(world.isRemote && BlockUtils.getBlock(world, pos) != SCContent.securityCamera){
			if(player.ridingEntity != null && player.ridingEntity instanceof EntitySecurityCamera) return true;

			if(stack.getTagCompound() == null || stack.getTagCompound().hasNoTags()) {
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:cameraMonitor.name"), StatCollector.translateToLocal("messages.securitycraft:cameraMonitor.rightclickToView"), EnumChatFormatting.RED);
				return true;
			}

			player.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			if(player.ridingEntity != null && player.ridingEntity instanceof EntitySecurityCamera) return stack;

			if(!stack.hasTagCompound() || !hasCameraAdded(stack.getTagCompound())) {
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:cameraMonitor.name"), StatCollector.translateToLocal("messages.securitycraft:cameraMonitor.rightclickToView"), EnumChatFormatting.RED);
				return stack;
			}

			player.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
		if(stack.getTagCompound() == null)
			return;

		list.add(StatCollector.translateToLocal("tooltip.securitycraft:cameraMonitor") + " " + getNumberOfCamerasBound(stack.getTagCompound()) + "/30");
	}

	public static String getTagNameFromPosition(NBTTagCompound tag, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(tag.hasKey("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return "Camera" + i;
			}

		return "";
	}

	public int getSlotFromPosition(NBTTagCompound tag, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(tag.hasKey("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return i;
			}

		return -1;
	}

	public boolean hasCameraAdded(NBTTagCompound tag){
		if(tag == null) return false;

		for(int i = 1; i <= 30; i++)
			if(tag.hasKey("Camera" + i))
				return true;

		return false;
	}

	public boolean isCameraAdded(NBTTagCompound tag, CameraView view){
		for(int i = 1; i <= 30; i++)
			if(tag.hasKey("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return true;
			}

		return false;
	}

	public ArrayList<CameraView> getCameraPositions(NBTTagCompound tag){
		ArrayList<CameraView> list = new ArrayList<CameraView>();

		for(int i = 1; i <= 30; i++)
			if(tag != null && tag.hasKey("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				list.add(new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? Integer.parseInt(coords[3]) : 0)));
			}
			else
				list.add(null);

		return list;
	}

	public int getNumberOfCamerasBound(NBTTagCompound stack) {
		if(stack == null) return 0;

		for(int i = 1; i <= 31; i++)
			if(stack.hasKey("Camera" + i))
				continue;
			else
				return i - 1;

		return 0;
	}

}