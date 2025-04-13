package net.geforcemods.securitycraft.entity.camera;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Side.CLIENT)
public class CameraController {
	public static int previousCameraType;
	//@formatter:off
	private static final ViewMovementKeyHandler[] MOVE_KEY_HANDLERS = {
			new ViewMovementKeyHandler(Minecraft.getMinecraft().gameSettings.keyBindForward, CameraController::moveViewUp),
			new ViewMovementKeyHandler(Minecraft.getMinecraft().gameSettings.keyBindBack, CameraController::moveViewDown),
			new ViewMovementKeyHandler(Minecraft.getMinecraft().gameSettings.keyBindLeft, cam -> moveViewHorizontally(cam, cam.rotationYaw - getMovementSpeed(cam) * cam.getZoomAmount())),
			new ViewMovementKeyHandler(Minecraft.getMinecraft().gameSettings.keyBindRight, cam -> moveViewHorizontally(cam, cam.rotationYaw + getMovementSpeed(cam) * cam.getZoomAmount()))
	};
	//@formatter:on
	private static int screenshotSoundCooldown = 0;

	private CameraController() {}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();

		if (renderViewEntity instanceof SecurityCamera) {
			SecurityCamera cam = (SecurityCamera) renderViewEntity;

			if (event.phase == Phase.END) {
				for (ViewMovementKeyHandler handler : MOVE_KEY_HANDLERS) {
					handler.tickEnd(cam);
				}

				if (KeyBindings.cameraZoomIn.isKeyDown())
					zoomIn(cam);
				else if (KeyBindings.cameraZoomOut.isKeyDown())
					zoomOut(cam);
				else
					cam.zooming = false;

				KeyBindings.cameraEmitRedstone.tick(cam);
				KeyBindings.cameraActivateNightVision.tick(cam);
				KeyBindings.setDefaultViewingDirection.tick(cam);
				screenshotSoundCooldown--;

				//update other players with the head rotation
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				double yRotChange = player.rotationYaw - player.lastReportedYaw;
				double xRotChange = player.rotationPitch - player.lastReportedPitch;

				if (yRotChange != 0.0D || xRotChange != 0.0D)
					player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));
			}
		}
	}

	public static void handleKeybinds() {
		GameSettings options = Minecraft.getMinecraft().gameSettings;

		for (ViewMovementKeyHandler handler : MOVE_KEY_HANDLERS) {
			handler.tickStart();
		}

		if (options.keyBindSneak.isKeyDown()) {
			dismount();
			KeyBinding.setKeyBindState(options.keyBindSneak.getKeyCode(), false);
		}
	}

	@SubscribeEvent
	public static void onScreenshot(ScreenshotEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (PlayerUtils.isPlayerMountedOnCamera(player) && screenshotSoundCooldown <= 0) {
			screenshotSoundCooldown = 7;
			Minecraft.getMinecraft().world.playSound(player.getPosition(), SCSounds.CAMERASNAP.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
		}
	}

	private static void dismount() {
		SecurityCraft.network.sendToServer(new DismountCamera());
	}

	public static void moveViewUp(SecurityCamera cam) {
		float next = cam.rotationPitch - getMovementSpeed(cam) * cam.getZoomAmount();

		if (cam.isCameraDown()) {
			if (next > 40F)
				cam.setRotation(cam.rotationYaw, next);
		}
		else if (next > -25F)
			cam.setRotation(cam.rotationYaw, next);
	}

	public static void moveViewDown(SecurityCamera cam) {
		float next = cam.rotationPitch + getMovementSpeed(cam) * cam.getZoomAmount();

		if (cam.isCameraDown()) {
			if (next < 90F)
				cam.setRotation(cam.rotationYaw, next);
		}
		else if (next < 60F)
			cam.setRotation(cam.rotationYaw, next);
	}

	public static void moveViewHorizontally(SecurityCamera cam, float next) {
		IBlockState state = cam.world.getBlockState(new BlockPos(cam.posX, cam.posY, cam.posZ));

		if (state.getProperties().containsKey(SecurityCameraBlock.FACING)) {
			float checkNext = next;

			if (checkNext < 0)
				checkNext += 360;

			boolean shouldSetRotation = false;

			switch (state.getValue(SecurityCameraBlock.FACING)) {
				case NORTH:
					shouldSetRotation = checkNext > 90F && checkNext < 270F;
					break;
				case SOUTH:
					shouldSetRotation = checkNext > 270F || checkNext < 90F;
					break;
				case EAST:
					shouldSetRotation = checkNext > 180F && checkNext < 360F;
					break;
				case WEST:
					shouldSetRotation = checkNext > 0F && checkNext < 180F;
					break;
				case DOWN:
					shouldSetRotation = true;
					break;
				default:
					shouldSetRotation = false;
					break;
			}

			if (shouldSetRotation)
				cam.rotationYaw = next;
		}
	}

	public static void zoomIn(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getMinecraft().world.playSound(new BlockPos(cam.posX, cam.posY, cam.posZ), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.setZoomAmount(Math.max(cam.getZoomAmount() - 0.1F, 0.1F));
	}

	public static void zoomOut(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getMinecraft().world.playSound(new BlockPos(cam.posX, cam.posY, cam.posZ), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.setZoomAmount(Math.min(cam.getZoomAmount() + 0.1F, 1.4F));
	}

	public static void toggleRedstone(SecurityCamera cam) {
		BlockPos pos = new BlockPos(cam.posX, cam.posY, cam.posZ);
		TileEntity be = cam.world.getTileEntity(pos);

		if (be instanceof IModuleInventory && ((IModuleInventory) be).isModuleEnabled(ModuleType.REDSTONE))
			SecurityCraft.network.sendToServer(new SetCameraPowered(pos, !cam.world.getBlockState(pos).getValue(SecurityCameraBlock.POWERED)));
	}

	public static void toggleNightVision(SecurityCamera cam) {
		SecurityCraft.network.sendToServer(new ToggleNightVision());
	}

	public static void setDefaultViewingDirection(SecurityCamera cam) {
		SecurityCraft.network.sendToServer(new SetDefaultCameraViewingDirection(cam));
	}

	public static float getMovementSpeed(SecurityCamera cam) {
		SecurityCameraBlockEntity be = cam.getBlockEntity();

		if (be != null)
			return (float) be.getMovementSpeed();

		return 0.0F;
	}

	public static class ViewMovementKeyHandler {
		private final KeyBinding key;
		private final Consumer<SecurityCamera> action;
		private boolean wasPressed;

		public ViewMovementKeyHandler(KeyBinding key, Consumer<SecurityCamera> action) {
			this.key = key;
			this.action = action;
		}

		public void tickStart() {
			wasPressed = key.isKeyDown();

			if (wasPressed)
				KeyBinding.setKeyBindState(key.getKeyCode(), false);
		}

		public void tickEnd(SecurityCamera cam) {
			if (wasPressed) {
				action.accept(cam);
				KeyBinding.setKeyBindState(key.getKeyCode(), true);
			}
		}
	}
}
