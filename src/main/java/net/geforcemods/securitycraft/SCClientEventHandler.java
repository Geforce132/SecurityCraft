package net.geforcemods.securitycraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.CameraController.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelRenderer.RenderChunkInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class SCClientEventHandler {
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	public static final ResourceLocation NIGHT_VISION = new ResourceLocation("textures/mob_effect/night_vision.png");
	public static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
	private static final Component REDSTONE_NOTE = Utils.localize("gui.securitycraft:camera.toggleRedstoneNote");
	private static final Component SMART_MODULE_NOTE = Utils.localize("gui.securitycraft:camera.smartModuleNote");
	//@formatter:off
	private static final CameraKeyInfoEntry[] CAMERA_KEY_INFO_LIST = {
			new CameraKeyInfoEntry(() -> true, options -> Utils.localize("gui.securitycraft:camera.lookAround", options.keyUp.getTranslatedKeyMessage(), options.keyLeft.getTranslatedKeyMessage(), options.keyDown.getTranslatedKeyMessage(), options.keyRight.getTranslatedKeyMessage()), $ -> true),
			new CameraKeyInfoEntry(() -> true, options -> Utils.localize("gui.securitycraft:camera.exit", options.keyShift.getTranslatedKeyMessage()), $ -> true),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.zoom", KeyBindings.cameraZoomIn.getTranslatedKeyMessage(), KeyBindings.cameraZoomOut.getTranslatedKeyMessage()), $ -> true),
			new CameraKeyInfoEntry(ConfigHandler.SERVER.allowCameraNightVision::get, $ -> Utils.localize("gui.securitycraft:camera.activateNightVision", KeyBindings.cameraActivateNightVision.getTranslatedKeyMessage()), $ -> true),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.toggleRedstone", KeyBindings.cameraEmitRedstone.getTranslatedKeyMessage()), be -> be.isModuleEnabled(ModuleType.REDSTONE)),
			new CameraKeyInfoEntry(() -> true, $ -> REDSTONE_NOTE, be -> be.isModuleEnabled(ModuleType.REDSTONE)),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.setDefaultViewingDirection", KeyBindings.setDefaultViewingDirection.getTranslatedKeyMessage()), be -> be.isModuleEnabled(ModuleType.SMART)),
			new CameraKeyInfoEntry(() -> true, $ -> SMART_MODULE_NOTE, be -> be.isModuleEnabled(ModuleType.SMART))
	};
	//@formatter:on

	private SCClientEventHandler() {}

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() == Stage.AFTER_TRIPWIRE_BLOCKS) {
			Vec3 camPos = event.getCamera().getPosition();
			PoseStack pose = event.getPoseStack();
			Minecraft mc = Minecraft.getInstance();
			Level level = mc.level;

			for (BlockPos bcdPos : BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getTrackedBlockEntities(level)) {
				BlockEntity be = level.getBlockEntity(bcdPos);

				if (be instanceof BlockChangeDetectorBlockEntity bcd && bcd.isShowingHighlights() && bcd.isOwnedBy(mc.player)) {
					int packedColor = bcd.getColor();
					float r = FastColor.ARGB32.red(packedColor) / 255.0F;
					float g = FastColor.ARGB32.green(packedColor) / 255.0F;
					float b = FastColor.ARGB32.blue(packedColor) / 255.0F;

					for (ChangeEntry changeEntry : bcd.getFilteredEntries()) {
						BlockPos pos = changeEntry.pos();

						pose.pushPose();
						pose.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
						LevelRenderer.renderLineBox(pose, BCDBuffer.INSTANCE.getBuffer(RenderType.lines()), 0, 0, 0, 1, 1, 1, r, g, b, 1.0F);
						pose.popPose();
					}
				}
			}

			mc.renderBuffers().bufferSource().endBatch();
		}
	}

	@SubscribeEvent
	public static void renderHandEvent(RenderHandEvent event) {
		if (ClientHandler.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
		if (ClientHandler.isPlayerMountedOnCamera()) {
			Minecraft mc = Minecraft.getInstance();
			InteractionHand hand = event.getHand();

			if (mc.player.getItemInHand(hand).is(SCContent.CAMERA_MONITOR.get()) && event.isUseItem())
				SCContent.CAMERA_MONITOR.get().use(mc.level, mc.player, hand);

			event.setCanceled(true);
			event.setSwingHand(false);
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (event.getLevel().isClientSide()) {
			ChunkPos pos = event.getChunk().getPos();

			CameraViewAreaExtension.onChunkUnload(pos.x, pos.z);
		}
	}

	@SubscribeEvent
	public static void onRenderFramePost(RenderTickEvent event) {
		if (event.phase == Phase.START)
			return;

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;

		if (player == null || CameraController.FRAME_CAMERA_FEEDS.isEmpty() || !ConfigHandler.SERVER.frameFeedViewingEnabled.get())
			return;

		ProfilerFiller profiler = mc.getProfiler();
		Map<GlobalPos, CameraFeed> activeFrameCameraFeeds;
		//+1 helps to reduce stuttering when many frames are active at once
		double feedsToRender = CameraController.FRAME_CAMERA_FEEDS.size() + 1;
		double fpsCap = ConfigHandler.CLIENT.frameFeedFpsLimit.get();
		double currentTime = GLFW.glfwGetTime();
		double frameInterval = 1.0D / fpsCap;
		double activeFramesPerMcFrame = Mth.ceil((fpsCap * feedsToRender) / mc.getFps());

		if (fpsCap < 260.0D) {
			activeFrameCameraFeeds = new HashMap<>();

			for (Entry<GlobalPos, CameraFeed> cameraView : CameraController.FRAME_CAMERA_FEEDS.entrySet()) {
				double timeBetweenFrames = frameInterval / feedsToRender;
				double lastActiveTime = cameraView.getValue().lastActiveTime().get();

				if (currentTime < lastActiveTime + frameInterval || currentTime < CameraController.lastFrameRendered + timeBetweenFrames || activeFramesPerMcFrame-- <= 0)
					continue;

				cameraView.getValue().lastActiveTime().set(currentTime);
				activeFrameCameraFeeds.put(cameraView.getKey(), cameraView.getValue());
			}
		}
		else
			activeFrameCameraFeeds = CameraController.FRAME_CAMERA_FEEDS;

		if (activeFrameCameraFeeds.isEmpty())
			return;

		CameraController.lastFrameRendered = currentTime;
		profiler.push("gameRenderer");
		profiler.push("securitycraft:frame_level");

		Level level = player.level;
		float partialTick = event.renderTickTime;
		Camera camera = mc.gameRenderer.getMainCamera();
		Entity oldCamEntity = mc.cameraEntity;
		Window window = mc.getWindow();
		int oldWidth = window.getWidth();
		int oldHeight = window.getHeight();
		List<LevelRenderer.RenderChunkInfo> oldVisibleSections = mc.levelRenderer.renderChunksInFrustum.clone();
		int oldServerRenderDistance = mc.options.serverRenderDistance;
		int newFrameFeedViewDistance = CameraController.getFrameFeedViewDistance(null);
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
		Frustum playerFrustum = getFrustum(mc.levelRenderer); //Saved once before the loop, because the frustum changes depending on which camera is viewed

		mc.gameRenderer.setRenderBlockOutline(false);
		mc.gameRenderer.setRenderHand(false);
		mc.gameRenderer.setPanoramicMode(true);
		mc.levelRenderer.graphicsChanged();
		mc.options.setServerRenderDistance(newFrameFeedViewDistance);
		window.setWidth(100);
		window.setHeight(100); //Different width/height values seem to have no effect, although the ratio needs to be 1:1
		mc.options.setCameraType(CameraType.FIRST_PERSON);
		camera.eyeHeight = camera.eyeHeightOld = player.getEyeHeight(Pose.STANDING);
		mc.renderBuffers().bufferSource().endBatch(); //Makes sure that previous world rendering is done

		for (Entry<GlobalPos, CameraFeed> cameraView : activeFrameCameraFeeds.entrySet()) {
			GlobalPos cameraPos = cameraView.getKey();

			if (cameraPos.dimension().equals(level.dimension())) {
				BlockPos pos = cameraPos.pos();

				if (level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be) {
					if (!isFrameInFrustum(cameraPos, playerFrustum))
						continue;

					CameraFeed feed = cameraView.getValue();
					RenderTarget frameTarget = feed.renderTarget();
					Vec3 cameraEntityPos = new Vec3(pos.getX() + 0.5D, pos.getY() - player.getEyeHeight(Pose.STANDING) + 0.5D, pos.getZ() + 0.5D);
					float cameraXRot = be.getDefaultXRotation();
					float cameraYRot = be.getDefaultYRotation(be.getBlockState().getValue(SecurityCameraBlock.FACING)) + (float) Mth.lerp(partialTick, be.getOriginalCameraRotation(), be.getCameraRotation()) * Mth.RAD_TO_DEG;

					securityCamera.setPos(cameraEntityPos);
					mc.setCameraEntity(securityCamera);
					securityCamera.setXRot(cameraXRot);
					securityCamera.setYRot(cameraYRot);
					CameraController.currentlyCapturedCamera = cameraPos;
					mc.levelRenderer.renderChunksInFrustum.clear();
					mc.levelRenderer.renderChunksInFrustum.addAll(feed.visibleSections());
					profiler.push("securitycraft:discover_frame_sections");
					CameraController.discoverVisibleSections(cameraPos, newFrameFeedViewDistance, feed);
					profiler.popPush("securitycraft:bind_frame_target");
					frameTarget.clear(true);
					frameTarget.bindWrite(true);
					profiler.pop();
					mc.gameRenderer.renderLevel(1.0F, 0L, new PoseStack());
					frameTarget.unbindWrite();
					profiler.push("securitycraft:apply_frame_frustum");

					Frustum frustum = new Frustum(getFrustum(mc.levelRenderer)).offsetToFullyIncludeCameraCube(8); //This needs the frame's newly calculated frustum, so it needs to be queried from inside the loop

					if (be.shouldRotate() || feed.visibleSections().isEmpty() || CameraController.FEED_FRUSTUM_UPDATE_REQUIRED.contains(cameraPos)) {
						CameraController.FEED_FRUSTUM_UPDATE_REQUIRED.remove(cameraPos);
						feed.visibleSections().clear();

						for (RenderChunkInfo section : feed.sectionsInRange()) {
							if (frustum.isVisible(section.chunk.getBoundingBox()))
								feed.visibleSections().add(section);
						}
					}

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
		camera.setup(level, oldCamEntity == null ? player : oldCamEntity, !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTick);
		camera.eyeHeight = oldEyeHeight;
		camera.eyeHeightOld = oldEyeHeightO;
		mc.options.setCameraType(oldCameraType);
		mc.gameRenderer.setRenderBlockOutline(true);
		mc.levelRenderer.renderChunksInFrustum.clear();
		mc.levelRenderer.renderChunksInFrustum.addAll(oldVisibleSections);
		window.setWidth(oldWidth);
		window.setHeight(oldHeight);
		mc.options.setServerRenderDistance(oldServerRenderDistance);
		mc.gameRenderer.setRenderHand(true);
		mc.gameRenderer.setPanoramicMode(false);
		mc.levelRenderer.graphicsChanged();
		mc.getMainRenderTarget().bindWrite(true);
		CameraController.currentlyCapturedCamera = null;

		profiler.pop();
		profiler.pop();
	}

	public static Frustum getFrustum(LevelRenderer levelRenderer) {
		return levelRenderer.capturedFrustum != null ? levelRenderer.capturedFrustum : levelRenderer.cullingFrustum;
	}

	private static boolean isFrameInFrustum(GlobalPos cameraPos, Frustum beFrustum) {
		for (BlockPos framePos : CameraController.FRAME_LINKS.get(cameraPos)) {
			if (beFrustum.isVisible(new AABB(framePos)))
				return true;
		}

		return false;
	}

	public static void cameraOverlay(ForgeGui gui, PoseStack pose, float partialTicks, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		Level level = mc.level;
		BlockPos pos = mc.cameraEntity.blockPosition();
		Window window = mc.getWindow();
		int scaledWidth = window.getGuiScaledWidth();
		int scaledHeight = window.getGuiScaledHeight();

		if (mc.options.renderDebug)
			return;

		if (!(level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be))
			return;

		Font font = Minecraft.getInstance().font;
		Options options = Minecraft.getInstance().options;
		BlockState state = level.getBlockState(pos);
		long dayTime = Minecraft.getInstance().level.getDayTime();
		int hours24 = (int) ((float) dayTime / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (dayTime / 16.666666F % 60.0F);
		String time = String.format("%02d:%02d %s", Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM");
		int timeY = 25;

		if (be.hasCustomName()) {
			Component cameraName = be.getCustomName();

			font.drawShadow(pose, cameraName, scaledWidth - font.width(cameraName) - 8, 25, 0xFFFFFF);
			timeY += 10;
		}

		font.drawShadow(pose, time, scaledWidth - font.width(time) - 4, timeY, 0xFFFFFF);

		int heightOffset = 10;

		for (int i = CAMERA_KEY_INFO_LIST.length - 1; i >= 0; i--) {
			CameraKeyInfoEntry entry = CAMERA_KEY_INFO_LIST[i];

			if (entry.enabled().get()) {
				entry.drawString(options, pose, font, scaledWidth, scaledHeight, heightOffset, be);
				heightOffset += 10;
			}
		}

		RenderSystem._setShaderTexture(0, CAMERA_DASHBOARD);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		GuiComponent.blit(pose, 5, 0, 0, 0, 90, 20);
		GuiComponent.blit(pose, window.getGuiScaledWidth() - 70, 5, 190, 0, 65, 30);

		if (!mc.player.hasEffect(MobEffects.NIGHT_VISION))
			GuiComponent.blit(pose, 28, 4, 90, 12, 16, 11);
		else {
			RenderSystem._setShaderTexture(0, NIGHT_VISION);
			GuiComponent.blit(pose, 27, -1, 0, 0, 18, 18, 18, 18);
		}

		if (state.getSignal(level, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
			if (!be.isModuleEnabled(ModuleType.REDSTONE))
				CameraRedstoneModuleState.NOT_INSTALLED.render(pose, 12, 2);
			else
				CameraRedstoneModuleState.DEACTIVATED.render(pose, 12, 2);
		}
		else
			CameraRedstoneModuleState.ACTIVATED.render(pose, 12, 2);
	}

	private enum BCDBuffer implements MultiBufferSource {
		INSTANCE;

		private final RenderType overlayLines = new OverlayLines(RenderType.lines());

		@Override
		public VertexConsumer getBuffer(RenderType renderType) {
			return Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(overlayLines);
		}

		private static class OverlayLines extends RenderType {
			private final RenderType normalLines;

			private OverlayLines(RenderType normalLines) {
				super("overlay_lines", normalLines.format(), normalLines.mode(), normalLines.bufferSize(), normalLines.affectsCrumbling(), normalLines.sortOnUpload, normalLines::setupRenderState, normalLines::clearRenderState);
				this.normalLines = normalLines;
			}

			@Override
			public void setupRenderState() {
				normalLines.setupRenderState();

				RenderTarget renderTarget = Minecraft.getInstance().levelRenderer.entityTarget();

				if (renderTarget != null)
					renderTarget.bindWrite(false);
			}

			@Override
			public void clearRenderState() {
				Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
				normalLines.clearRenderState();
			}
		}
	}

	public record CameraKeyInfoEntry(Supplier<Boolean> enabled, Function<Options, Component> text, Predicate<SecurityCameraBlockEntity> whiteText) {
		public void drawString(Options options, PoseStack pose, Font font, int scaledWidth, int scaledHeight, int heightOffset, SecurityCameraBlockEntity be) {
			Component text = text().apply(options);
			boolean whiteText = whiteText().test(be);

			font.drawShadow(pose, text, scaledWidth - font.width(text) - 8, scaledHeight - heightOffset, whiteText ? 0xFFFFFF : 0xFF3377);
		}
	}
}
