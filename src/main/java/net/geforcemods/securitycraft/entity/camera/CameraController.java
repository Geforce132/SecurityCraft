package net.geforcemods.securitycraft.entity.camera;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OverlayToggleHandler;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenshotEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.event.TickEvent.ClientTickEvent;
import net.neoforged.neoforge.event.TickEvent.Phase;

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
	//@formatter:off
	private static final ViewMovementKeyHandler[] MOVE_KEY_HANDLERS = {
			new ViewMovementKeyHandler(Minecraft.getInstance().options.keyUp, CameraController::moveViewUp),
			new ViewMovementKeyHandler(Minecraft.getInstance().options.keyDown, CameraController::moveViewDown),
			new ViewMovementKeyHandler(Minecraft.getInstance().options.keyLeft, cam -> moveViewHorizontally(cam, cam.getYRot() - (float) cameraSpeed() * cam.zoomAmount)),
			new ViewMovementKeyHandler(Minecraft.getInstance().options.keyRight, cam -> moveViewHorizontally(cam, cam.getYRot() + (float) cameraSpeed() * cam.zoomAmount))
	};
	//@formatter:on
	private static int screenshotSoundCooldown = 0;

	private CameraController() {}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;

		if (cameraEntity instanceof SecurityCamera cam) {
			Options options = Minecraft.getInstance().options;

			//up/down/left/right handling is split to prevent players who are viewing a camera from moving around in a boat or on a horse
			if (event.phase == Phase.START) {
				for (ViewMovementKeyHandler handler : MOVE_KEY_HANDLERS) {
					handler.tickStart();
				}

				if (options.keyShift.isDown()) {
					dismount();
					options.keyShift.setDown(false);
				}
			}
			else if (event.phase == Phase.END) {
				for (ViewMovementKeyHandler handler : MOVE_KEY_HANDLERS) {
					handler.tickEnd(cam);
				}

				if (KeyBindings.cameraZoomIn.isDown())
					zoomIn(cam);
				else if (KeyBindings.cameraZoomOut.isDown())
					zoomOut(cam);
				else
					cam.zooming = false;

				KeyBindings.cameraEmitRedstone.tick(cam);
				KeyBindings.cameraActivateNightVision.tick(cam);
				KeyBindings.setDefaultViewingDirection.tick(cam);
				screenshotSoundCooldown--;

				//update other players with the head rotation
				LocalPlayer player = Minecraft.getInstance().player;
				double yRotChange = player.getYRot() - player.yRotLast;
				double xRotChange = player.getXRot() - player.xRotLast;

				if (yRotChange != 0.0D || xRotChange != 0.0D)
					player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.onGround()));
			}
		}
		else if (resetOverlaysAfterDismount) {
			resetOverlaysAfterDismount = false;
			OverlayToggleHandler.disable(ClientHandler.cameraOverlay);
			OverlayToggleHandler.enable(VanillaGuiOverlay.JUMP_BAR);
			OverlayToggleHandler.enable(VanillaGuiOverlay.EXPERIENCE_BAR);
			OverlayToggleHandler.enable(VanillaGuiOverlay.POTION_ICONS);
		}
	}

	@SubscribeEvent
	public static void onScreenshot(ScreenshotEvent event) {
		Player player = Minecraft.getInstance().player;

		if (PlayerUtils.isPlayerMountedOnCamera(player) && screenshotSoundCooldown <= 0) {
			screenshotSoundCooldown = 7;
			Minecraft.getInstance().level.playLocalSound(player.blockPosition(), SCSounds.CAMERASNAP.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);
		}
	}

	private static void dismount() {
		SecurityCraft.CHANNEL.sendToServer(new DismountCamera());
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

	public static void moveViewHorizontally(SecurityCamera cam, float next) {
		BlockState state = cam.level().getBlockState(cam.blockPosition());

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

	public static void toggleRedstone(SecurityCamera cam) {
		BlockPos pos = cam.blockPosition();
		Level level = cam.level();

		if (((IModuleInventory) level.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE))
			SecurityCraft.CHANNEL.sendToServer(new SetCameraPowered(pos, !level.getBlockState(pos).getValue(SecurityCameraBlock.POWERED)));
	}

	public static void toggleNightVision(SecurityCamera cam) {
		SecurityCraft.CHANNEL.sendToServer(new ToggleNightVision());
	}

	public static void setDefaultViewingDirection(SecurityCamera cam) {
		SecurityCraft.CHANNEL.sendToServer(new SetDefaultCameraViewingDirection(cam));
	}

	public static ClientChunkCache.Storage getCameraStorage() {
		return cameraStorage;
	}

	public static void setCameraStorage(ClientChunkCache.Storage newStorage) {
		cameraStorage = newStorage;
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

	public static class ViewMovementKeyHandler {
		private final KeyMapping key;
		private final Consumer<SecurityCamera> action;
		private boolean wasPressed;

		public ViewMovementKeyHandler(KeyMapping key, Consumer<SecurityCamera> action) {
			this.key = key;
			this.action = action;
		}

		public void tickStart() {
			wasPressed = key.isDown();

			if (wasPressed)
				key.setDown(false);
		}

		public void tickEnd(SecurityCamera cam) {
			if (wasPressed) {
				action.accept(cam);
				key.setDown(true);
			}
		}
	}
}
