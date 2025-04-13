package net.geforcemods.securitycraft.entity.camera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldRenderer.LocalRenderInformationContainer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class FrameFeedHandler {
	private static final Map<GlobalPos, CameraFeed> FRAME_CAMERA_FEEDS = new ConcurrentHashMap<>();
	private static GlobalPos currentlyCapturedCamera;
	private static double lastFrameRendered = 0.0D;
	private static ClippingHelper lastUsedFrustum;

	@SubscribeEvent
	public static void onRenderFramePost(RenderTickEvent event) {
		if (event.phase == Phase.END)
			return;

		Minecraft mc = Minecraft.getInstance();
		ClientPlayerEntity player = mc.player;

		if (player == null || player.connection.getLevel() == null || !hasFeeds() || !ConfigHandler.SERVER.frameFeedViewingEnabled.get())
			return;

		IProfiler profiler = mc.getProfiler();
		double currentTime = GLFW.glfwGetTime();
		Map<GlobalPos, CameraFeed> activeFrameCameraFeeds = getFeedsToRender(currentTime);

		if (activeFrameCameraFeeds.isEmpty())
			return;

		lastFrameRendered = currentTime;
		profiler.pop(); //out of "render"
		profiler.push("gameRenderer");
		profiler.push("securitycraft:frame_level");

		World level = player.level;
		float partialTick = event.renderTickTime;
		ActiveRenderInfo camera = mc.gameRenderer.getMainCamera();
		Entity oldCamEntity = mc.cameraEntity;
		MainWindow window = mc.getWindow();
		int oldWidth = window.getWidth();
		int oldHeight = window.getHeight();
		List<WorldRenderer.LocalRenderInformationContainer> oldVisibleSections = new ObjectArrayList<>(mc.levelRenderer.renderChunks);
		GraphicsFanciness oldGraphicsMode = mc.options.graphicsMode;
		Framebuffer oldItemEntityTarget = mc.levelRenderer.getItemEntityTarget();
		Framebuffer oldWeatherTarget = mc.levelRenderer.getWeatherTarget();
		ShaderGroup oldTransparencyChain = mc.levelRenderer.transparencyChain;
		int newFrameFeedViewDistance = getFrameFeedViewDistance(null);
		double oldX = player.getX();
		double oldXO = player.xOld;
		double oldY = player.getY();
		double oldYO = player.yOld;
		double oldZ = player.getZ();
		double oldZO = player.zOld;
		float oldXRot = player.xRot;
		float oldXRotO = player.xRotO;
		float oldYRot = player.yRot;
		float oldYRotO = player.yRotO;
		float oldEyeHeight = camera.eyeHeight;
		float oldEyeHeightO = camera.eyeHeightOld;
		boolean oldRenderHand = mc.gameRenderer.renderHand;
		boolean oldRenderBlockOutline = mc.gameRenderer.renderBlockOutline;
		boolean oldPanoramicMode = mc.gameRenderer.panoramicMode;
		PointOfView oldCameraType = mc.options.getCameraType();
		ArmorStandEntity securityCamera = EntityType.ARMOR_STAND.create(level); //A separate entity is used instead of moving the player to allow the player to see themselves
		ClippingHelper playerFrustum = prepareFrustum(camera); //Saved once before the loop, because the frustum changes depending on which camera is viewed

		mc.gameRenderer.renderBlockOutline = false;
		mc.gameRenderer.renderHand = false;
		mc.gameRenderer.panoramicMode = true;

		if (mc.options.graphicsMode.getId() > GraphicsFanciness.FANCY.getId())
			mc.options.graphicsMode = GraphicsFanciness.FANCY;

		mc.levelRenderer.itemEntityTarget = null;
		mc.levelRenderer.weatherTarget = null;
		mc.levelRenderer.transparencyChain = null;
		window.framebufferWidth = 100;
		window.framebufferHeight = 100; //Different width/height values seem to have no effect, although the ratio needs to be 1:1
		mc.options.setCameraType(PointOfView.FIRST_PERSON);
		camera.eyeHeight = camera.eyeHeightOld = player.getEyeHeight(Pose.STANDING);
		mc.renderBuffers().bufferSource().endBatch(); //Makes sure that previous world rendering is done

		for (Entry<GlobalPos, CameraFeed> cameraView : activeFrameCameraFeeds.entrySet()) {
			GlobalPos cameraPos = cameraView.getKey();

			if (cameraPos.dimension().equals(level.dimension())) {
				BlockPos pos = cameraPos.pos();
				TileEntity te = level.getBlockEntity(pos);

				if (te instanceof SecurityCameraBlockEntity) {
					CameraFeed feed = cameraView.getValue();

					if (playerFrustum != null && !feed.hasFrameInFrustum(playerFrustum))
						continue;

					SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;
					Framebuffer frameTarget = feed.renderTarget();
					Vector3d cameraEntityPos = new Vector3d(pos.getX() + 0.5D, pos.getY() - player.getEyeHeight(Pose.STANDING) + 0.5D, pos.getZ() + 0.5D);
					float cameraXRot = be.getDefaultXRotation();
					float cameraYRot = be.getDefaultYRotation(be.getBlockState().getValue(SecurityCameraBlock.FACING)) + (float) MathHelper.lerp(partialTick, be.getOriginalCameraRotation(), be.getCameraRotation()) * (180F / (float) Math.PI);

					securityCamera.setPos(cameraEntityPos.x, cameraEntityPos.y, cameraEntityPos.z);
					mc.setCameraEntity(securityCamera);
					securityCamera.xRot = cameraXRot;
					securityCamera.yHeadRot = cameraYRot;
					currentlyCapturedCamera = cameraPos;
					feed.applyVisibleSections(mc.levelRenderer.renderChunks, mc.levelRenderer.chunksToCompile);
					profiler.push("securitycraft:discover_frame_sections");
					feed.discoverVisibleSections(cameraPos, newFrameFeedViewDistance);
					mc.levelRenderer.chunksToCompile.addAll(feed.getDirtyRenderChunks());
					profiler.popPush("securitycraft:bind_frame_target");
					frameTarget.clear(true);
					frameTarget.bindWrite(true);
					profiler.pop();

					try {
						mc.gameRenderer.renderLevel(1.0F, 0L, new MatrixStack());
					}
					catch (Exception e) {
						SecurityCraft.LOGGER.error("Frame feed at {} threw an exception while rendering the level. Deactivating clientside rendering for this feed", be.getBlockPos());
						e.printStackTrace();
						feed.markForRemoval();
					}

					frameTarget.unbindWrite();
					profiler.push("securitycraft:apply_frame_frustum");

					ClippingHelper frustum = prepareFrustum(camera);

					if (frustum != null && (be.shouldRotate() || !feed.hasVisibleSections() || feed.requiresFrustumUpdate()))
						feed.updateVisibleSections(frustum);

					profiler.pop();
				}
			}
		}

		securityCamera.kill();
		mc.setCameraEntity(oldCamEntity);
		player.setPosRaw(oldX, oldY, oldZ);
		player.xOld = player.xo = oldXO;
		player.yOld = player.yo = oldYO;
		player.zOld = player.zo = oldZO;
		player.xRot = oldXRot;
		player.xRotO = oldXRotO;
		player.yRot = oldYRot;
		player.yRotO = oldYRotO;
		camera.setup(level, oldCamEntity == null ? player : oldCamEntity, !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTick);
		camera.eyeHeight = oldEyeHeight;
		camera.eyeHeightOld = oldEyeHeightO;
		mc.options.setCameraType(oldCameraType);
		mc.options.graphicsMode = oldGraphicsMode;
		mc.gameRenderer.renderBlockOutline = oldRenderBlockOutline;
		mc.levelRenderer.renderChunks.clear();
		mc.levelRenderer.renderChunks.addAll(oldVisibleSections);
		mc.levelRenderer.itemEntityTarget = oldItemEntityTarget;
		mc.levelRenderer.weatherTarget = oldWeatherTarget;
		mc.levelRenderer.transparencyChain = oldTransparencyChain;
		window.framebufferWidth = oldWidth;
		window.framebufferHeight = oldHeight;
		mc.gameRenderer.renderHand = oldRenderHand;
		mc.gameRenderer.panoramicMode = oldPanoramicMode;
		mc.getMainRenderTarget().bindWrite(true);
		currentlyCapturedCamera = null;

		profiler.pop();
		profiler.pop();
		profiler.push("render");
	}

	@SubscribeEvent
	public static void onClientTickPost(ClientTickEvent event) {
		if (event.phase == Phase.END && hasFeeds())
			FRAME_CAMERA_FEEDS.entrySet().removeIf(e -> e.getValue().shouldBeRemoved());
	}

	public static Map<GlobalPos, CameraFeed> getFeedsToRender(double currentTime) {
		//+1 helps to reduce stuttering when many frames are active at once
		double feedsToRender = FRAME_CAMERA_FEEDS.size() + 1;
		double fpsCap = ConfigHandler.CLIENT.frameFeedFpsLimit.get();
		double frameInterval = 1.0D / fpsCap;
		double activeFramesPerMcFrame = MathHelper.ceil((fpsCap * feedsToRender) / Minecraft.fps);

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
		LocalRenderInformationContainer startingSection = Minecraft.getInstance().levelRenderer.new LocalRenderInformationContainer(CameraViewAreaExtension.rawFetch(cameraSectionPos.x(), MathHelper.clamp(cameraSectionPos.y(), 0, 15), cameraSectionPos.z(), true), null, 0);

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

		return Math.min(frameSpecificRenderDistance, Math.min(ConfigHandler.CLIENT.frameFeedRenderDistance.get(), Math.min(ConfigHandler.SERVER.frameFeedViewDistance.get(), Minecraft.getInstance().options.renderDistance)));
	}

	public static void updateLastUsedFrustum(Matrix4f renderMatrix, Matrix4f projectionMatrix) {
		if (renderMatrix != null && projectionMatrix != null)
			lastUsedFrustum = new ClippingHelper(renderMatrix, projectionMatrix);
	}

	private static ClippingHelper prepareFrustum(ActiveRenderInfo camera) {
		if (lastUsedFrustum != null) {
			Vector3d activeRenderInfoPos = camera.getPosition();

			lastUsedFrustum.prepare(activeRenderInfoPos.x, activeRenderInfoPos.y, activeRenderInfoPos.z);
		}

		return lastUsedFrustum;
	}
}
