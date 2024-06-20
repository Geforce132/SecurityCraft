package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.compat.embeddium.EmbeddiumCompat;
import net.geforcemods.securitycraft.compat.ium.IumCompat;
import net.geforcemods.securitycraft.entity.camera.CameraController;
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
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
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
					float r = FastColor.ARGB32.red(packedColor) / 255.0F;
					float g = FastColor.ARGB32.green(packedColor) / 255.0F;
					float b = FastColor.ARGB32.blue(packedColor) / 255.0F;

					for (ChangeEntry changeEntry : bcd.getFilteredEntries()) {
						BlockPos pos = changeEntry.pos();

						pose.pushPose();
						pose.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
						LevelRenderer.renderLineBox(pose, consumer, 0, 0, 0, 1, 1, 1, r, g, b, 1.0F);
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
		ChunkPos pos = event.getChunk().getPos();

		CameraViewAreaExtension.onChunkUnload(pos.x, pos.z);
	}

	@SubscribeEvent
	public static void onRenderFramePost(RenderFrameEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		DeltaTracker partialTick = event.getPartialTick();

		if (player == null || CameraController.FRAME_CAMERA_FEEDS.isEmpty())
			return;

		Level level = player.level();
		Camera camera = mc.gameRenderer.getMainCamera();

		Entity oldCamEntity = mc.cameraEntity;
		Window window = mc.getWindow();
		int oldWidth = window.getWidth();
		int oldHeight = window.getHeight();
		List<SectionRenderDispatcher.RenderSection> oldVisibleSections = mc.levelRenderer.visibleSections.clone();
		int oldServerRenderDistance = mc.options.serverRenderDistance;

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

		mc.gameRenderer.setRenderBlockOutline(false);
		mc.gameRenderer.setRenderHand(false);
		mc.gameRenderer.setPanoramicMode(true);
		mc.levelRenderer.graphicsChanged();

		for (Map.Entry<GlobalPos, CameraController.CameraFeed> cameraView : CameraController.FRAME_CAMERA_FEEDS.entrySet()) {
			if (!cameraView.getKey().dimension().equals(level.dimension()))
				continue;

			BlockPos cameraPos = cameraView.getKey().pos();

			if (level.getBlockEntity(cameraPos) instanceof SecurityCameraBlockEntity be) {
				if (!be.isOwnedBy(player) && !be.isAllowed(player))
					continue;

				CameraController.CameraFeed feed = cameraView.getValue();
				RenderTarget frameTarget = feed.renderTarget;
				Entity securityCamera = new Marker(EntityType.MARKER, level); //We are using a separate entity instead of moving the player to allow the player to see itself
				Vec3 cameraEntityPos = new Vec3(cameraPos.getX() + 0.5D, cameraPos.getY() - player.getDimensions(Pose.STANDING).eyeHeight() + 0.5D, cameraPos.getZ() + 0.5D);
				float cameraXRot = be.getDefaultXRotation();
				float cameraYRot = be.getDefaultYRotation(be.getBlockState().getValue(SecurityCameraBlock.FACING));

				securityCamera.setPos(cameraEntityPos);
				window.setWidth(100);
				window.setHeight(100); //Different values have no effect to my knowledge, it's just important that the window is sized in an 1:1 ratio
				mc.options.setCameraType(CameraType.FIRST_PERSON);
				mc.setCameraEntity(securityCamera);
				securityCamera.setXRot(cameraXRot);
				securityCamera.setYRot(cameraYRot);
				camera.eyeHeight = camera.eyeHeightOld = player.getDimensions(Pose.STANDING).eyeHeight();
				mc.renderBuffers().bufferSource().endBatch(); //Makes sure that main world rendering is done
				CameraController.currentlyCapturedCamera = cameraView.getKey();

				//mc.options.setServerRenderDistance(CameraController.cameraViewDistance); //TODO Should we implement a serverconfig that limits frame chunk loading distance? Or/And a client one that limits frame render distance? (Current behaviour is just to take whatever the server/client currently has)

				if (feed.visibleSections != null) {
					mc.levelRenderer.visibleSections.clear();
					mc.levelRenderer.visibleSections.addAll(feed.visibleSections);
				}

				//TODO: Sodium support
				if (SecurityCraftClient.INSTALLED_IUM_MOD == IumCompat.EMBEDDIUM)
					EmbeddiumCompat.setEmptyRenderLists();

				frameTarget.bindWrite(true);
				mc.gameRenderer.renderLevel(DeltaTracker.ONE);
				frameTarget.unbindWrite();

				//TODO: Sodium support
				if (SecurityCraftClient.INSTALLED_IUM_MOD == IumCompat.EMBEDDIUM)
					EmbeddiumCompat.setPreviousRenderLists();

				if (feed.visibleSections == null) { //Set up the visible sections in the frame's frustum when they haven't been set up yet
					CameraController.discoverVisibleSections(camera, new Frustum(mc.levelRenderer.getFrustum()).offsetToFullyIncludeCameraCube(8), mc.levelRenderer.visibleSections);
					feed.setup(new ArrayList<>(mc.levelRenderer.visibleSections));
				}
			}
		}

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
		mc.options.setServerRenderDistance(oldServerRenderDistance);
		mc.gameRenderer.setRenderHand(true);
		mc.gameRenderer.setPanoramicMode(false);
		mc.levelRenderer.graphicsChanged();
		mc.getMainRenderTarget().bindWrite(true);
		CameraController.currentlyCapturedCamera = null;
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
		guiGraphics.blitSprite(BACKGROUND_SPRITE, 5, 0, 90, 20);
		guiGraphics.blitSprite(LIVE_SPRITE, window.getGuiScaledWidth() - 70, 5, 65, 16);

		if (!mc.player.hasEffect(MobEffects.NIGHT_VISION))
			guiGraphics.blitSprite(NIGHT_VISION_INACTIVE_SPRITE, 28, 4, 16, 9);
		else
			guiGraphics.blit(NIGHT_VISION, 27, -1, 0, 0, 18, 18, 18, 18);

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
