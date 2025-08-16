package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CameraMonitorItem extends Item {
	public CameraMonitorItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();

		if (level.getBlockState(pos).getBlock() == SCContent.SECURITY_CAMERA.get() && !PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) level.getBlockEntity(pos);

			if (!be.isOwnedBy(player) && !be.isAllowed(player)) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cannotView"), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}

			ItemStack stack = ctx.getItemInHand();

			if (stack.getTag() == null)
				stack.setTag(new CompoundTag());

			GlobalPos view = GlobalPos.of(player.level.dimension(), pos);

			if (isCameraAdded(stack.getTag(), view)) {
				stack.getTag().remove(getTagNameFromPosition(stack.getTag(), view));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.unbound", Utils.getFormattedCoordinates(pos)), ChatFormatting.RED);
				return InteractionResult.SUCCESS;
			}

			for (int i = 1; i <= 30; i++) {
				if (!stack.getTag().contains("Camera" + i)) {
					stack.getTag().putString("Camera" + i, LevelUtils.toNBTString(view));
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.bound", Utils.getFormattedCoordinates(pos)), ChatFormatting.GREEN);
					break;
				}
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!stack.hasTag()) {
			if (!hasCameraAdded(stack.getTag())) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), ChatFormatting.RED);
				return InteractionResultHolder.pass(stack);
			}
		}
		else
			updateTagWithNames(stack, level);

		if (level.isClientSide && stack.getItem() == SCContent.CAMERA_MONITOR.get())
			ClientHandler.displayCameraMonitorScreen(stack);

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		if (stack.getTag() == null)
			return;

		tooltip.add(Utils.localize("tooltip.securitycraft:cameraMonitor", getNumberOfCamerasBound(stack.getTag()) + "/30").setStyle(Utils.GRAY_STYLE));
	}

	public static String getTagNameFromPosition(CompoundTag tag, GlobalPos view) {
		for (int i = 1; i <= 30; i++) {
			if (tag.contains("Camera" + i)) {
				String[] coords = tag.getString("Camera" + i).split(" ");

				if (LevelUtils.checkCoordinates(view, coords))
					return "Camera" + i;
			}
		}

		return "";
	}

	public static boolean hasCameraAdded(CompoundTag tag) {
		if (tag == null)
			return false;

		for (int i = 1; i <= 30; i++) {
			if (tag.contains("Camera" + i))
				return true;
		}

		return false;
	}

	public static boolean isCameraAdded(CompoundTag tag, GlobalPos view) {
		for (int i = 1; i <= 30; i++) {
			if (tag.contains("Camera" + i)) {
				String[] coords = tag.getString("Camera" + i).split(" ");

				if (LevelUtils.checkCoordinates(view, coords))
					return true;
			}
		}

		return false;
	}

	public static void removeCameraOnClient(int camID, CompoundTag stackTag) {
		if (stackTag != null)
			stackTag.remove(CameraMonitorItem.getTagNameFromPosition(stackTag, CameraMonitorItem.getCameraPositions(stackTag).get(camID - 1).getLeft()));

		SecurityCraft.CHANNEL.sendToServer(new RemoveCameraTag(camID));
	}

	public static List<Pair<GlobalPos, String>> getCameraPositions(CompoundTag tag) {
		List<Pair<GlobalPos, String>> list = new ArrayList<>();

		for (int i = 1; i <= 30; i++) {
			if (tag != null && tag.contains("Camera" + i)) {
				String[] coords = tag.getString("Camera" + i).split(" ");
				BlockPos pos = new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
				String nameKey = "camera" + i + "_name";
				String cameraName = null;

				if (tag.contains(nameKey))
					cameraName = tag.getString(nameKey);

				//default to overworld if there is no dimension saved
				list.add(Pair.of(GlobalPos.of(coords.length == 4 ? ResourceKey.create(Registries.DIMENSION, new ResourceLocation(coords[3])) : Level.OVERWORLD, pos), cameraName));
			}
			else
				list.add(Pair.of(null, null));
		}

		return list;
	}

	public static int getNumberOfCamerasBound(CompoundTag tag) {
		if (tag == null)
			return 0;

		int amount = 0;

		for (int i = 1; i <= 30; i++) {
			if (tag.contains("Camera" + i))
				amount++;
		}

		return amount;
	}

	private void updateTagWithNames(ItemStack stack, Level level) {
		if (!stack.hasTag())
			return;

		CompoundTag tag = stack.getTag();

		for (int i = 1; i <= 30; i++) {
			String cameraString = tag.getString("Camera" + i);
			String[] globalPos = cameraString.split(" ");
			String nameKey = "camera" + i + "_name";

			if (globalPos.length == 3 || (globalPos.length == 4 && level.dimension().location().toString().equals(globalPos[3]))) {
				BlockPos camPos = new BlockPos(Integer.parseInt(globalPos[0]), Integer.parseInt(globalPos[1]), Integer.parseInt(globalPos[2]));

				if (level.isLoaded(camPos)) {
					BlockEntity be = level.getBlockEntity(camPos);

					if (be instanceof SecurityCameraBlockEntity camera && camera.hasCustomName()) {
						tag.putString(nameKey, camera.getCustomName().getString());
						continue;
					}

					tag.remove(nameKey);
				}
			}
		}
	}
}