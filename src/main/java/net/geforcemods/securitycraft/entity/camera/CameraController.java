package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class CameraController {
	public static int previousCameraType;
	private static ClientChunkProvider.ChunkArray cameraStorage;
	private static boolean wasUpPressed;
	private static boolean wasDownPressed;
	private static boolean wasLeftPressed;
	private static boolean wasRightPressed;

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		Entity renderViewEntity = Minecraft.getInstance().cameraEntity;

		if (renderViewEntity instanceof SecurityCamera) {
			SecurityCamera cam = (SecurityCamera) renderViewEntity;
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
					moveViewHorizontally(cam, cam.yRot, cam.yRot - (float) cam.cameraSpeed * cam.zoomAmount);
					options.keyLeft.setDown(true);
				}

				if (wasRightPressed) {
					moveViewHorizontally(cam, cam.yRot, cam.yRot + (float) cam.cameraSpeed * cam.zoomAmount);
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

				//update other players with the head rotation
				ClientPlayerEntity player = Minecraft.getInstance().player;
				double yRotChange = player.yRot - player.yRotLast;
				double xRotChange = player.xRot - player.xRotLast;

				if (yRotChange != 0.0D || xRotChange != 0.0D)
					player.connection.send(new CPlayerPacket.RotationPacket(player.yRot, player.xRot, player.onGround));
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
				cam.setRot(cam.yRot, next);
		}
		else if (next > -25F)
			cam.setRot(cam.yRot, next);
	}

	public static void moveViewDown(SecurityCamera cam) {
		float next = cam.xRot + (float) cam.cameraSpeed * cam.zoomAmount;

		if (cam.isCameraDown()) {
			if (next < 90F)
				cam.setRot(cam.yRot, next);
		}
		else if (next < 60F)
			cam.setRot(cam.yRot, next);
	}

	public static void moveViewHorizontally(SecurityCamera cam, float yRot, float next) {
		BlockState state = cam.level.getBlockState(cam.getCommandSenderBlockPosition());

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
			Minecraft.getInstance().level.playLocalSound(cam.getCommandSenderBlockPosition(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.max(cam.zoomAmount - 0.1F, 0.1F);
	}

	public static void zoomOut(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getInstance().level.playLocalSound(cam.getCommandSenderBlockPosition(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.min(cam.zoomAmount + 0.1F, 1.4F);
	}

	public static void emitRedstone(SecurityCamera cam) {
		if (cam.redstoneCooldown == 0) {
			cam.toggleRedstonePower();
			cam.redstoneCooldown = 30;
		}
	}

	public static void giveNightVision(SecurityCamera cam) {
		if (cam.toggleNightVisionCooldown == 0)
			cam.toggleNightVision();
	}

	public static ClientChunkProvider.ChunkArray getCameraStorage() {
		return cameraStorage;
	}

	public static void setCameraStorage(ClientChunkProvider.ChunkArray cameraStorage) {
		if (CameraController.cameraStorage == null)
			CameraController.cameraStorage = cameraStorage;

		if (cameraStorage != null) {
			cameraStorage.viewCenterX = CameraController.cameraStorage.viewCenterX;
			cameraStorage.viewCenterZ = CameraController.cameraStorage.viewCenterZ;

			for (int k = 0; k < CameraController.cameraStorage.chunks.length(); ++k) {
				Chunk chunk = CameraController.cameraStorage.chunks.get(k);

				if (chunk != null) {
					ChunkPos pos = chunk.getPos();

					if (cameraStorage.inRange(pos.x, pos.z))
						cameraStorage.replace(cameraStorage.getIndex(pos.x, pos.z), chunk);
				}
			}

			CameraController.cameraStorage = cameraStorage;
		}
	}

	public static void setRenderPosition(Entity entity) {
		if (entity instanceof SecurityCamera) {
			SectionPos cameraPos = SectionPos.of(entity);

			cameraStorage.viewCenterX = cameraPos.x();
			cameraStorage.viewCenterZ = cameraPos.z();
		}
	}
}
