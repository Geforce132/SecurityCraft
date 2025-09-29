package net.geforcemods.securitycraft;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
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
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.zoom", KeyBindings.cameraZoomIn().getTranslatedKeyMessage(), KeyBindings.cameraZoomOut().getTranslatedKeyMessage()), $ -> true),
			new CameraKeyInfoEntry(ConfigHandler.SERVER.allowCameraNightVision::get, $ -> Utils.localize("gui.securitycraft:camera.activateNightVision", KeyBindings.cameraActivateNightVision().getTranslatedKeyMessage()), $ -> true),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.toggleRedstone", KeyBindings.cameraEmitRedstone().getTranslatedKeyMessage()), be -> be.isModuleEnabled(ModuleType.REDSTONE)),
			new CameraKeyInfoEntry(() -> true, $ -> REDSTONE_NOTE, be -> be.isModuleEnabled(ModuleType.REDSTONE)),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.setDefaultViewingDirection", KeyBindings.setDefaultViewingDirection().getTranslatedKeyMessage()), be -> be.isModuleEnabled(ModuleType.SMART)),
			new CameraKeyInfoEntry(() -> true, $ -> SMART_MODULE_NOTE, be -> be.isModuleEnabled(ModuleType.SMART))
	};
	//@formatter:on
	private static final List<DeferredHolder<DataComponentType<?>, ? extends DataComponentType<? extends TooltipProvider>>> COMPONENTS_WITH_GLOBAL_TOOLTIP = List.of(SCContent.OWNER_DATA, SCContent.NOTES);
	private static float cameraInfoMessageTime;

	private SCClientEventHandler() {}

	@SubscribeEvent
	public static void onClientTickPost(ClientTickEvent.Post event) {
		if (cameraInfoMessageTime >= 0)
			cameraInfoMessageTime--;
	}

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent.AfterTripwireBlocks event) {
		Vec3 camPos = event.getCamera().pos;
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
					ShapeRenderer.renderLineBox(pose.last(), consumer, 0, 0, 0, 1, 1, 1, r, g, b, 1.0F);
					pose.popPose();
				}
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
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		TooltipContext ctx = event.getContext();
		TooltipDisplay display = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		List<Component> tooltip = event.getToolTip();
		TooltipFlag flag = event.getFlags();
		int nextIndex = 0;

		for (int i = 0; i < COMPONENTS_WITH_GLOBAL_TOOLTIP.size(); i++) {
			DataComponentType<? extends TooltipProvider> type = COMPONENTS_WITH_GLOBAL_TOOLTIP.get(i).get();
			TooltipProvider tooltipProvider = stack.get(type);

			if (tooltipProvider != null && display.shows(type)) {
				final int index = nextIndex++ + 1; //add in order of global tooltip components list, after item name

				tooltipProvider.addToTooltip(ctx, line -> {
					if (index >= tooltip.size())
						tooltip.add(line);
					else
						tooltip.add(index, line);
				}, flag, stack.getComponents());
			}
		}
	}

	public static void cameraOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		Level level = mc.level;
		BlockPos pos = mc.getCameraEntity().blockPosition();
		Window window = mc.getWindow();
		int scaledWidth = window.getGuiScaledWidth();
		int scaledHeight = window.getGuiScaledHeight();

		if (mc.options.hideGui || mc.getDebugOverlay().showDebugScreen())
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

			guiGraphics.drawString(font, cameraName, scaledWidth - font.width(cameraName) - 8, 25, CommonColors.WHITE, true);
			timeY += 10;
		}

		guiGraphics.drawString(font, time, scaledWidth - font.width(time) - 4, timeY, CommonColors.WHITE, true);

		if (cameraInfoMessageTime >= 0) {
			float fadeOutPartialTick = Math.max(cameraInfoMessageTime + 1.0F - deltaTracker.getGameTimeDeltaPartialTick(false), 1.0F);
			int alpha = (int) Math.ceil(255.0F * Math.min(20.0F, fadeOutPartialTick) / 20.0F);
			int heightOffset = 10;

			for (int i = CAMERA_KEY_INFO_LIST.length - 1; i >= 0; i--) {
				CameraKeyInfoEntry entry = CAMERA_KEY_INFO_LIST[i];

				if (entry.enabled().get()) {
					entry.drawString(options, guiGraphics, font, scaledWidth, scaledHeight, heightOffset, be, alpha);
					heightOffset += 10;
				}
			}
		}

		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 5, 0, 90, 20);
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIVE_SPRITE, window.getGuiScaledWidth() - 70, 5, 65, 16);

		if (!mc.player.hasEffect(MobEffects.NIGHT_VISION))
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, NIGHT_VISION_INACTIVE_SPRITE, 28, 4, 16, 9);
		else
			guiGraphics.blit(RenderPipelines.GUI_TEXTURED, NIGHT_VISION, 27, -1, 0, 0, 18, 18, 18, 18);

		if (state.getSignal(level, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
			if (!be.isModuleEnabled(ModuleType.REDSTONE))
				CameraRedstoneModuleState.NOT_INSTALLED.render(guiGraphics, 12, 2);
			else
				CameraRedstoneModuleState.DEACTIVATED.render(guiGraphics, 12, 2);
		}
		else
			CameraRedstoneModuleState.ACTIVATED.render(guiGraphics, 12, 2);
	}

	public static void resetCameraInfoMessageTime() {
		cameraInfoMessageTime = 200;
	}

	public record CameraKeyInfoEntry(Supplier<Boolean> enabled, Function<Options, Component> text, Predicate<SecurityCameraBlockEntity> whiteText) {
		public void drawString(Options options, GuiGraphics guiGraphics, Font font, int scaledWidth, int scaledHeight, int heightOffset, SecurityCameraBlockEntity be, int alpha) {
			Component text = text().apply(options);
			int textColor = whiteText().test(be) ? 0xFFFFFF : 0xFF3377;

			guiGraphics.drawString(font, text, scaledWidth - font.width(text) - 8, scaledHeight - heightOffset, textColor + (alpha << 24), true);
		}
	}
}
