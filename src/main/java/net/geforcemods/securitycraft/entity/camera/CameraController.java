package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.OverlayToggleHandler;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class CameraController {
	/**
	 * @deprecated Don't use directly, use {@link #cameraSpeed()} so the config value gets loaded at the correct time
	 */
	@Deprecated
	private static double cameraSpeed = -1.0D;
	public static CameraType previousCameraType;
	public static boolean resetOverlaysAfterDismount = false;
	private static ClientChunkCache.Storage cameraStorage;
	private static boolean chunksLoaded = false;
	private static int chunkLoadingDistance = -1;
	private static boolean wasUpPressed;
	private static boolean wasDownPressed;
	private static boolean wasLeftPressed;
	private static boolean wasRightPressed;

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;

		if (cameraEntity instanceof SecurityCamera cam) {
			Options options = Minecraft.getInstance().options;

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
					moveViewHorizontally(cam, cam.getYRot(), cam.getYRot() - (float) cameraSpeed() * cam.zoomAmount);
					options.keyLeft.setDown(true);
				}

				if (wasRightPressed) {
					moveViewHorizontally(cam, cam.getYRot(), cam.getYRot() + (float) cameraSpeed() * cam.zoomAmount);
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
				LocalPlayer player = Minecraft.getInstance().player;
				double yRotChange = player.getYRot() - player.yRotLast;
				double xRotChange = player.getXRot() - player.xRotLast;

				if (yRotChange != 0.0D || xRotChange != 0.0D)
					player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.isOnGround()));
			}
		}
		else if (resetOverlaysAfterDismount) {
			resetOverlaysAfterDismount = false;
			OverlayToggleHandler.disable(ClientHandler.cameraOverlay);
			OverlayToggleHandler.enable(ClientHandler.hotbarBindOverlay);
			OverlayToggleHandler.enable(VanillaGuiOverlay.JUMP_BAR);
			OverlayToggleHandler.enable(VanillaGuiOverlay.EXPERIENCE_BAR);
			OverlayToggleHandler.enable(VanillaGuiOverlay.POTION_ICONS);
		}
	}

	private static void dismount() {
		SecurityCraft.channel.sendToServer(new DismountCamera());
	}

	public static void moveViewUp(SecurityCamera cam) {
		float next = cam.getXRot() - (float) cameraSpeed() * cam.zoomAmount;

		if (cam.isCameraDown()) {
			if (next > 40F)
				cam.setRotation(cam.getYRot(), next);
		}
		else if (next > -25F)
			cam.setRotation(cam.getYRot(), next);
	}

	public static void moveViewDown(SecurityCamera cam) {
		float next = cam.getXRot() + (float) cameraSpeed() * cam.zoomAmount;

		if (cam.isCameraDown()) {
			if (next < 90F)
				cam.setRotation(cam.getYRot(), next);
		}
		else if (next < 60F)
			cam.setRotation(cam.getYRot(), next);
	}

	public static void moveViewHorizontally(SecurityCamera cam, float yRot, float next) {
		BlockState state = cam.level.getBlockState(cam.blockPosition());

		if (state.hasProperty(SecurityCameraBlock.FACING)) {
			float checkNext = next;

			if (checkNext < 0)
				checkNext += 360;

			boolean shouldSetRotation = switch (state.getValue(SecurityCameraBlock.FACING)) {
				case NORTH -> checkNext > 90F && checkNext < 270F;
				case SOUTH -> checkNext > 270F || checkNext < 90F;
				case EAST -> checkNext > 180F && checkNext < 360F;
				case WEST -> checkNext > 0F && checkNext < 180F;
				case DOWN -> true;
				default -> false;
			};

			if (shouldSetRotation)
				cam.setYRot(next);
		}
	}

	public static void zoomIn(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.max(cam.zoomAmount - 0.1F, 0.1F);
	}

	public static void zoomOut(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);

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

	public static ClientChunkCache.Storage getCameraStorage() {
		return cameraStorage;
	}

	public static void setCameraStorage(ClientChunkCache.Storage newStorage) {
		if (newStorage != null) {
			if (cameraStorage != null) {
				newStorage.viewCenterX = cameraStorage.viewCenterX;
				newStorage.viewCenterZ = cameraStorage.viewCenterZ;

				for (int i = 0; i < cameraStorage.chunks.length(); i++) {
					LevelChunk chunk = cameraStorage.chunks.get(i);

					if (chunk != null) {
						ChunkPos pos = chunk.getPos();

						if (newStorage.inRange(pos.x, pos.z))
							newStorage.replace(newStorage.getIndex(pos.x, pos.z), chunk);
					}
				}
			}

			cameraStorage = newStorage;
		}
	}

	public static void setHasLoadedChunks(boolean hasLoadedChunks) {
		chunksLoaded = hasLoadedChunks;
	}

	public static void setChunkLoadingDistance(int initialViewDistance) {
		chunkLoadingDistance = initialViewDistance;
	}

	public static boolean hasLoadedChunks() {
		return chunksLoaded;
	}

	public static int getChunkLoadingDistance() {
		return chunkLoadingDistance;
	}

	public static void setRenderPosition(Entity entity) {
		if (entity instanceof SecurityCamera) {
			SectionPos cameraPos = SectionPos.of(entity);

			cameraStorage.viewCenterX = cameraPos.x();
			cameraStorage.viewCenterZ = cameraPos.z();
		}
	}

	private static double cameraSpeed() {
		if (cameraSpeed < 0.0D)
			cameraSpeed = ConfigHandler.CLIENT.cameraSpeed.get();

		return cameraSpeed;
	}
}
