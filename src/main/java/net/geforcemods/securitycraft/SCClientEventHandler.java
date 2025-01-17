package net.geforcemods.securitycraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.CameraController.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldRenderer.LocalRenderInformationContainer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class SCClientEventHandler {
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	public static final ResourceLocation NIGHT_VISION = new ResourceLocation("textures/mob_effect/night_vision.png");
	public static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
	private static final TranslationTextComponent REDSTONE_NOTE = Utils.localize("gui.securitycraft:camera.toggleRedstoneNote");
	private static final TranslationTextComponent SMART_MODULE_NOTE = Utils.localize("gui.securitycraft:camera.smartModuleNote");
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
	public static void onRenderLevelStage(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getInstance();
		Vector3d camPos = mc.gameRenderer.getMainCamera().getPosition();
		MatrixStack pose = event.getMatrixStack();
		World level = mc.level;

		for (BlockPos bcdPos : BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getTrackedBlockEntities(level)) {
			TileEntity be = level.getBlockEntity(bcdPos);

			if (!(be instanceof BlockChangeDetectorBlockEntity))
				continue;

			BlockChangeDetectorBlockEntity bcd = (BlockChangeDetectorBlockEntity) be;

			if (bcd.isShowingHighlights() && bcd.isOwnedBy(mc.player)) {
				int packedColor = bcd.getColor();
				float r = ColorHelper.PackedColor.red(packedColor) / 255.0F;
				float g = ColorHelper.PackedColor.green(packedColor) / 255.0F;
				float b = ColorHelper.PackedColor.blue(packedColor) / 255.0F;

				for (ChangeEntry changeEntry : bcd.getFilteredEntries()) {
					BlockPos pos = changeEntry.pos;

					pose.pushPose();
					pose.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
					WorldRenderer.renderLineBox(pose, BCDBuffer.INSTANCE.getBuffer(RenderType.lines()), 0, 0, 0, 1, 1, 1, r, g, b, 1.0F);
					pose.popPose();
				}
			}
		}

		mc.renderBuffers().bufferSource().endBatch();
	}

	@SubscribeEvent
	public static void renderHandEvent(RenderHandEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;

		if (PlayerUtils.isPlayerMountedOnCamera(player))
			event.setCanceled(true);
		else {
			boolean mainHandTaser = player.getMainHandItem().getItem() instanceof TaserItem;
			boolean offhandTaser = player.getOffhandItem().getItem() instanceof TaserItem;

			if (mainHandTaser || offhandTaser) {
				boolean isRightHanded = Minecraft.getInstance().options.mainHand == HandSide.RIGHT;
				boolean isMainHand = event.getHand() == Hand.MAIN_HAND;

				if (mainHandTaser && offhandTaser)
					event.setCanceled(!isMainHand);
				else if ((isMainHand && offhandTaser || !isMainHand && mainHandTaser)) {
					event.setCanceled(true);
					return;
				}

				if (isRightHanded == isMainHand)
					event.getMatrixStack().translate(-0.54F, 0.0F, 0.0F);
				else
					event.getMatrixStack().translate(0.58F, 0.0F, 0.0F);
			}
		}
	}

	@SubscribeEvent
	public static void onClickInput(InputEvent.ClickInputEvent event) {
		if (event.isAttack() && ClientHandler.isPlayerMountedOnCamera()) {
			event.setCanceled(true);
			event.setSwingHand(false);
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (event.getWorld().isClientSide()) {
			ChunkPos pos = event.getChunk().getPos();

			CameraViewAreaExtension.onChunkUnload(pos.x, pos.z);
		}
	}

	@SubscribeEvent
	public static void onRenderFramePost(RenderTickEvent event) {
		if (event.phase == Phase.END)
			return;

		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;

		if (player == null || CameraController.FRAME_CAMERA_FEEDS.isEmpty() || !ConfigHandler.SERVER.frameFeedViewingEnabled.get())
			return;

		IProfiler profiler = mc.getProfiler();
		Map<GlobalPos, CameraFeed> activeFrameCameraFeeds;
		//+1 helps to reduce stuttering when many frames are active at once
		double feedsToRender = CameraController.FRAME_CAMERA_FEEDS.size() + 1;
		double fpsCap = ConfigHandler.CLIENT.frameFeedFpsLimit.get();
		double currentTime = GLFW.glfwGetTime();
		double frameInterval = 1.0D / fpsCap;
		double activeFramesPerMcFrame = MathHelper.ceil((fpsCap * feedsToRender) / Minecraft.fps);

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

		World level = player.level;
		float partialTick = event.renderTickTime;
		ActiveRenderInfo camera = mc.gameRenderer.getMainCamera();
		Entity oldCamEntity = mc.cameraEntity;
		MainWindow window = mc.getWindow();
		int oldWidth = window.getWidth();
		int oldHeight = window.getHeight();
		List<WorldRenderer.LocalRenderInformationContainer> oldVisibleSections = new ObjectArrayList<>(mc.levelRenderer.renderChunks);
		int oldServerRenderDistance = mc.options.renderDistance;
		int newFrameFeedViewDistance = CameraController.getFrameFeedViewDistance(null);
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
		//TODO: marker clone
		Entity securityCamera = EntityType.ARMOR_STAND.create(level); //A separate entity is used instead of moving the player to allow the player to see themselves
		//		ClippingHelper playerFrustum = mc.levelRenderer.capturedFrustum; //Saved once before the loop, because the frustum changes depending on which camera is viewed

		mc.gameRenderer.renderBlockOutline = false;
		mc.gameRenderer.renderHand = false;
		mc.gameRenderer.panoramicMode = true;
		mc.options.renderDistance = newFrameFeedViewDistance;
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
					//					if (!isFrameInFrustum(cameraPos, playerFrustum))
					//						continue;

					SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;
					CameraFeed feed = cameraView.getValue();
					Framebuffer frameTarget = feed.renderTarget();
					Vector3d cameraEntityPos = new Vector3d(pos.getX() + 0.5D, pos.getY() - player.getEyeHeight(Pose.STANDING) + 0.5D, pos.getZ() + 0.5D);
					float cameraXRot = be.getDefaultXRotation();
					float cameraYRot = be.getDefaultYRotation(be.getBlockState().getValue(SecurityCameraBlock.FACING)) + (float) MathHelper.lerp(partialTick, be.getOriginalCameraRotation(), be.getCameraRotation()) * (180F / (float) Math.PI);

					securityCamera.setPos(cameraEntityPos.x, cameraEntityPos.y, cameraEntityPos.z);
					mc.setCameraEntity(securityCamera);
					securityCamera.xRot = cameraXRot;
					securityCamera.yRot = cameraYRot;
					CameraController.currentlyCapturedCamera = cameraPos;
					mc.levelRenderer.renderChunks.clear();
					mc.levelRenderer.renderChunks.addAll(feed.visibleSections());

					//					if (SecurityCraft.IS_A_SODIUM_MOD_INSTALLED)
					//						SodiumCompat.clearRenderList();

					profiler.push("securitycraft:discover_frame_sections");
					CameraController.discoverVisibleSections(cameraPos, newFrameFeedViewDistance, feed);
					profiler.popPush("securitycraft:bind_frame_target");
					frameTarget.clear(true);
					frameTarget.bindWrite(true);
					profiler.pop();
					mc.gameRenderer.renderLevel(1.0F, 0L, new MatrixStack());
					frameTarget.unbindWrite();
					profiler.push("securitycraft:apply_frame_frustum");

					if (be.shouldRotate() || feed.visibleSections().isEmpty() || CameraController.FEED_FRUSTUM_UPDATE_REQUIRED.contains(cameraPos)) {
						CameraController.FEED_FRUSTUM_UPDATE_REQUIRED.remove(cameraPos);
						feed.visibleSections().clear();

						for (LocalRenderInformationContainer section : feed.sectionsInRange()) {
							//							if (playerFrustum.isVisible(section.chunk.bb))
							feed.visibleSections().add(section);
						}
					}

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
		mc.gameRenderer.renderBlockOutline = oldRenderBlockOutline;
		mc.levelRenderer.renderChunks.clear();
		mc.levelRenderer.renderChunks.addAll(oldVisibleSections);
		window.framebufferWidth = oldWidth;
		window.framebufferHeight = oldHeight;
		mc.options.renderDistance = oldServerRenderDistance;
		mc.gameRenderer.renderHand = oldRenderHand;
		mc.gameRenderer.panoramicMode = oldPanoramicMode;
		mc.getMainRenderTarget().bindWrite(true);
		CameraController.currentlyCapturedCamera = null;

		profiler.pop();
		profiler.pop();
	}

	private static boolean isFrameInFrustum(GlobalPos cameraPos, ClippingHelper beFrustum) {
		for (BlockPos framePos : CameraController.FRAME_LINKS.get(cameraPos)) {
			if (beFrustum.isVisible(new AxisAlignedBB(framePos)))
				return true;
		}

		return false;
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if ((event.getType() == ElementType.EXPERIENCE || event.getType() == ElementType.JUMPBAR || event.getType() == ElementType.POTION_ICONS) && ClientHandler.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.ALL && ClientHandler.isPlayerMountedOnCamera()) {
			Minecraft mc = Minecraft.getInstance();

			drawCameraOverlay(event.getMatrixStack(), mc, mc.gui, mc.getWindow(), mc.player, mc.level, mc.cameraEntity.blockPosition());
		}
	}

	private static void drawCameraOverlay(MatrixStack matrix, Minecraft mc, AbstractGui gui, MainWindow resolution, PlayerEntity player, World level, BlockPos pos) {
		if (mc.options.renderDebug)
			return;

		TileEntity te = level.getBlockEntity(pos);

		if (!(te instanceof SecurityCameraBlockEntity))
			return;

		int scaledWidth = resolution.getGuiScaledWidth();
		int scaledHeight = resolution.getGuiScaledHeight();
		FontRenderer font = Minecraft.getInstance().font;
		GameSettings settings = mc.options;
		SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;
		BlockState state = level.getBlockState(pos);
		long dayTime = Minecraft.getInstance().level.getDayTime();
		int hours24 = (int) ((float) dayTime / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (dayTime / 16.666666F % 60.0F);
		String time = String.format("%02d:%02d %s", Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM");
		int timeY = 25;

		if (be.hasCustomName()) {
			ITextComponent cameraName = be.getCustomName();

			font.drawShadow(matrix, cameraName, scaledWidth - font.width(cameraName) - 8, 25, 0xFFFFFF);
			timeY += 10;
		}

		font.drawShadow(matrix, time, scaledWidth - font.width(time) - 4, timeY, 0xFFFFFF);

		int heightOffset = 10;

		for (int i = CAMERA_KEY_INFO_LIST.length - 1; i >= 0; i--) {
			CameraKeyInfoEntry entry = CAMERA_KEY_INFO_LIST[i];

			if (entry.enabled().get()) {
				entry.drawString(settings, matrix, font, scaledWidth, scaledHeight, heightOffset, be);
				heightOffset += 10;
			}
		}

		mc.getTextureManager().bind(CAMERA_DASHBOARD);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		gui.blit(matrix, 5, 0, 0, 0, 90, 20);
		gui.blit(matrix, resolution.getGuiScaledWidth() - 70, 5, 190, 0, 65, 30);

		if (!player.hasEffect(Effects.NIGHT_VISION))
			gui.blit(matrix, 28, 4, 90, 12, 16, 11);
		else {
			mc.getTextureManager().bind(NIGHT_VISION);
			AbstractGui.blit(matrix, 27, -1, 0, 0, 18, 18, 18, 18);
		}

		if (state.getSignal(level, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
			if (!be.isModuleEnabled(ModuleType.REDSTONE))
				CameraRedstoneModuleState.NOT_INSTALLED.render(gui, matrix, 12, 2);
			else
				CameraRedstoneModuleState.DEACTIVATED.render(gui, matrix, 12, 2);
		}
		else
			CameraRedstoneModuleState.ACTIVATED.render(gui, matrix, 12, 2);
	}

	private enum BCDBuffer implements IRenderTypeBuffer {
		INSTANCE;

		private final RenderType overlayLines = new OverlayLines(RenderType.lines());

		@Override
		public IVertexBuilder getBuffer(RenderType renderType) {
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

				Framebuffer renderTarget = Minecraft.getInstance().levelRenderer.entityTarget();

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

	public static final class CameraKeyInfoEntry {
		private final Supplier<Boolean> enabled;
		private final Function<GameSettings, ITextComponent> text;
		private final Predicate<SecurityCameraBlockEntity> whiteText;

		public CameraKeyInfoEntry(Supplier<Boolean> enabled, Function<GameSettings, ITextComponent> text, Predicate<SecurityCameraBlockEntity> whiteText) {
			this.enabled = enabled;
			this.text = text;
			this.whiteText = whiteText;
		}

		public void drawString(GameSettings options, MatrixStack matrix, FontRenderer font, int scaledWidth, int scaledHeight, int heightOffset, SecurityCameraBlockEntity be) {
			ITextComponent text = text().apply(options);
			boolean whiteText = whiteText().test(be);

			font.drawShadow(matrix, text, scaledWidth - font.width(text) - 8, scaledHeight - heightOffset, whiteText ? 0xFFFFFF : 0xFF3377);
		}

		public Supplier<Boolean> enabled() {
			return enabled;
		}

		public Function<GameSettings, ITextComponent> text() {
			return text;
		}

		public Predicate<SecurityCameraBlockEntity> whiteText() {
			return whiteText;
		}
	}
}
