package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

public class CameraMonitorItem extends Item {
	public CameraMonitorItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		PlayerEntity player = ctx.getPlayer();

		if (level.getBlockState(pos).getBlock() == SCContent.SECURITY_CAMERA.get() && !PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) level.getBlockEntity(pos);

			if (!be.isOwnedBy(player) && !be.isAllowed(player)) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cannotView"), TextFormatting.RED);
				return ActionResultType.FAIL;
			}

			ItemStack stack = ctx.getItemInHand();

			if (stack.getTag() == null)
				stack.setTag(new CompoundNBT());

			GlobalPos view = GlobalPos.of(player.level.dimension(), pos);

			if (isCameraAdded(stack.getTag(), view)) {
				stack.getTag().remove(getTagNameFromPosition(stack.getTag(), view));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.unbound", Utils.getFormattedCoordinates(pos)), TextFormatting.RED);
				return ActionResultType.SUCCESS;
			}

			for (int i = 1; i <= 30; i++) {
				if (!stack.getTag().contains("Camera" + i)) {
					stack.getTag().putString("Camera" + i, LevelUtils.toNBTString(view));
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.bound", Utils.getFormattedCoordinates(pos)), TextFormatting.GREEN);
					break;
				}
			}

			if (!level.isClientSide)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new UpdateNBTTagOnClient(stack));

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!stack.hasTag()) {
			if (!hasCameraAdded(stack.getTag())) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), TextFormatting.RED);
				return ActionResult.pass(stack);
			}
		}
		else
			updateTagWithNames(stack, level);

		if (level.isClientSide && stack.getItem() == SCContent.CAMERA_MONITOR.get())
			ClientHandler.displayCameraMonitorScreen(player.inventory, (CameraMonitorItem) stack.getItem(), stack.getTag());

		return ActionResult.consume(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World level, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (stack.getTag() == null)
			return;

		tooltip.add(Utils.localize("tooltip.securitycraft:cameraMonitor", getNumberOfCamerasBound(stack.getTag()) + "/30").setStyle(Utils.GRAY_STYLE));
	}

	public static String getTagNameFromPosition(CompoundNBT tag, GlobalPos view) {
		for (int i = 1; i <= 30; i++) {
			if (tag.contains("Camera" + i)) {
				String[] coords = tag.getString("Camera" + i).split(" ");

				if (LevelUtils.checkCoordinates(view, coords))
					return "Camera" + i;
			}
		}

		return "";
	}

	public static boolean hasCameraAdded(CompoundNBT tag) {
		if (tag == null)
			return false;

		for (int i = 1; i <= 30; i++) {
			if (tag.contains("Camera" + i))
				return true;
		}

		return false;
	}

	public static boolean isCameraAdded(CompoundNBT tag, GlobalPos view) {
		for (int i = 1; i <= 30; i++) {
			if (tag.contains("Camera" + i)) {
				String[] coords = tag.getString("Camera" + i).split(" ");

				if (LevelUtils.checkCoordinates(view, coords))
					return true;
			}
		}

		return false;
	}

	public static List<Pair<GlobalPos, String>> getCameraPositions(CompoundNBT tag) {
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
				list.add(Pair.of(GlobalPos.of(coords.length == 4 ? RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(coords[3])) : World.OVERWORLD, pos), cameraName));
			}
			else
				list.add(Pair.of(null, null));
		}

		return list;
	}

	public static int getNumberOfCamerasBound(CompoundNBT tag) {
		if (tag == null)
			return 0;

		int amount = 0;

		for (int i = 1; i <= 30; i++) {
			if (tag.contains("Camera" + i))
				amount++;
		}

		return amount;
	}

	private void updateTagWithNames(ItemStack stack, World level) {
		if (!stack.hasTag())
			return;

		CompoundNBT tag = stack.getTag();

		for (int i = 1; i <= 30; i++) {
			String cameraString = tag.getString("Camera" + i);
			String[] globalPos = cameraString.split(" ");
			String nameKey = "camera" + i + "_name";

			if (globalPos.length == 3 || (globalPos.length == 4 && level.dimension().location().toString().equals(globalPos[3]))) {
				BlockPos camPos = new BlockPos(Integer.parseInt(globalPos[0]), Integer.parseInt(globalPos[1]), Integer.parseInt(globalPos[2]));

				if (level.isLoaded(camPos)) {
					TileEntity be = level.getBlockEntity(camPos);

					if (be instanceof SecurityCameraBlockEntity && ((SecurityCameraBlockEntity) be).hasCustomName()) {
						tag.putString(nameKey, ((SecurityCameraBlockEntity) be).getCustomName().getString());
						continue;
					}

					tag.remove(nameKey);
				}
			}
		}
	}
}