package net.geforcemods.securitycraft.entity.camera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Side.CLIENT)
public class FrameFeedHandler {
	private static final Map<GlobalPos, CameraFeed> FRAME_CAMERA_FEEDS = new ConcurrentHashMap<>();
	private static GlobalPos currentlyCapturedCamera;
	private static Frustum lastUsedFrustum;
	private static double lastFrameRendered = 0.0D;

	@SubscribeEvent
	public static void onRenderFramePost(RenderTickEvent event) {
		if (event.phase == Phase.END)
			return;

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;

		if (player == null || player.world == null || !hasFeeds() || !ConfigHandler.frameFeedViewingEnabled || mc.currentScreen instanceof GuiVideoSettings)
			return;

		Profiler profiler = mc.profiler;
		double currentTime = System.nanoTime() / 1E9D;
		Map<GlobalPos, CameraFeed> activeFrameCameraFeeds = getFeedsToRender(currentTime);

		if (activeFrameCameraFeeds.isEmpty())
			return;

		lastFrameRendered = currentTime;
		profiler.endSection(); //out of "render"
		profiler.startSection("gameRenderer");
		profiler.startSection("securitycraft:frame_level");

		World level = player.world;
		float partialTick = mc.isGamePaused() ? mc.renderPartialTicksPaused : event.renderTickTime;
		Entity oldCamEntity = mc.getRenderViewEntity();
		int oldWidth = mc.displayWidth;
		int oldHeight = mc.displayHeight;
		int frameFeedResolution = ConfigHandler.frameFeedResolution;
		List<ContainerLocalRenderInformation> oldVisibleSections = new ObjectArrayList<>(mc.renderGlobal.renderInfos);
		int newFrameFeedViewDistance = getFrameFeedViewDistance(null);
		float oldXRot = player.cameraPitch;
		float oldXRotO = player.prevCameraPitch;
		float oldYRot = player.cameraYaw;
		float oldYRotO = player.prevCameraYaw;
		float oldEyeHeight = player.eyeHeight;
		boolean oldRenderHand = mc.entityRenderer.renderHand;
		boolean oldRenderBlockOutline = mc.entityRenderer.drawBlockOutline;
		int oldCameraType = mc.gameSettings.thirdPersonView;
		EntityArmorStand securityCamera = new EntityArmorStand(level); //A separate entity is used instead of moving the player to allow the player to see themselves
		Frustum playerFrustum = prepareFrustum(oldCamEntity); //Saved once before the loop, because the frustum changes depending on which camera is viewed
		Framebuffer oldMainRenderTarget = mc.getFramebuffer();

		mc.entityRenderer.drawBlockOutline = false;
		mc.entityRenderer.renderHand = false;
		mc.displayWidth = frameFeedResolution;
		mc.displayHeight = frameFeedResolution;
		mc.gameSettings.thirdPersonView = 0;

		for (Entry<GlobalPos, CameraFeed> cameraView : activeFrameCameraFeeds.entrySet()) {
			GlobalPos cameraPos = cameraView.getKey();
			CameraFeed feed = cameraView.getValue();

			if (feed.usesVbo() != OpenGlHelper.useVbo()) //If the player switches their VBO setting, update + recollect all render chunks in range, or else the game crashes
				refreshCameraFeed(cameraPos);
			else if (cameraPos.dimension() == level.provider.getDimension()) {
				BlockPos pos = cameraPos.pos();
				TileEntity te = level.getTileEntity(pos);

				if (te instanceof SecurityCameraBlockEntity) {
					if (playerFrustum != null && !feed.hasFrameInFrustum(playerFrustum))
						continue;

					SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;
					Framebuffer frameTarget = feed.renderTarget();
					Vec3d cameraEntityPos = new Vec3d(pos.getX() + 0.5D, pos.getY() - player.getEyeHeight() + 0.5D, pos.getZ() + 0.5D);
					float cameraXRot = be.getDefaultXRotation();
					float cameraYRot = be.getDefaultYRotation(be.getWorld().getBlockState(be.getPos()).getValue(SecurityCameraBlock.FACING)) + (float) Utils.lerp(partialTick, be.getOriginalCameraRotation(), be.getCameraRotation()) * (180F / (float) Math.PI);

					securityCamera.setPosition(cameraEntityPos.x, cameraEntityPos.y, cameraEntityPos.z);
					mc.setRenderViewEntity(securityCamera);
					securityCamera.rotationPitch = cameraXRot;
					securityCamera.rotationYaw = cameraYRot;
					securityCamera.prevRotationPitch = cameraXRot;
					securityCamera.prevRotationYaw = cameraYRot;
					currentlyCapturedCamera = cameraPos;
					feed.applyVisibleSections(mc.renderGlobal.renderInfos, mc.renderGlobal.chunksToUpdate);
					profiler.startSection("securitycraft:discover_frame_sections");
					feed.discoverVisibleSections(cameraPos, newFrameFeedViewDistance);
					mc.renderGlobal.chunksToUpdate.addAll(feed.getDirtyRenderChunks());
					profiler.endStartSection("securitycraft:bind_frame_target");
					frameTarget.framebufferClear();
					frameTarget.bindFramebuffer(true);
					mc.framebuffer = frameTarget;
					profiler.endSection();

					try {
						mc.entityRenderer.renderWorld(1.0F, System.nanoTime() + 4166666);
					}
					catch (Exception e) {
						SecurityCraft.LOGGER.error("Frame feed at {} threw an exception while rendering the level. Deactivating clientside rendering for this feed", be.getPos());
						e.printStackTrace();
						feed.markForRemoval();
					}

					frameTarget.unbindFramebuffer();
					profiler.startSection("securitycraft:apply_frame_frustum");

					Frustum frustum = prepareFrustum(securityCamera);

					if (be.shouldRotate() || !feed.hasVisibleSections() || feed.requiresFrustumUpdate())
						feed.updateVisibleSections(frustum);

					profiler.endSection();
				}
			}
		}

		securityCamera.setDead();
		mc.setRenderViewEntity(oldCamEntity);
		player.cameraPitch = oldXRot;
		player.prevCameraPitch = oldXRotO;
		player.cameraYaw = oldYRot;
		player.prevCameraYaw = oldYRotO;
		player.eyeHeight = oldEyeHeight;
		mc.gameSettings.thirdPersonView = oldCameraType;
		mc.entityRenderer.drawBlockOutline = oldRenderBlockOutline;
		mc.renderGlobal.renderInfos.clear();
		mc.renderGlobal.renderInfos.addAll(oldVisibleSections);
		mc.entityRenderer.renderHand = oldRenderHand;
		mc.displayWidth = oldWidth;
		mc.displayHeight = oldHeight;
		mc.framebuffer = oldMainRenderTarget;
		mc.getFramebuffer().bindFramebuffer(true);
		currentlyCapturedCamera = null;

		profiler.endSection();
		profiler.endSection();
		profiler.startSection("render");
	}

	@SubscribeEvent
	public static void onClientTickPost(ClientTickEvent event) {
		if (event.phase == Phase.END && hasFeeds())
			FRAME_CAMERA_FEEDS.entrySet().removeIf(e -> e.getValue().shouldBeRemoved());
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
		CameraFeed feed = FRAME_CAMERA_FEEDS.computeIfAbsent(cameraPos, FrameFeedHandler::createFeedForCamera);

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

	public static int getFrameFeedViewDistance(FrameBlockEntity be) {
		int frameSpecificRenderDistance = be == null ? 32 : be.getChunkLoadingDistanceOption();

		return Math.min(frameSpecificRenderDistance, Math.min(ConfigHandler.frameFeedRenderDistance, Math.min(ConfigHandler.frameFeedViewDistance, Minecraft.getMinecraft().gameSettings.renderDistanceChunks)));
	}

	public static void updateLastUsedFrustum() {
		ClippingHelper currentClippingHelper = ClippingHelperImpl.getInstance();
		ClippingHelper newClippingHelper = new ClippingHelper();

		newClippingHelper.frustum = currentClippingHelper.frustum.clone();
		newClippingHelper.projectionMatrix = currentClippingHelper.projectionMatrix.clone();
		newClippingHelper.modelviewMatrix = currentClippingHelper.modelviewMatrix.clone();
		newClippingHelper.clippingMatrix = currentClippingHelper.clippingMatrix.clone();
		lastUsedFrustum = new Frustum(newClippingHelper);
	}

	private static Frustum prepareFrustum(Entity entity) {
		if (lastUsedFrustum != null)
			lastUsedFrustum.setPosition(entity.posX, entity.posY, entity.posZ);

		return lastUsedFrustum;
	}
}
