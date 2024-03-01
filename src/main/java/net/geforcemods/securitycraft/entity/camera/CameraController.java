package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.SectionPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class CameraController {
	public static PointOfView previousCameraType;
	private static ClientChunkProvider.ChunkArray cameraStorage;
	private static boolean wasUpPressed;
	private static boolean wasDownPressed;
	private static boolean wasLeftPressed;
	private static boolean wasRightPressed;
	private static int setDefaultViewingDirectionCooldown = 0;

	private CameraController() {}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;

		if (cameraEntity instanceof SecurityCamera) {
			SecurityCamera cam = (SecurityCamera) cameraEntity;
			GameSettings options = Minecraft.getInstance().options;

			//up/down/left/right handling is split to prevent players who are viewing a camera from moving around in a boat or on a horse
			if (event.phase == Phase.START) {
				if (wasUpPressed = options.keyUp.isDown())
					options.keyUp.setDown(false);

				if (wasDownPressed = options.keyDown.isDown())
					options.keyDown.setDown(false);

				if (wasLeftPressed = options.keyLeft.isDown())
					options.keyLeft.setDown(false);

				if (wasRightPressed = options.keyRight.isDown())
					options.keyRight.setDown(false);

				if (options.keyShift.isDown()) {
					dismount();
					options.keyShift.setDown(false);
				}
			}
			else if (event.phase == Phase.END) {
				if (wasUpPressed) {
					moveViewUp(cam);
					options.keyUp.setDown(true);
				}

				if (wasDownPressed) {
					moveViewDown(cam);
					options.keyDown.setDown(true);
				}

				if (wasLeftPressed) {
					moveViewHorizontally(cam, cam.yRot - (float) cam.cameraSpeed * cam.zoomAmount);
					options.keyLeft.setDown(true);
				}

				if (wasRightPressed) {
					moveViewHorizontally(cam, cam.yRot + (float) cam.cameraSpeed * cam.zoomAmount);
					options.keyRight.setDown(true);
				}

				if (KeyBindings.cameraZoomIn.isDown())
					zoomIn(cam);
				else if (KeyBindings.cameraZoomOut.isDown())
					zoomOut(cam);
				else
					cam.zooming = false;

				if (KeyBindings.cameraEmitRedstone.consumeClick())
					emitRedstone(cam);

				if (KeyBindings.cameraActivateNightVision.consumeClick())
					giveNightVision(cam);

				if (setDefaultViewingDirectionCooldown-- <= 0 && KeyBindings.setDefaultViewingDirection.consumeClick()) {
					setDefaultViewingDirection(cam);
					setDefaultViewingDirectionCooldown = 20;
				}

				//update other players with the head rotation
				ClientPlayerEntity player = Minecraft.getInstance().player;
				double yRotChange = player.yRot - player.yRotLast;
				double xRotChange = player.xRot - player.xRotLast;

				if (yRotChange != 0.0D || xRotChange != 0.0D)
					player.connection.send(new CPlayerPacket.RotationPacket(player.yRot, player.xRot, player.isOnGround()));
			}
		}
	}

	private static void dismount() {
		SecurityCraft.channel.sendToServer(new DismountCamera());
	}

	public static void moveViewUp(SecurityCamera cam) {
		float next = cam.xRot - (float) cam.cameraSpeed * cam.zoomAmount;

		if (cam.isCameraDown()) {
			if (next > 40F)
				cam.setRotation(cam.yRot, next);
		}
		else if (next > -25F)
			cam.setRotation(cam.yRot, next);
	}

	public static void moveViewDown(SecurityCamera cam) {
		float next = cam.xRot + (float) cam.cameraSpeed * cam.zoomAmount;

		if (cam.isCameraDown()) {
			if (next < 90F)
				cam.setRotation(cam.yRot, next);
		}
		else if (next < 60F)
			cam.setRotation(cam.yRot, next);
	}

	public static void moveViewHorizontally(SecurityCamera cam, float next) {
		BlockState state = cam.level.getBlockState(cam.blockPosition());

		if (state.hasProperty(SecurityCameraBlock.FACING)) {
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
				cam.yRot = next;
		}
	}

	public static void zoomIn(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.max(cam.zoomAmount - 0.1F, 0.1F);
	}

	public static void zoomOut(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

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
		SecurityCraft.channel.sendToServer(new SetDefaultCameraViewingDirection(cam));
	}

	public static ClientChunkProvider.ChunkArray getCameraStorage() {
		return cameraStorage;
	}

	public static void setCameraStorage(ClientChunkProvider.ChunkArray newStorage) {
		cameraStorage = newStorage;
	}

	public static void setRenderPosition(Entity entity) {
		if (entity instanceof SecurityCamera) {
			SectionPos cameraPos = SectionPos.of(entity);

			cameraStorage.viewCenterX = cameraPos.x();
			cameraStorage.viewCenterZ = cameraPos.z();
		}
	}
}
