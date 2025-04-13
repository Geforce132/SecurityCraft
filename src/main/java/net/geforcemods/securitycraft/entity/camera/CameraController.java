package net.geforcemods.securitycraft.entity.camera;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
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
import net.minecraft.Util;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer.RenderChunkInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class CameraController {
	public static boolean jumpBarElementEnabledPreviously;
	public static boolean experienceBarElementEnabledPreviously;
	public static boolean potionIconsElementEnabledPreviously;
	public static CameraType previousCameraType;
	public static boolean resetOverlaysAfterDismount = false;
	private static final ViewMovementKeyHandler[] MOVE_KEY_HANDLERS = Util.make(() -> {
		Minecraft mc = Minecraft.getInstance();

		if (mc != null) {
			return new ViewMovementKeyHandler[] {
				//@formatter:off
				new ViewMovementKeyHandler(Minecraft.getInstance().options.keyUp, CameraController::moveViewUp),
				new ViewMovementKeyHandler(Minecraft.getInstance().options.keyDown, CameraController::moveViewDown),
				new ViewMovementKeyHandler(Minecraft.getInstance().options.keyLeft, cam -> moveViewHorizontally(cam, cam.getYRot() - getMovementSpeed(cam) * cam.getZoomAmount())),
				new ViewMovementKeyHandler(Minecraft.getInstance().options.keyRight, cam -> moveViewHorizontally(cam, cam.getYRot() + getMovementSpeed(cam) * cam.getZoomAmount()))
				//@formatter:on
			};
		}
		else
			return new ViewMovementKeyHandler[0];
	});
	private static int screenshotSoundCooldown = 0;
	private static final Map<GlobalPos, CameraFeed> FRAME_CAMERA_FEEDS = new ConcurrentHashMap<>();
	private static GlobalPos currentlyCapturedCamera;
	private static double lastFrameRendered = 0.0D;

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
					player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.isOnGround()));
			}
		}
		else if (resetOverlaysAfterDismount) {
			resetOverlaysAfterDismount = false;
			OverlayRegistry.enableOverlay(ClientHandler.cameraOverlay, false);
			CameraController.restoreOverlayStates();
		}

		if (event.phase == Phase.END)
			FRAME_CAMERA_FEEDS.entrySet().removeIf(e -> e.getValue().shouldBeRemoved());
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
		float next = cam.getXRot() - getMovementSpeed(cam) * cam.getZoomAmount();

		if (cam.isCameraDown()) {
			if (next > 40F)
				cam.setRotation(cam.getYRot(), next);
		}
		else if (next > -25F)
			cam.setRotation(cam.getYRot(), next);
	}

	public static void moveViewDown(SecurityCamera cam) {
		float next = cam.getXRot() + getMovementSpeed(cam) * cam.getZoomAmount();

		if (cam.isCameraDown()) {
			if (next < 90F)
				cam.setRotation(cam.getYRot(), next);
		}
		else if (next < 60F)
			cam.setRotation(cam.getYRot(), next);
	}

	public static void moveViewHorizontally(SecurityCamera cam, float next) {
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
		cam.setZoomAmount(Math.max(cam.getZoomAmount() - 0.1F, 0.1F));
	}

	public static void zoomOut(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.setZoomAmount(Math.min(cam.getZoomAmount() + 0.1F, 1.4F));
	}

	public static void toggleRedstone(SecurityCamera cam) {
		BlockPos pos = cam.blockPosition();

		if (cam.level.getBlockEntity(pos) instanceof IModuleInventory be && be.isModuleEnabled(ModuleType.REDSTONE))
			SecurityCraft.CHANNEL.sendToServer(new SetCameraPowered(pos, !cam.level.getBlockState(pos).getValue(SecurityCameraBlock.POWERED)));
	}

	public static void toggleNightVision(SecurityCamera cam) {
		if (ConfigHandler.SERVER.allowCameraNightVision.get())
			SecurityCraft.CHANNEL.sendToServer(new ToggleNightVision());
	}

	public static void setDefaultViewingDirection(SecurityCamera cam) {
		SecurityCraft.CHANNEL.sendToServer(new SetDefaultCameraViewingDirection(cam));
	}

	public static void markAsCapturedCamera(GlobalPos cameraPos) {
		currentlyCapturedCamera = cameraPos;
	}

	public static Map<GlobalPos, CameraFeed> getFeedsToRender(double currentTime) {
		//+1 helps to reduce stuttering when many frames are active at once
		double feedsToRender = FRAME_CAMERA_FEEDS.size() + 1;
		double fpsCap = ConfigHandler.CLIENT.frameFeedFpsLimit.get();
		double frameInterval = 1.0D / fpsCap;
		double activeFramesPerMcFrame = Mth.ceil((fpsCap * feedsToRender) / Minecraft.fps);

		if (fpsCap < 260.0D) {
			Map<GlobalPos, CameraFeed> activeFrameCameraFeeds = new HashMap<>();

			for (Entry<GlobalPos, CameraFeed> cameraView : FRAME_CAMERA_FEEDS.entrySet()) {
				double timeBetweenFrames = frameInterval / feedsToRender;
				double lastActiveTime = cameraView.getValue().lastActiveTime().get();

				if (currentTime < lastActiveTime + frameInterval || currentTime < lastFrameRendered + timeBetweenFrames || activeFramesPerMcFrame-- <= 0)
					continue;

				cameraView.getValue().lastActiveTime().set(currentTime);
				activeFrameCameraFeeds.put(cameraView.getKey(), cameraView.getValue());
			}

			return activeFrameCameraFeeds;
		}
		else
			return FRAME_CAMERA_FEEDS;
	}

	public static void addFrameLink(FrameBlockEntity be, GlobalPos cameraPos) {
		CameraFeed feed = FRAME_CAMERA_FEEDS.computeIfAbsent(cameraPos, CameraController::createFeedForCamera);

		feed.linkFrame(be);
	}

	private static CameraFeed createFeedForCamera(GlobalPos cameraPos) {
		SectionPos cameraSectionPos = SectionPos.of(cameraPos.pos());
		RenderChunkInfo startingSection = new RenderChunkInfo(CameraViewAreaExtension.rawFetch(cameraSectionPos.x(), Mth.clamp(cameraSectionPos.y(), CameraViewAreaExtension.minSectionY(), CameraViewAreaExtension.maxSectionY() - 1), cameraSectionPos.z(), true), null, 0);

		return new CameraFeed(cameraPos, startingSection);
	}

	public static void removeFrameLink(GlobalPos cameraPos, FrameBlockEntity be) {
		if (FRAME_CAMERA_FEEDS.containsKey(cameraPos))
			FRAME_CAMERA_FEEDS.get(cameraPos).unlinkFrame(be);
	}

	public static void removeAllFrameLinks(GlobalPos cameraPos) {
		if (FRAME_CAMERA_FEEDS.containsKey(cameraPos))
			FRAME_CAMERA_FEEDS.remove(cameraPos);
	}

	public static boolean isCapturingCamera() {
		return currentlyCapturedCamera != null;
	}

	public static boolean amIBeingCaptured(SecurityCameraBlockEntity be) {
		return isCapturingCamera() && currentlyCapturedCamera.pos().equals(be.getBlockPos());
	}

	public static boolean shouldAddChunk(ChunkPos pos, int renderDistance) {
		for (GlobalPos cameraPos : CameraController.FRAME_CAMERA_FEEDS.keySet()) {
			if (pos.getChessboardDistance(new ChunkPos(cameraPos.pos())) <= (renderDistance + 1))
				return true;
		}

		return false;
	}

	public static boolean hasFeeds() {
		return !FRAME_CAMERA_FEEDS.isEmpty();
	}

	public static boolean hasFeed(GlobalPos cameraPos) {
		return FRAME_CAMERA_FEEDS.containsKey(cameraPos);
	}

	public static CameraFeed getFeed(GlobalPos cameraPos) {
		return FRAME_CAMERA_FEEDS.get(cameraPos);
	}

	public static void removeAllFeeds() {
		FRAME_CAMERA_FEEDS.clear();
	}

	public static CameraFeed getCurrentlyCapturedFeed() {
		return getFeed(currentlyCapturedCamera);
	}

	public static void setLastFrameRendered(double lastFrameRendered) {
		CameraController.lastFrameRendered = lastFrameRendered;
	}

	public static int getFrameFeedViewDistance(FrameBlockEntity be) {
		int frameSpecificRenderDistance = be == null ? 32 : be.getChunkLoadingDistanceOption();

		return Math.min(frameSpecificRenderDistance, Math.min(ConfigHandler.CLIENT.frameFeedRenderDistance.get(), Math.min(ConfigHandler.SERVER.frameFeedViewDistance.get(), Minecraft.getInstance().options.getEffectiveRenderDistance())));
	}

	public static void saveOverlayStates() {
		jumpBarElementEnabledPreviously = OverlayRegistry.getEntry(ForgeIngameGui.JUMP_BAR_ELEMENT).isEnabled();
		experienceBarElementEnabledPreviously = OverlayRegistry.getEntry(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT).isEnabled();
		potionIconsElementEnabledPreviously = OverlayRegistry.getEntry(ForgeIngameGui.POTION_ICONS_ELEMENT).isEnabled();
	}

	public static void restoreOverlayStates() {
		if (jumpBarElementEnabledPreviously)
			OverlayRegistry.enableOverlay(ForgeIngameGui.JUMP_BAR_ELEMENT, true);

		if (experienceBarElementEnabledPreviously)
			OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, true);

		if (potionIconsElementEnabledPreviously)
			OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, true);
	}

	public static float getMovementSpeed(SecurityCamera cam) {
		SecurityCameraBlockEntity be = cam.getBlockEntity();

		if (be != null)
			return (float) be.getMovementSpeed();

		return 0.0F;
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
