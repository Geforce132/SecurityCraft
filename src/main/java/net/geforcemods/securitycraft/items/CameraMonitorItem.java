package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.misc.LinkingStateItemPropertyHandler;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CameraMonitorItem extends Item {
	public CameraMonitorItem() {
		addPropertyOverride(LinkingStateItemPropertyHandler.LINKING_STATE_PROPERTY, LinkingStateItemPropertyHandler::cameraMonitor);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.getBlockState(pos).getBlock() == SCContent.securityCamera && !PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCameraBlockEntity te = (SecurityCameraBlockEntity) world.getTileEntity(pos);

			if (!te.isOwnedBy(player) && !te.isAllowed(player)) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:cameraMonitor.name"), Utils.localize("messages.securitycraft:cameraMonitor.cannotView"), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			if (stack.getTagCompound() == null)
				stack.setTagCompound(new NBTTagCompound());

			CameraView view = new CameraView(pos, player.dimension);

			if (isCameraAdded(stack.getTagCompound(), view)) {
				stack.getTagCompound().removeTag(getTagNameFromPosition(stack.getTagCompound(), view));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:cameraMonitor.name"), Utils.localize("messages.securitycraft:cameraMonitor.unbound", pos), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			for (int i = 1; i <= 30; i++) {
				if (!stack.getTagCompound().hasKey("Camera" + i)) {
					stack.getTagCompound().setString("Camera" + i, view.toNBTString());
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:cameraMonitor.name"), Utils.localize("messages.securitycraft:cameraMonitor.bound", pos), TextFormatting.GREEN);
					break;
				}
			}

			if (!world.isRemote)
				SecurityCraft.network.sendTo(new UpdateNBTTagOnClient(stack), (EntityPlayerMP) player);

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!stack.hasTagCompound()) {
			if (!hasCameraAdded(stack.getTagCompound())) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:cameraMonitor.name"), Utils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), TextFormatting.RED);
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}
		}
		else
			updateTagWithNames(stack, world);

		player.openGui(SecurityCraft.instance, Screens.CAMERA_MONITOR.ordinal(), world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		if (stack.getTagCompound() == null)
			return;

		tooltip.add(Utils.localize("tooltip.securitycraft:cameraMonitor", getNumberOfCamerasBound(stack.getTagCompound()) + "/30").setStyle(Utils.GRAY_STYLE).getFormattedText());
	}

	public static String getTagNameFromPosition(NBTTagCompound tag, CameraView view) {
		for (int i = 1; i <= 30; i++) {
			if (tag.hasKey("Camera" + i)) {
				String[] coords = tag.getString("Camera" + i).split(" ");

				if (view.checkCoordinates(coords))
					return "Camera" + i;
			}
		}

		return "";
	}

	public static boolean hasCameraAdded(NBTTagCompound tag) {
		if (tag == null)
			return false;

		for (int i = 1; i <= 30; i++) {
			if (tag.hasKey("Camera" + i))
				return true;
		}

		return false;
	}

	public static boolean isCameraAdded(NBTTagCompound tag, CameraView view) {
		for (int i = 1; i <= 30; i++) {
			if (tag.hasKey("Camera" + i)) {
				String[] coords = tag.getString("Camera" + i).split(" ");

				if (view.checkCoordinates(coords))
					return true;
			}
		}

		return false;
	}

	public static List<Pair<CameraView, String>> getCameraPositions(NBTTagCompound tag) {
		List<Pair<CameraView, String>> list = new ArrayList<>();

		for (int i = 1; i <= 30; i++) {
			if (tag != null && tag.hasKey("Camera" + i)) {
				String[] coords = tag.getString("Camera" + i).split(" ");
				String nameKey = "camera" + i + "_name";
				String cameraName = null;

				if (tag.hasKey(nameKey))
					cameraName = tag.getString(nameKey);

				list.add(Pair.of(new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? Integer.parseInt(coords[3]) : 0)), cameraName));
			}
			else
				list.add(Pair.of(null, null));
		}

		return list;
	}

	public static int getNumberOfCamerasBound(NBTTagCompound tag) {
		if (tag == null)
			return 0;

		int amount = 0;

		for (int i = 1; i <= 31; i++) {
			if (tag.hasKey("Camera" + i))
				amount++;
		}

		return amount;
	}

	private void updateTagWithNames(ItemStack stack, World level) {
		if (!stack.hasTagCompound())
			return;

		NBTTagCompound tag = stack.getTagCompound();

		for (int i = 1; i <= 30; i++) {
			String cameraString = tag.getString("Camera" + i);
			String[] globalPos = cameraString.split(" ");
			String nameKey = "camera" + i + "_name";

			if (globalPos.length == 3 || (globalPos.length == 4 && level.provider.getDimension() == Integer.parseInt(globalPos[3]))) {
				BlockPos camPos = new BlockPos(Integer.parseInt(globalPos[0]), Integer.parseInt(globalPos[1]), Integer.parseInt(globalPos[2]));

				if (level.isBlockLoaded(camPos)) {
					TileEntity be = level.getTileEntity(camPos);

					if (be instanceof SecurityCameraBlockEntity && ((SecurityCameraBlockEntity) be).hasCustomName()) {
						tag.setString(nameKey, ((SecurityCameraBlockEntity) be).getName());
						continue;
					}

					tag.removeTag(nameKey);
				}
			}
		}
	}
}