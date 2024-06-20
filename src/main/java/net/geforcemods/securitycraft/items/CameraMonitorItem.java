package net.geforcemods.securitycraft.items;

import java.util.List;
import java.util.Objects;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class CameraMonitorItem extends Item {
	public static final int MAX_CAMERAS = 30;

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
			GlobalPos view = GlobalPos.of(player.level().dimension(), pos);
			GlobalPositions cameras = stack.get(SCContent.BOUND_CAMERAS);

			if (cameras != null) {
				if (cameras.remove(SCContent.BOUND_CAMERAS, stack, view))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.unbound", Utils.getFormattedCoordinates(pos)), ChatFormatting.RED);
				else if (cameras.add(SCContent.BOUND_CAMERAS, stack, view))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.bound", Utils.getFormattedCoordinates(pos)), ChatFormatting.GREEN);

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		GlobalPositions cameras = stack.get(SCContent.BOUND_CAMERAS);

		if (cameras != null && cameras.isEmpty()) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), ChatFormatting.RED);
			return InteractionResultHolder.pass(stack);
		}

		if (level.isClientSide && stack.getItem() == SCContent.CAMERA_MONITOR.get())
			ClientHandler.displayCameraMonitorScreen(stack);

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
		GlobalPositions cameras = stack.get(SCContent.BOUND_CAMERAS);

		if (cameras != null)
			tooltip.add(Utils.localize("tooltip.securitycraft:cameraMonitor", cameras.positions().stream().filter(Objects::nonNull).count() + "/" + MAX_CAMERAS).setStyle(Utils.GRAY_STYLE));
	}

	public static void removeCameraOnClient(GlobalPos camera, ItemStack monitor) {
		if (monitor.has(SCContent.BOUND_CAMERAS)) {
			monitor.get(SCContent.BOUND_CAMERAS).remove(SCContent.BOUND_CAMERAS, monitor, camera);
			PacketDistributor.sendToServer(new RemoveCameraTag(camera));
		}
	}
}