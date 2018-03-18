package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.network.packets.PacketCCreateLGView;
import net.geforcemods.securitycraft.network.packets.PacketCRemoveLGView;
import net.geforcemods.securitycraft.network.packets.PacketCSetCameraLocation;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCameraMonitor extends Item {

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = playerIn.getHeldItem(hand);

		if(!worldIn.isRemote){
			if(SecurityCraft.config.useLookingGlass()){
				if(BlockUtils.getBlock(worldIn, pos) instanceof BlockSecurityCamera){
					if(!((TileEntitySecurityCamera) worldIn.getTileEntity(pos)).getOwner().isOwner(playerIn)){
						PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.cannotView"), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(stack.getTagCompound() == null)
						stack.setTagCompound(new NBTTagCompound());

					CameraView view = new CameraView(pos, playerIn.dimension);

					if(isCameraAdded(stack.getTagCompound(), view)){
						stack.getTagCompound().removeTag(getTagNameFromPosition(stack.getTagCompound(), view));

						if(SecurityCraft.config.useLookingGlass())
							SecurityCraft.network.sendToAll(new PacketCRemoveLGView(pos, worldIn.provider.getDimension()));

						PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(view.getLocation())), TextFormatting.RED);
						return EnumActionResult.SUCCESS;
					}

					stack.getTagCompound().setString("Camera1", view.toNBTString());
					SecurityCraft.network.sendTo(new PacketCCreateLGView(view.getLocation(), view.dimension), (EntityPlayerMP) playerIn);
					PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(view.getLocation())), TextFormatting.GREEN);

					return EnumActionResult.SUCCESS;
				}else if(BlockUtils.getBlock(worldIn, pos) == SCContent.frame){
					if(!stack.hasTagCompound() || !hasCameraAdded(stack.getTagCompound())) return EnumActionResult.FAIL;

					CameraView view = getCameraView(stack.getTagCompound());
					if(view == null) return EnumActionResult.SUCCESS;

					((TileEntityFrame) worldIn.getTileEntity(pos)).setCameraLocation(view.getLocation(), view.dimension);
					SecurityCraft.network.sendToAll(new PacketCSetCameraLocation(pos, view.getLocation(), view.dimension));
					stack.setCount(stack.getCount() - 1);

					return EnumActionResult.SUCCESS;
				}else{
					if(!stack.hasTagCompound() || !hasCameraAdded(stack.getTagCompound())) return EnumActionResult.FAIL;

					CameraView view = getCameraView(stack.getTagCompound());
					if(view == null) return EnumActionResult.SUCCESS;

					if(!(BlockUtils.getBlock(worldIn, view.getLocation()) instanceof BlockSecurityCamera)){
						PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.noCamera").replace("#", Utils.getFormattedCoordinates(view.getLocation())), TextFormatting.RED);
						return EnumActionResult.FAIL;
					}

					if(SecurityCraft.config.useLookingGlass())
						SecurityCraft.network.sendTo(new PacketCCreateLGView(view.getLocation(), view.dimension), (EntityPlayerMP) playerIn);
					else
						playerIn.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());

					return EnumActionResult.FAIL;
				}
			}
			else if(BlockUtils.getBlock(worldIn, pos) == SCContent.securityCamera){
				if(!((IOwnable) worldIn.getTileEntity(pos)).getOwner().isOwner(playerIn)){
					PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.cannotView"), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				if(playerIn.inventory.getCurrentItem().getTagCompound() == null)
					playerIn.inventory.getCurrentItem().setTagCompound(new NBTTagCompound());

				CameraView view = new CameraView(pos, playerIn.dimension);

				if(isCameraAdded(playerIn.inventory.getCurrentItem().getTagCompound(), view)){
					playerIn.inventory.getCurrentItem().getTagCompound().removeTag(getTagNameFromPosition(playerIn.inventory.getCurrentItem().getTagCompound(), view));
					PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.unbound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				for(int i = 1; i <= 30; i++)
					if (!playerIn.inventory.getCurrentItem().getTagCompound().hasKey("Camera" + i)){
						playerIn.inventory.getCurrentItem().getTagCompound().setString("Camera" + i, view.toNBTString());
						PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.bound").replace("#", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
						break;
					}

				SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(stack), (EntityPlayerMP)playerIn);

				return EnumActionResult.SUCCESS;
			}
		}else if(worldIn.isRemote && BlockUtils.getBlock(worldIn, pos) != SCContent.securityCamera && BlockUtils.getBlock(worldIn, pos) != SCContent.frame){
			if(playerIn.getRidingEntity() != null && playerIn.getRidingEntity() instanceof EntitySecurityCamera)
				return EnumActionResult.SUCCESS;

			if(stack.getTagCompound() == null || stack.getTagCompound().hasNoTags()) {
				PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.rightclickToView"), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			playerIn.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.SUCCESS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);

		if (!worldIn.isRemote) {
			if(playerIn.getRidingEntity() != null && playerIn.getRidingEntity() instanceof EntitySecurityCamera)
				return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);

			if(!itemStackIn.hasTagCompound() || !hasCameraAdded(itemStackIn.getTagCompound())) {
				PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.rightclickToView"), TextFormatting.RED);
				return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
			}

			if(SecurityCraft.config.useLookingGlass()){
				CameraView view = getCameraView(itemStackIn.getTagCompound());

				if(!(BlockUtils.getBlock(worldIn, view.getLocation()) instanceof BlockSecurityCamera)){
					PlayerUtils.sendMessageToPlayer(playerIn, ClientUtils.localize("item.cameraMonitor.name"), ClientUtils.localize("messages.cameraMonitor.noCamera").replace("#", Utils.getFormattedCoordinates(view.getLocation())), TextFormatting.RED);
					return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
				}

				SecurityCraft.network.sendTo(new PacketCCreateLGView(view.getLocation(), view.dimension), (EntityPlayerMP) playerIn);
			}
			else
				playerIn.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		}

		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(stack.getTagCompound() == null)
			return;

		tooltip.add(ClientUtils.localize("tooltip.cameraMonitor") + " " + getNumberOfCamerasBound(stack.getTagCompound()) + "/30");
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