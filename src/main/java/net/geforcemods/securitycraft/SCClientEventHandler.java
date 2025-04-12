package net.geforcemods.securitycraft;

import java.util.ArrayList;
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
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class SCClientEventHandler {
	public static final ResourceLocation BACKGROUND_SPRITE = SecurityCraft.resLoc("hud/camera/background");
	public static final ResourceLocation LIVE_SPRITE = SecurityCraft.resLoc("hud/camera/live");
	public static final ResourceLocation NIGHT_VISION_INACTIVE_SPRITE = SecurityCraft.resLoc("hud/camera/night_vision_inactive");
	public static final ResourceLocation REDSTONE_MODULE_NOT_PRESENT_SPRITE = SecurityCraft.resLoc("hud/camera/redstone_module_not_present");
	public static final ResourceLocation REDSTONE_MODULE_PRESENT_SPRITE = SecurityCraft.resLoc("hud/camera/redstone_module_present");
	public static final ResourceLocation NIGHT_VISION = SecurityCraft.mcResLoc("textures/mob_effect/night_vision.png");
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
	private static final List<DeferredHolder<DataComponentType<?>, ? extends DataComponentType<? extends TooltipProvider>>> COMPONENTS_WITH_GLOBAL_TOOLTIP = List.of(SCContent.OWNER_DATA, SCContent.NOTES);

	private SCClientEventHandler() {}

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() == Stage.AFTER_TRIPWIRE_BLOCKS) {
			Vec3 camPos = event.getCamera().getPosition();
			PoseStack pose = event.getPoseStack();
			Minecraft mc = Minecraft.getInstance();
			Level level = mc.level;
			VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(ClientHandler.OVERLAY_LINES);

			for (BlockPos bcdPos : BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getTrackedBlockEntities(level)) {
				BlockEntity be = level.getBlockEntity(bcdPos);

				if (be instanceof BlockChangeDetectorBlockEntity bcd && bcd.isShowingHighlights() && bcd.isOwnedBy(mc.player)) {
					int packedColor = bcd.getColor();
					float r = ARGB.red(packedColor) / 255.0F;
					float g = ARGB.green(packedColor) / 255.0F;
					float b = ARGB.blue(packedColor) / 255.0F;

					for (ChangeEntry changeEntry : bcd.getFilteredEntries()) {
						BlockPos pos = changeEntry.pos();

						pose.pushPose();
						pose.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
						ShapeRenderer.renderLineBox(pose, consumer, 0, 0, 0, 1, 1, 1, r, g, b, 1.0F);
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
	public static void onRenderFramePost(RenderFrameEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;

		if (player == null || player.connection.getLevel() == null || CameraController.FRAME_CAMERA_FEEDS.isEmpty() || !ConfigHandler.SERVER.frameFeedViewingEnabled.get())
			return;

		ProfilerFiller profiler = Profiler.get();
		Map<GlobalPos, CameraFeed> activeFrameCameraFeeds;
		List<GlobalPos> erroringFrameCameraFeeds = new ArrayList<>();
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

		Level level = player.level();
		DeltaTracker partialTick = event.getPartialTick();
		Camera camera = mc.gameRenderer.getMainCamera();
		Entity oldCamEntity = mc.cameraEntity;
		Window window = mc.getWindow();
		int oldWidth = window.getWidth();
		int oldHeight = window.getHeight();
		List<SectionRenderDispatcher.RenderSection> oldVisibleSections = mc.levelRenderer.visibleSections.clone();
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
		Frustum playerFrustum = mc.levelRenderer.getFrustum(); //Saved once before the loop, because the frustum changes depending on which camera is viewed

		mc.gameRenderer.setRenderBlockOutline(false);
		mc.gameRenderer.setRenderHand(false);
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

					RenderTarget frameTarget = feed.renderTarget();
					Vec3 cameraEntityPos = new Vec3(pos.getX() + 0.5D, pos.getY() - player.getDimensions(Pose.STANDING).eyeHeight() + 0.5D, pos.getZ() + 0.5D);
					float cameraXRot = be.getDefaultXRotation();
					float cameraYRot = be.getDefaultYRotation(be.getBlockState().getValue(SecurityCameraBlock.FACING)) + (float) Mth.lerp(partialTick.getGameTimeDeltaPartialTick(false), be.getOriginalCameraRotation(), be.getCameraRotation()) * Mth.RAD_TO_DEG;

					securityCamera.setPos(cameraEntityPos);
					mc.setCameraEntity(securityCamera);
					securityCamera.setXRot(cameraXRot);
					securityCamera.setYRot(cameraYRot);
					CameraController.currentlyCapturedCamera = cameraPos;
					feed.applyVisibleSections(mc.levelRenderer.visibleSections);
					profiler.push("securitycraft:discover_frame_sections");
					feed.discoverVisibleSections(cameraPos, newFrameFeedViewDistance);
					profiler.popPush("securitycraft:bind_frame_target");
					frameTarget.clear();
					frameTarget.bindWrite(true);
					profiler.pop();

					try {
						mc.gameRenderer.renderLevel(DeltaTracker.ONE);
					}
					catch (Exception e) {
						SecurityCraft.LOGGER.error("Frame feed at {} threw an exception while rendering the level. Deactivating clientside rendering for this feed", be.getBlockPos());
						e.printStackTrace();
						erroringFrameCameraFeeds.add(cameraPos);
					}

					frameTarget.unbindWrite();
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
		mc.gameRenderer.setRenderHand(true);
		mc.gameRenderer.setPanoramicMode(false);
		mc.getMainRenderTarget().bindWrite(true);
		CameraController.currentlyCapturedCamera = null;

		profiler.pop();
		profiler.pop();

		for (GlobalPos erroringFeed : erroringFrameCameraFeeds) {
			CameraController.removeAllFrameLinks(erroringFeed);
		}
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		TooltipContext ctx = event.getContext();
		List<Component> tooltip = event.getToolTip();
		TooltipFlag flag = event.getFlags();
		int nextIndex = 0;

		for (int i = 0; i < COMPONENTS_WITH_GLOBAL_TOOLTIP.size(); i++) {
			TooltipProvider tooltipProvider = stack.get(COMPONENTS_WITH_GLOBAL_TOOLTIP.get(i));

			if (tooltipProvider != null) {
				final int index = nextIndex++ + 1; //add in order of global tooltip components list, after item name

				tooltipProvider.addToTooltip(ctx, line -> {
					if (index >= tooltip.size())
						tooltip.add(line);
					else
						tooltip.add(index, line);
				}, flag);
			}
		}
	}

	public static void cameraOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		Level level = mc.level;
		BlockPos pos = mc.cameraEntity.blockPosition();
		Window window = mc.getWindow();
		int scaledWidth = window.getGuiScaledWidth();
		int scaledHeight = window.getGuiScaledHeight();

		if (mc.getDebugOverlay().showDebugScreen())
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

			guiGraphics.drawString(font, cameraName, scaledWidth - font.width(cameraName) - 8, 25, 0xFFFFFF, true);
			timeY += 10;
		}

		guiGraphics.drawString(font, time, scaledWidth - font.width(time) - 4, timeY, 0xFFFFFF, true);

		int heightOffset = 10;

		for (int i = CAMERA_KEY_INFO_LIST.length - 1; i >= 0; i--) {
			CameraKeyInfoEntry entry = CAMERA_KEY_INFO_LIST[i];

			if (entry.enabled().get()) {
				entry.drawString(options, guiGraphics, font, scaledWidth, scaledHeight, heightOffset, be);
				heightOffset += 10;
			}
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		guiGraphics.blitSprite(RenderType::guiTextured, BACKGROUND_SPRITE, 5, 0, 90, 20);
		guiGraphics.blitSprite(RenderType::guiTextured, LIVE_SPRITE, window.getGuiScaledWidth() - 70, 5, 65, 16);

		if (!mc.player.hasEffect(MobEffects.NIGHT_VISION))
			guiGraphics.blitSprite(RenderType::guiTextured, NIGHT_VISION_INACTIVE_SPRITE, 28, 4, 16, 9);
		else
			guiGraphics.blit(RenderType::guiTextured, NIGHT_VISION, 27, -1, 0, 0, 18, 18, 18, 18);

		if (state.getSignal(level, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
			if (!be.isModuleEnabled(ModuleType.REDSTONE))
				CameraRedstoneModuleState.NOT_INSTALLED.render(guiGraphics, 12, 2);
			else
				CameraRedstoneModuleState.DEACTIVATED.render(guiGraphics, 12, 2);
		}
		else
			CameraRedstoneModuleState.ACTIVATED.render(guiGraphics, 12, 2);
	}

	public record CameraKeyInfoEntry(Supplier<Boolean> enabled, Function<Options, Component> text, Predicate<SecurityCameraBlockEntity> whiteText) {
		public void drawString(Options options, GuiGraphics guiGraphics, Font font, int scaledWidth, int scaledHeight, int heightOffset, SecurityCameraBlockEntity be) {
			Component text = text().apply(options);
			boolean whiteText = whiteText().test(be);

			guiGraphics.drawString(font, text, scaledWidth - font.width(text) - 8, scaledHeight - heightOffset, whiteText ? 0xFFFFFF : 0xFF3377, true);
		}
	}
}
