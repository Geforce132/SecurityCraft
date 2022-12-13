package net.geforcemods.securitycraft;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class SCClientEventHandler {
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	public static final ResourceLocation NIGHT_VISION = new ResourceLocation("textures/mob_effect/night_vision.png");
	private static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
	private static final Component REDSTONE_NOTE = Utils.localize("gui.securitycraft:camera.toggleRedstoneNote");

	@SubscribeEvent
	public static void onRenderLevelStage(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getInstance();
		Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
		PoseStack pose = event.getMatrixStack();
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

	@SubscribeEvent
	public static void onScreenshot(ScreenshotEvent event) {
		Player player = Minecraft.getInstance().player;

		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCamera camera = (SecurityCamera) Minecraft.getInstance().cameraEntity;

			if (camera.screenshotSoundCooldown == 0) {
				camera.screenshotSoundCooldown = 7;
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
	public static void onClickInput(InputEvent.ClickInputEvent event) {
		if (event.isAttack() && ClientHandler.isPlayerMountedOnCamera()) {
			event.setCanceled(true);
			event.setSwingHand(false);
		}
	}

	public static void cameraOverlay(ForgeIngameGui gui, PoseStack pose, float partialTicks, int width, int height) {
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
		gui.blit(pose, 5, 0, 0, 0, 90, 20);
		gui.blit(pose, window.getGuiScaledWidth() - 70, 5, 190, 0, 65, 30);

		if (!mc.player.hasEffect(MobEffects.NIGHT_VISION))
			gui.blit(pose, 28, 4, 90, 12, 16, 11);
		else {
			RenderSystem._setShaderTexture(0, NIGHT_VISION);
			GuiComponent.blit(pose, 27, -1, 0, 0, 18, 18, 18, 18);
			RenderSystem._setShaderTexture(0, CAMERA_DASHBOARD);
		}

		if (state.getSignal(level, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
			if (!hasRedstoneModule)
				gui.blit(pose, 12, 2, 104, 0, 12, 12);
			else
				gui.blit(pose, 12, 3, 90, 0, 12, 11);
		}
		else
			Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(REDSTONE, 10, 0);
	}

	public static void hotbarBindOverlay(ForgeIngameGui gui, PoseStack pose, float partialTicks, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		Level level = player.getCommandSenderWorld();
		double reachDistance = mc.gameMode.getPickRange();

		for (InteractionHand hand : InteractionHand.values()) {
			int uCoord = 0;
			ItemStack stack = player.getItemInHand(hand);

			if (stack.getItem() == SCContent.CAMERA_MONITOR.get()) {
				double eyeHeight = player.getEyeHeight();
				Vec3 lookVec = new Vec3(player.getX() + player.getLookAngle().x * reachDistance, eyeHeight + player.getY() + player.getLookAngle().y * reachDistance, player.getZ() + player.getLookAngle().z * reachDistance);
				HitResult hitResult = level.clip(new ClipContext(new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ()), lookVec, Block.OUTLINE, Fluid.NONE, player));

				if (hitResult instanceof BlockHitResult bhr && level.getBlockEntity(bhr.getBlockPos()) instanceof SecurityCameraBlockEntity) {
					CompoundTag cameras = stack.getOrCreateTag();
					uCoord = 110;

					for (int i = 1; i < 31; i++) {
						if (!cameras.contains("Camera" + i))
							continue;

						String[] coords = cameras.getString("Camera" + i).split(" ");

						if (Integer.parseInt(coords[0]) == bhr.getBlockPos().getX() && Integer.parseInt(coords[1]) == bhr.getBlockPos().getY() && Integer.parseInt(coords[2]) == bhr.getBlockPos().getZ()) {
							uCoord = 88;
							break;
						}
					}
				}
			}
			else if (stack.getItem() == SCContent.REMOTE_ACCESS_MINE.get()) {
				double eyeHeight = player.getEyeHeight();
				Vec3 lookVec = new Vec3(player.getX() + player.getLookAngle().x * reachDistance, eyeHeight + player.getY() + player.getLookAngle().y * reachDistance, player.getZ() + player.getLookAngle().z * reachDistance);
				HitResult hitResult = level.clip(new ClipContext(new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ()), lookVec, Block.OUTLINE, Fluid.NONE, player));

				if (hitResult instanceof BlockHitResult bhr && level.getBlockState(bhr.getBlockPos()).getBlock() instanceof IExplosive) {
					uCoord = 110;
					CompoundTag mines = stack.getOrCreateTag();

					for (int i = 1; i <= 6; i++) {
						if (stack.getTag().getIntArray("mine" + i).length > 0) {
							int[] coords = mines.getIntArray("mine" + i);

							if (coords[0] == bhr.getBlockPos().getX() && coords[1] == bhr.getBlockPos().getY() && coords[2] == bhr.getBlockPos().getZ()) {
								uCoord = 88;
								break;
							}
						}
					}
				}
			}
			else if (stack.getItem() == SCContent.REMOTE_ACCESS_SENTRY.get()) {
				if (Minecraft.getInstance().crosshairPickEntity instanceof Sentry sentry) {
					CompoundTag sentries = stack.getOrCreateTag();

					uCoord = 110;

					for (int i = 1; i <= 12; i++) {
						if (stack.getTag().getIntArray("sentry" + i).length > 0) {
							int[] coords = sentries.getIntArray("sentry" + i);

							if (coords[0] == sentry.blockPosition().getX() && coords[1] == sentry.blockPosition().getY() && coords[2] == sentry.blockPosition().getZ()) {
								uCoord = 88;
								break;
							}
						}
					}
				}
			}
			else if (stack.getItem() == SCContent.SONIC_SECURITY_SYSTEM_ITEM.get()) {
				double eyeHeight = player.getEyeHeight();
				Vec3 lookVec = new Vec3(player.getX() + player.getLookAngle().x * reachDistance, eyeHeight + player.getY() + player.getLookAngle().y * reachDistance, player.getZ() + player.getLookAngle().z * reachDistance);
				HitResult hitResult = level.clip(new ClipContext(new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ()), lookVec, Block.OUTLINE, Fluid.NONE, player));

				if (hitResult instanceof BlockHitResult bhr && level.getBlockEntity(bhr.getBlockPos()) instanceof ILockable lockable) {
					BlockPos pos = bhr.getBlockPos();

					//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
					if (!(lockable instanceof IOwnable ownable) || !ownable.isOwnedBy(player)) {
						if (DisguisableBlock.getDisguisedBlockState(level, pos).isPresent())
							return;
					}

					if (SonicSecuritySystemItem.isAdded(stack.getOrCreateTag(), pos))
						uCoord = 88;
					else
						uCoord = 110;
				}
			}

			if (uCoord != 0) {
				RenderSystem._setShaderTexture(0, BEACON_GUI);
				GuiComponent.blit(pose, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 90 + (hand == InteractionHand.MAIN_HAND ? player.getInventory().selected * 20 : (mc.options.mainHand == HumanoidArm.LEFT ? 189 : -29)), Minecraft.getInstance().getWindow().getGuiScaledHeight() - 22, uCoord, 219, 21, 22, 256, 256);
			}
		}
	}

	private static enum BCDBuffer implements MultiBufferSource {
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
