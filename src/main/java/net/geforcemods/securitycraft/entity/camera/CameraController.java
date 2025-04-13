package net.geforcemods.securitycraft.entity.camera;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.GlobalPos;
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
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
	private static final Map<GlobalPos, CameraFeed> FRAME_CAMERA_FEEDS = new ConcurrentHashMap<>();
	private static GlobalPos currentlyCapturedCamera;
	private static ClippingHelper lastUsedClippingHelper;
	private static double lastFrameRendered = 0.0D;

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

		if (event.phase == Phase.END)
			FRAME_CAMERA_FEEDS.entrySet().removeIf(e -> e.getValue().shouldBeRemoved());
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

	public static void markAsCapturedCamera(GlobalPos cameraPos) {
		currentlyCapturedCamera = cameraPos;
	}

	public static Map<GlobalPos, CameraFeed> getFeedsToRender(double currentTime) {
		//+1 helps to reduce stuttering when many frames are active at once
		double feedsToRender = FRAME_CAMERA_FEEDS.size() + 1;
		double fpsCap = ConfigHandler.frameFeedFpsLimit;
		double frameInterval = 1.0D / fpsCap;
		double activeFramesPerMcFrame = MathHelper.ceil((fpsCap * feedsToRender) / Minecraft.getDebugFPS());

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

	public static void refreshCameraFeed(GlobalPos cameraPos) {
		FRAME_CAMERA_FEEDS.put(cameraPos, createFeedForCamera(cameraPos));
	}

	private static CameraFeed createFeedForCamera(GlobalPos cameraPos) {
		BlockPos pos = cameraPos.pos();
		ContainerLocalRenderInformation startingSection = Minecraft.getMinecraft().renderGlobal.new ContainerLocalRenderInformation(CameraViewAreaExtension.rawFetch(pos.getX() >> 4, MathHelper.clamp(pos.getY() >> 4, 0, 15), pos.getZ() >> 4, true), null, 0);

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
		return isCapturingCamera() && currentlyCapturedCamera.pos().equals(be.getPos());
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

	public static void copyClippingHelper() {
		ClippingHelper currentClippingHelper = ClippingHelperImpl.getInstance();

		lastUsedClippingHelper = new ClippingHelper();
		lastUsedClippingHelper.frustum = currentClippingHelper.frustum.clone();
		lastUsedClippingHelper.projectionMatrix = currentClippingHelper.projectionMatrix.clone();
		lastUsedClippingHelper.modelviewMatrix = currentClippingHelper.modelviewMatrix.clone();
		lastUsedClippingHelper.clippingMatrix = currentClippingHelper.clippingMatrix.clone();
	}

	public static ClippingHelper getLastUsedClippingHelper() {
		return lastUsedClippingHelper;
	}

	public static CameraFeed getCurrentFeed() {
		return FRAME_CAMERA_FEEDS.get(currentlyCapturedCamera);
	}

	public static int getFrameFeedViewDistance(FrameBlockEntity be) {
		int frameSpecificRenderDistance = be == null ? 32 : be.getChunkLoadingDistanceOption();

		return Math.min(frameSpecificRenderDistance, Math.min(ConfigHandler.frameFeedRenderDistance, Math.min(ConfigHandler.frameFeedViewDistance, Minecraft.getMinecraft().gameSettings.renderDistanceChunks)));
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
