package net.geforcemods.securitycraft;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
				for (ChangeEntry changeEntry : bcd.getFilteredEntries()) {
					BlockPos pos = changeEntry.pos;

					pose.pushPose();
					pose.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
					ClientUtils.renderBoxInLevel(BCDBuffer.INSTANCE, pose.last().pose(), 0, 1, 0, 1, 1, bcd.getColor());
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
	public static void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if ((event.getType() == ElementType.EXPERIENCE || event.getType() == ElementType.JUMPBAR || event.getType() == ElementType.POTION_ICONS) && ClientHandler.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.ALL && ClientHandler.isPlayerMountedOnCamera())
			drawCameraOverlay(event.getMatrixStack(), Minecraft.getInstance(), Minecraft.getInstance().gui, Minecraft.getInstance().getWindow(), Minecraft.getInstance().player, Minecraft.getInstance().level, Minecraft.getInstance().cameraEntity.blockPosition());
	}

	private static void drawCameraOverlay(MatrixStack matrix, Minecraft mc, AbstractGui gui, MainWindow resolution, PlayerEntity player, World level, BlockPos pos) {
		if (mc.options.renderDebug)
			return;

		TileEntity te = level.getBlockEntity(pos);

		if (!(te instanceof SecurityCameraBlockEntity))
			return;

		FontRenderer font = Minecraft.getInstance().font;
		GameSettings settings = mc.options;
		SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;
		boolean hasRedstoneModule = be.isModuleEnabled(ModuleType.REDSTONE);
		boolean hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		BlockState state = level.getBlockState(pos);
		ITextComponent lookAround = Utils.localize("gui.securitycraft:camera.lookAround", settings.keyUp.getTranslatedKeyMessage(), settings.keyLeft.getTranslatedKeyMessage(), settings.keyDown.getTranslatedKeyMessage(), settings.keyRight.getTranslatedKeyMessage());
		ITextComponent exit = Utils.localize("gui.securitycraft:camera.exit", settings.keyShift.getTranslatedKeyMessage());
		ITextComponent zoom = Utils.localize("gui.securitycraft:camera.zoom", KeyBindings.cameraZoomIn.getTranslatedKeyMessage(), KeyBindings.cameraZoomOut.getTranslatedKeyMessage());
		ITextComponent nightVision = Utils.localize("gui.securitycraft:camera.activateNightVision", KeyBindings.cameraActivateNightVision.getTranslatedKeyMessage());
		ITextComponent redstone = Utils.localize("gui.securitycraft:camera.toggleRedstone", KeyBindings.cameraEmitRedstone.getTranslatedKeyMessage());
		ITextComponent smart = Utils.localize("gui.securitycraft:camera.setDefaultViewingDirection", KeyBindings.setDefaultViewingDirection.getTranslatedKeyMessage());
		long dayTime = Minecraft.getInstance().level.getDayTime();
		int hours24 = (int) ((float) dayTime / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (dayTime / 16.666666F % 60.0F);
		String time = String.format("%02d:%02d %s", Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM");
		int timeY = 25;

		if (be.hasCustomName()) {
			ITextComponent cameraName = be.getCustomName();

			font.drawShadow(matrix, cameraName, resolution.getGuiScaledWidth() - font.width(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		//TODO: simplify
		font.drawShadow(matrix, time, resolution.getGuiScaledWidth() - font.width(time) - 4, timeY, 16777215);
		font.drawShadow(matrix, lookAround, resolution.getGuiScaledWidth() - font.width(lookAround) - 8, resolution.getGuiScaledHeight() - 80, 16777215);
		font.drawShadow(matrix, exit, resolution.getGuiScaledWidth() - font.width(exit) - 8, resolution.getGuiScaledHeight() - 70, 16777215);
		font.drawShadow(matrix, zoom, resolution.getGuiScaledWidth() - font.width(zoom) - 8, resolution.getGuiScaledHeight() - 60, 16777215);
		font.drawShadow(matrix, nightVision, resolution.getGuiScaledWidth() - font.width(nightVision) - 8, resolution.getGuiScaledHeight() - 50, 16777215);
		font.drawShadow(matrix, redstone, resolution.getGuiScaledWidth() - font.width(redstone) - 8, resolution.getGuiScaledHeight() - 40, hasRedstoneModule ? 16777215 : 16724855);
		font.drawShadow(matrix, REDSTONE_NOTE, resolution.getGuiScaledWidth() - font.width(REDSTONE_NOTE) - 8, resolution.getGuiScaledHeight() - 30, hasRedstoneModule ? 16777215 : 16724855);
		font.drawShadow(matrix, smart, resolution.getGuiScaledWidth() - font.width(smart) - 8, resolution.getGuiScaledHeight() - 20, hasSmartModule ? 16777215 : 16724855);
		font.drawShadow(matrix, SMART_MODULE_NOTE, resolution.getGuiScaledWidth() - font.width(SMART_MODULE_NOTE) - 8, resolution.getGuiScaledHeight() - 10, hasSmartModule ? 16777215 : 16724855);

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
			if (!hasRedstoneModule)
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
}
