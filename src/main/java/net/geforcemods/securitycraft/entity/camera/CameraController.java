package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Side.CLIENT)
public class CameraController {
	public static int previousCameraType;
	private static boolean wasUpPressed;
	private static boolean wasDownPressed;
	private static boolean wasLeftPressed;
	private static boolean wasRightPressed;
	private static int setDefaultViewingDirectionCooldown = 0;

	private CameraController() {}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();

		if (renderViewEntity instanceof SecurityCamera) {
			SecurityCamera cam = (SecurityCamera) renderViewEntity;
			GameSettings options = Minecraft.getMinecraft().gameSettings;

			if (event.phase == Phase.END) {
				if (wasUpPressed) {
					moveViewUp(cam);
					KeyBinding.setKeyBindState(options.keyBindForward.getKeyCode(), true);
				}

				if (wasDownPressed) {
					moveViewDown(cam);
					KeyBinding.setKeyBindState(options.keyBindBack.getKeyCode(), true);
				}

				if (wasLeftPressed) {
					moveViewHorizontally(cam, cam.rotationYaw - ConfigHandler.cameraSpeed * cam.zoomAmount);
					KeyBinding.setKeyBindState(options.keyBindLeft.getKeyCode(), true);
				}

				if (wasRightPressed) {
					moveViewHorizontally(cam, cam.rotationYaw + ConfigHandler.cameraSpeed * cam.zoomAmount);
					KeyBinding.setKeyBindState(options.keyBindRight.getKeyCode(), true);
				}

				if (KeyBindings.cameraZoomIn.isKeyDown())
					zoomIn(cam);
				else if (KeyBindings.cameraZoomOut.isKeyDown())
					zoomOut(cam);
				else
					cam.zooming = false;

				if (KeyBindings.cameraEmitRedstone.isPressed())
					emitRedstone(cam);

				if (KeyBindings.cameraActivateNightVision.isPressed())
					giveNightVision(cam);

				if (setDefaultViewingDirectionCooldown-- <= 0 && KeyBindings.setDefaultViewingDirection.isPressed()) {
					setDefaultViewingDirection(cam);
					setDefaultViewingDirectionCooldown = 20;
				}

				//update other players with the head rotation
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				double yRotChange = player.rotationYaw - player.lastReportedYaw;
				double xRotChange = player.rotationPitch - player.lastReportedPitch;

				if (yRotChange != 0.0D || xRotChange != 0.0D) {
					player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));
				}
			}
		}
	}

	public static void handleKeybinds() {
		GameSettings options = Minecraft.getMinecraft().gameSettings;

		if (wasUpPressed = options.keyBindForward.isKeyDown())
			KeyBinding.setKeyBindState(options.keyBindForward.getKeyCode(), false);

		if (wasDownPressed = options.keyBindBack.isKeyDown())
			KeyBinding.setKeyBindState(options.keyBindBack.getKeyCode(), false);

		if (wasLeftPressed = options.keyBindLeft.isKeyDown())
			KeyBinding.setKeyBindState(options.keyBindLeft.getKeyCode(), false);

		if (wasRightPressed = options.keyBindRight.isKeyDown())
			KeyBinding.setKeyBindState(options.keyBindRight.getKeyCode(), false);

		if (options.keyBindSneak.isKeyDown()) {
			dismount();
			KeyBinding.setKeyBindState(options.keyBindSneak.getKeyCode(), false);
		}
	}

	private static void dismount() {
		SecurityCraft.network.sendToServer(new DismountCamera());
	}

	public static void moveViewUp(SecurityCamera cam) {
		float next = cam.rotationPitch - ConfigHandler.cameraSpeed * cam.zoomAmount;

		if (cam.isCameraDown()) {
			if (next > 40F)
				cam.setRotation(cam.rotationYaw, next);
		}
		else if (next > -25F)
			cam.setRotation(cam.rotationYaw, next);
	}

	public static void moveViewDown(SecurityCamera cam) {
		float next = cam.rotationPitch + ConfigHandler.cameraSpeed * cam.zoomAmount;

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
		cam.zoomAmount = Math.max(cam.zoomAmount - 0.1F, 0.1F);
	}

	public static void zoomOut(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getMinecraft().world.playSound(new BlockPos(cam.posX, cam.posY, cam.posZ), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.min(cam.zoomAmount + 0.1F, 1.4F);
	}

	public static void emitRedstone(SecurityCamera cam) {
		if (cam.redstoneCooldown == 0) {
			cam.toggleRedstonePowerFromClient();
			cam.redstoneCooldown = 30;
		}
	}

	public static void giveNightVision(SecurityCamera cam) {
		if (cam.toggleNightVisionCooldown == 0)
			cam.toggleNightVisionFromClient();
	}

	public static void setDefaultViewingDirection(SecurityCamera cam) {
		SecurityCraft.network.sendToServer(new SetDefaultCameraViewingDirection(cam));
	}
}
