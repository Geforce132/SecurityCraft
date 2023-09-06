package net.geforcemods.securitycraft;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class SCClientEventHandler {
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	public static final ResourceLocation NIGHT_VISION = new ResourceLocation("textures/mob_effect/night_vision.png");
	public static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
	private static final Component REDSTONE_NOTE = Utils.localize("gui.securitycraft:camera.toggleRedstoneNote");

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
					for (ChangeEntry changeEntry : bcd.getFilteredEntries()) {
						BlockPos pos = changeEntry.pos();

						pose.pushPose();
						pose.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
						ClientUtils.renderBoxInLevel(BCDBuffer.INSTANCE, pose.last().pose(), 0, 1, 0, 1, 1, bcd.getColor());
						pose.popPose();
					}
				}
			}

			mc.renderBuffers().bufferSource().endBatch();
		}
	}

	@SubscribeEvent
	public static void onScreenshot(ScreenshotEvent event) {
		Player player = Minecraft.getInstance().player;

		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCamera camera = (SecurityCamera) Minecraft.getInstance().cameraEntity;

			if (camera.getScreenshotSoundCooldown() == 0) {
				camera.setScreenshotSoundCooldown(7);
				Minecraft.getInstance().level.playLocalSound(player.blockPosition(), SCSounds.CAMERASNAP.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);
			}
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

			if (mc.player.getItemInHand(hand).is(SCContent.CAMERA_MONITOR.get()))
				SCContent.CAMERA_MONITOR.get().use(mc.level, mc.player, hand);

			event.setCanceled(true);
			event.setSwingHand(false);
		}
	}

	public static void cameraOverlay(ForgeGui gui, PoseStack pose, float partialTicks, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		Level level = mc.level;
		BlockPos pos = mc.cameraEntity.blockPosition();
		Window window = mc.getWindow();

		if (mc.options.renderDebug)
			return;

		if (!(level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be))
			return;

		Font font = Minecraft.getInstance().font;
		Options settings = Minecraft.getInstance().options;
		boolean hasRedstoneModule = be.isModuleEnabled(ModuleType.REDSTONE);
		BlockState state = level.getBlockState(pos);
		Component lookAround = Utils.localize("gui.securitycraft:camera.lookAround", settings.keyUp.getTranslatedKeyMessage(), settings.keyLeft.getTranslatedKeyMessage(), settings.keyDown.getTranslatedKeyMessage(), settings.keyRight.getTranslatedKeyMessage());
		Component exit = Utils.localize("gui.securitycraft:camera.exit", settings.keyShift.getTranslatedKeyMessage());
		Component zoom = Utils.localize("gui.securitycraft:camera.zoom", KeyBindings.cameraZoomIn.getTranslatedKeyMessage(), KeyBindings.cameraZoomOut.getTranslatedKeyMessage());
		Component nightVision = Utils.localize("gui.securitycraft:camera.activateNightVision", KeyBindings.cameraActivateNightVision.getTranslatedKeyMessage());
		Component redstone = Utils.localize("gui.securitycraft:camera.toggleRedstone", KeyBindings.cameraEmitRedstone.getTranslatedKeyMessage());
		String time = ClientUtils.getFormattedMinecraftTime();
		int timeY = 25;

		if (be.hasCustomName()) {
			Component cameraName = be.getCustomName();

			font.drawShadow(pose, cameraName, window.getGuiScaledWidth() - font.width(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		font.drawShadow(pose, time, window.getGuiScaledWidth() - font.width(time) - 4, timeY, 16777215);
		font.drawShadow(pose, lookAround, window.getGuiScaledWidth() - font.width(lookAround) - 8, window.getGuiScaledHeight() - 80, 16777215);
		font.drawShadow(pose, exit, window.getGuiScaledWidth() - font.width(exit) - 8, window.getGuiScaledHeight() - 70, 16777215);
		font.drawShadow(pose, zoom, window.getGuiScaledWidth() - font.width(zoom) - 8, window.getGuiScaledHeight() - 60, 16777215);
		font.drawShadow(pose, nightVision, window.getGuiScaledWidth() - font.width(nightVision) - 8, window.getGuiScaledHeight() - 50, 16777215);
		font.drawShadow(pose, redstone, window.getGuiScaledWidth() - font.width(redstone) - 8, window.getGuiScaledHeight() - 40, hasRedstoneModule ? 16777215 : 16724855);
		font.drawShadow(pose, REDSTONE_NOTE, window.getGuiScaledWidth() - font.width(REDSTONE_NOTE) - 8, window.getGuiScaledHeight() - 30, hasRedstoneModule ? 16777215 : 16724855);

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
			if (!hasRedstoneModule)
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
}
