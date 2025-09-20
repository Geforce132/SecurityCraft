package net.geforcemods.securitycraft.entity.camera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class FrameFeedHandler {
	private static final Map<GlobalPos, CameraFeed> FRAME_CAMERA_FEEDS = new ConcurrentHashMap<>();
	private static GlobalPos currentlyCapturedCamera;
	private static double lastFrameRendered = 0.0D;

	public static void captureFrameFeeds(DeltaTracker partialTick) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;

		if (player == null || player.connection.getLevel() == null || !hasFeeds() || !ConfigHandler.SERVER.frameFeedViewingEnabled.get() || isCapturingCamera())
			return;

		ProfilerFiller profiler = Profiler.get();
		double currentTime = GLFW.glfwGetTime();
		Map<GlobalPos, CameraFeed> activeFrameCameraFeeds = getFeedsToRender(mc, currentTime);

		if (activeFrameCameraFeeds.isEmpty())
			return;

		lastFrameRendered = currentTime;
		profiler.popPush("securitycraft:frame_level");

		Level level = player.level();
		Camera camera = mc.gameRenderer.getMainCamera();
		Entity oldCamEntity = mc.getCameraEntity();
		Window window = mc.getWindow();
		int oldWidth = window.getWidth();
		int oldHeight = window.getHeight();
		List<SectionRenderDispatcher.RenderSection> oldVisibleSections = mc.levelRenderer.visibleSections.clone();
		int newFrameFeedViewDistance = getFrameFeedViewDistance(null);
		double oldX = player.getX();
		double oldXO = player.xOld;
		double oldY = player.getY();
		double oldYO = player.yOld;
		double oldZ = player.getZ();
		double oldZO = player.zOld;
		float oldXRot = player.getXRot();
		float oldXRotO = player.xRotO;
		float oldYRot = player.getYRot();
		float oldYRotO = player.yRotO;
		float oldEyeHeight = camera.eyeHeight;
		float oldEyeHeightO = camera.eyeHeightOld;
		CameraType oldCameraType = mc.options.getCameraType();
		Entity securityCamera = new Marker(EntityType.MARKER, level); //A separate entity is used instead of moving the player to allow the player to see themselves
		Frustum playerFrustum = mc.levelRenderer.getFrustum(); //Saved once before the loop, because the frustum changes depending on which camera is viewed
		RenderTarget oldMainRenderTarget = mc.getMainRenderTarget();

		mc.gameRenderer.setRenderBlockOutline(false);
		mc.gameRenderer.setPanoramicMode(true);
		window.setWidth(100);
		window.setHeight(100); //Different width/height values seem to have no effect, although the ratio needs to be 1:1
		mc.options.setCameraType(CameraType.FIRST_PERSON);
		camera.eyeHeight = camera.eyeHeightOld = player.getDimensions(Pose.STANDING).eyeHeight();
		mc.renderBuffers().bufferSource().endBatch(); //Makes sure that previous world rendering is done

		for (Entry<GlobalPos, CameraFeed> cameraView : activeFrameCameraFeeds.entrySet()) {
			GlobalPos cameraPos = cameraView.getKey();

			if (cameraPos.dimension().equals(level.dimension())) {
				BlockPos pos = cameraPos.pos();

				if (level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be) {
					CameraFeed feed = cameraView.getValue();

					if (!feed.hasFrameInFrustum(playerFrustum))
						continue;

					Vec3 cameraEntityPos = new Vec3(pos.getX() + 0.5D, pos.getY() - player.getDimensions(Pose.STANDING).eyeHeight() + 0.5D, pos.getZ() + 0.5D);
					float cameraXRot = be.getDefaultXRotation();
					float cameraYRot = be.getDefaultYRotation(be.getBlockState().getValue(SecurityCameraBlock.FACING)) + (float) Mth.lerp(partialTick.getGameTimeDeltaPartialTick(false), be.getOriginalCameraRotation(), be.getCameraRotation()) * Mth.RAD_TO_DEG;

					securityCamera.setPos(cameraEntityPos);
					mc.setCameraEntity(securityCamera);
					securityCamera.setXRot(cameraXRot);
					securityCamera.setYRot(cameraYRot);
					currentlyCapturedCamera = cameraPos;
					feed.applyVisibleSections(mc.levelRenderer.visibleSections);
					profiler.push("securitycraft:discover_frame_sections");
					feed.discoverVisibleSections(cameraPos, newFrameFeedViewDistance);
					mc.levelRenderer.endFrame(); //This fixes frame feed clouds being rendered at the position of a previous feed sometimes, due to the cloud rendering buffer not resetting itself properly
					mc.gameRenderer.fogRenderer.endFrame(); //Same fix but for fog color
					mc.mainRenderTarget = feed.renderTarget();

					try {
						mc.gameRenderer.renderLevel(DeltaTracker.ONE);
					}
					catch (Exception e) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.FRAME.get().getDescriptionId()), Utils.localize("messages.securitycraft:frame.error"), ChatFormatting.RED, true);
						SecurityCraft.LOGGER.error("Frame feed at {} threw an exception while rendering the level. Deactivating clientside rendering for this feed", be.getBlockPos());
						e.printStackTrace();
						feed.markForRemoval();
					}

					profiler.push("securitycraft:apply_frame_frustum");

					Frustum frustum = LevelRenderer.offsetFrustum(mc.levelRenderer.getFrustum()); //This needs the frame's newly calculated frustum, so it needs to be queried from inside the loop

					if (be.shouldRotate() || !feed.hasVisibleSections() || feed.requiresFrustumUpdate())
						feed.updateVisibleSections(frustum);

					profiler.pop();
				}
			}
		}

		securityCamera.discard();
		mc.setCameraEntity(oldCamEntity);
		player.setPosRaw(oldX, oldY, oldZ);
		player.xOld = player.xo = oldXO;
		player.yOld = player.yo = oldYO;
		player.zOld = player.zo = oldZO;
		player.setXRot(oldXRot);
		player.xRotO = oldXRotO;
		player.setYRot(oldYRot);
		player.yRotO = oldYRotO;
		camera.setup(level, oldCamEntity == null ? player : oldCamEntity, !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), level.tickRateManager().isEntityFrozen(oldCamEntity) ? 1.0F : partialTick.getGameTimeDeltaPartialTick(true));
		camera.eyeHeight = oldEyeHeight;
		camera.eyeHeightOld = oldEyeHeightO;
		mc.options.setCameraType(oldCameraType);
		mc.gameRenderer.setRenderBlockOutline(true);
		mc.levelRenderer.visibleSections.clear();
		mc.levelRenderer.visibleSections.addAll(oldVisibleSections);
		window.setWidth(oldWidth);
		window.setHeight(oldHeight);
		mc.gameRenderer.setPanoramicMode(false);
		mc.mainRenderTarget = oldMainRenderTarget;
		currentlyCapturedCamera = null;
	}

	@SubscribeEvent
	public static void onClientTickPost(ClientTickEvent.Post event) {
		if (hasFeeds())
			FRAME_CAMERA_FEEDS.entrySet().removeIf(e -> e.getValue().shouldBeRemoved());
	}

	public static Map<GlobalPos, CameraFeed> getFeedsToRender(Minecraft mc, double currentTime) {
		//+1 helps to reduce stuttering when many frames are active at once
		double feedsToRender = FRAME_CAMERA_FEEDS.size() + 1;
		double fpsCap = ConfigHandler.CLIENT.frameFeedFpsLimit.get();
		double frameInterval = 1.0D / fpsCap;
		double activeFramesPerMcFrame = Mth.ceil((fpsCap * feedsToRender) / mc.getFps());

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

	private static CameraFeed createFeedForCamera(GlobalPos cameraPos) {
		SectionPos cameraSectionPos = SectionPos.of(cameraPos.pos());
		RenderSection startingSection = CameraViewAreaExtension.rawFetch(cameraSectionPos.x(), Mth.clamp(cameraSectionPos.y(), CameraViewAreaExtension.minSectionY(), CameraViewAreaExtension.maxSectionY() - 1), cameraSectionPos.z(), true);

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
		for (GlobalPos cameraPos : FRAME_CAMERA_FEEDS.keySet()) {
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

	public static int getFrameFeedViewDistance(FrameBlockEntity be) {
		int frameSpecificRenderDistance = be == null ? 32 : be.getChunkLoadingDistanceOption();

		return Math.min(frameSpecificRenderDistance, Math.min(ConfigHandler.CLIENT.frameFeedRenderDistance.get(), Math.min(ConfigHandler.SERVER.frameFeedViewDistance.get(), Minecraft.getInstance().options.getEffectiveRenderDistance())));
	}
}
