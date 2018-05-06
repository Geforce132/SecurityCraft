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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
			//When the mod is using the LookingGlass system.
			if(SecurityCraft.instance.useLookingGlass()){
				if(world.getBlock(x, y, z) instanceof BlockSecurityCamera){
					if(!((TileEntitySecurityCamera) world.getTileEntity(x, y, z)).getOwner().isOwner(player)){
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.cannotView"), EnumChatFormatting.RED);
						return false;
					}

					if(stack.getTagCompound() == null)
						stack.setTagCompound(new NBTTagCompound());

					CameraView view = new CameraView(x, y, z, player.dimension);

					if(isCameraAdded(stack.getTagCompound(), view)){
						stack.getTagCompound().removeTag(getTagNameFromPosition(stack.getTagCompound(), view));
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(view.x, view.y, view.z)), EnumChatFormatting.RED);
						return true;
					}

					stack.getTagCompound().setString("Camera1", view.toNBTString());
					SecurityCraft.network.sendTo(new PacketCCreateLGView(view.x, view.y, view.z, view.dimension), (EntityPlayerMP) player);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(view.x, view.y, view.z)), EnumChatFormatting.GREEN);

					return true;
				}else if(world.getBlock(x, y, z) == SCContent.frame){
					if(!stack.hasTagCompound() || !hasCameraAdded(stack.getTagCompound())) return false;

					CameraView view = getCameraView(stack.getTagCompound());
					if(view == null) return true;

					((TileEntityFrame) world.getTileEntity(x, y, z)).setCameraLocation(view.x, view.y, view.z, view.dimension);
					SecurityCraft.network.sendToAll(new PacketCSetCameraLocation(x, y, z, view.x, view.y, view.z, view.dimension));
					stack.stackSize--;

					return true;
				}else{
					if(!stack.hasTagCompound() || !hasCameraAdded(stack.getTagCompound())) return false;

					CameraView view = getCameraView(stack.getTagCompound());
					if(view == null) return true;

					if(!(world.getBlock(view.x, view.y, view.z) instanceof BlockSecurityCamera)){
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.noCamera").replace("#", Utils.getFormattedCoordinates(view.x, view.y, view.z)), EnumChatFormatting.RED);
						return false;
					}

					if(SecurityCraft.instance.useLookingGlass())
						SecurityCraft.network.sendTo(new PacketCCreateLGView(view.x, view.y, view.z, view.dimension), (EntityPlayerMP) player);
					else
						player.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, world, x, y, z);

					return false;
				}
			}
			else if(world.getBlock(x, y, z) == SCContent.securityCamera){
				if(!((IOwnable) world.getTileEntity(x, y, z)).getOwner().isOwner(player)){
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.cannotView"), EnumChatFormatting.RED);
					return true;
				}

				if(player.getCurrentEquippedItem().getTagCompound() == null)
					player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());

				CameraView view = new CameraView(x, y, z, player.dimension);

				if(isCameraAdded(player.getCurrentEquippedItem().getTagCompound(), view)){
					player.getCurrentEquippedItem().getTagCompound().removeTag(getTagNameFromPosition(player.getCurrentEquippedItem().getTagCompound(), view));
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(x, y, z)), EnumChatFormatting.RED);
					return true;
				}

				for(int i = 1; i <= 30; i++)
					if (!player.getCurrentEquippedItem().getTagCompound().hasKey("Camera" + i)){
						player.getCurrentEquippedItem().getTagCompound().setString("Camera" + i, view.toNBTString());
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(x, y, z)), EnumChatFormatting.GREEN);
						break;
					}

				SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP)player);

				return true;
			}

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		if(!world.isRemote){
			if(!stack.hasTagCompound() || !hasCameraAdded(stack.getTagCompound())){
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.rightclickToView"), EnumChatFormatting.RED);
				return stack;
			}

			if(SecurityCraft.instance.useLookingGlass()){
				CameraView view = getCameraView(stack.getTagCompound());

				if(!(world.getBlock(view.x, view.y, view.z) instanceof BlockSecurityCamera)){
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.cameraMonitor.name"), StatCollector.translateToLocal("messages.cameraMonitor.noCamera").replace("#", Utils.getFormattedCoordinates(view.x, view.y, view.z)), EnumChatFormatting.RED);
					return stack;
				}

				SecurityCraft.network.sendTo(new PacketCCreateLGView(view.x, view.y, view.z, view.dimension), (EntityPlayerMP) player);
			}
			else
				player.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean update) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if(stack.getTagCompound() == null)
			return;

		list.add(StatCollector.translateToLocal("tooltip.cameraMonitor") + " " + getNumberOfCamerasBound(stack.getTagCompound()) + "/30");
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
