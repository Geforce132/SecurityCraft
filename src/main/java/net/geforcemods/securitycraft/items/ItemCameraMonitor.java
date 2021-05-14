package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
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
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = player.getHeldItem(hand);

		if(BlockUtils.getBlock(world, pos) == SCContent.securityCamera && !PlayerUtils.isPlayerMountedOnCamera(player)){
			if(!((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player) && !((TileEntitySecurityCamera)world.getTileEntity(pos)).hasModule(EnumModuleType.SMART)){
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:cameraMonitor.name"), Utils.localize("messages.securitycraft:cameraMonitor.cannotView"), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			if(stack.getTagCompound() == null)
				stack.setTagCompound(new NBTTagCompound());

			CameraView view = new CameraView(pos, player.dimension);

			if(isCameraAdded(stack.getTagCompound(), view)){
				stack.getTagCompound().removeTag(getTagNameFromPosition(stack.getTagCompound(), view));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:cameraMonitor.name"), Utils.localize("messages.securitycraft:cameraMonitor.unbound", pos), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			for(int i = 1; i <= 30; i++)
				if (!stack.getTagCompound().hasKey("Camera" + i)){
					stack.getTagCompound().setString("Camera" + i, view.toNBTString());
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:cameraMonitor.name"), Utils.localize("messages.securitycraft:cameraMonitor.bound", pos), TextFormatting.GREEN);
					break;
				}

			if (!world.isRemote)
				SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP)player);

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(!stack.hasTagCompound() || !hasCameraAdded(stack.getTagCompound())) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:cameraMonitor.name"), Utils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), TextFormatting.RED);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}

		player.openGui(SecurityCraft.instance, GuiHandler.CAMERA_MONITOR_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		if(stack.getTagCompound() == null)
			return;

		tooltip.add(Utils.localize("tooltip.securitycraft:cameraMonitor").getFormattedText() + " " + getNumberOfCamerasBound(stack.getTagCompound()) + "/30");
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
		ArrayList<CameraView> list = new ArrayList<>();

		for(int i = 1; i <= 30; i++)
			if(tag != null && tag.hasKey("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				list.add(new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? Integer.parseInt(coords[3]) : 0)));
			}
			else
				list.add(null);

		return list;
	}

	public int getNumberOfCamerasBound(NBTTagCompound tag) {
		if(tag == null) return 0;

		int amount = 0;

		for(int i = 1; i <= 31; i++)
		{
			if(tag.hasKey("Camera" + i))
				amount++;
		}

		return amount;
	}

}