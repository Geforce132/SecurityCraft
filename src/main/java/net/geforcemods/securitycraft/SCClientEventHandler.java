package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = SecurityCraft.MODID)
public class SCClientEventHandler {
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");

	private SCClientEventHandler() {}

	@SubscribeEvent
	public static void onRenderLevelStage(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		World level = mc.world;
		float partialTicks = event.getPartialTicks();
		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		for (BlockPos bcdPos : BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getTrackedTileEntities(level)) {
			TileEntity be = level.getTileEntity(bcdPos);

			if (!(be instanceof BlockChangeDetectorBlockEntity))
				continue;

			BlockChangeDetectorBlockEntity bcd = (BlockChangeDetectorBlockEntity) be;

			if (bcd.isShowingHighlights() && bcd.isOwnedBy(mc.player)) {
				for (ChangeEntry changeEntry : bcd.getFilteredEntries()) {
					BlockPos pos = changeEntry.pos;

					GlStateManager.pushMatrix();
					GlStateManager.disableDepth();
					GlStateManager.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
					ClientUtils.renderBoxInLevel(0, 1, 0, 1, 1, bcd.getColor());
					GlStateManager.enableDepth();
					GlStateManager.popMatrix();
				}
			}
		}
	}

	@SubscribeEvent
	public static void renderHandEvent(RenderSpecificHandEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;

		if (PlayerUtils.isPlayerMountedOnCamera(player))
			event.setCanceled(true);
		else {
			boolean mainHandTaser = player.getHeldItemMainhand().getItem() instanceof TaserItem;
			boolean offhandTaser = player.getHeldItemOffhand().getItem() instanceof TaserItem;

			if (mainHandTaser || offhandTaser) {
				boolean isRightHanded = mc.gameSettings.mainHand == EnumHandSide.RIGHT;
				boolean isMainHand = event.getHand() == EnumHand.MAIN_HAND;

				if (mainHandTaser && offhandTaser)
					event.setCanceled(!isMainHand);
				else if ((isMainHand && offhandTaser || !isMainHand && mainHandTaser)) {
					event.setCanceled(true);
					return;
				}

				if (isRightHanded == isMainHand)
					GlStateManager.translate(-0.54F, 0.0F, 0.0F);
				else
					GlStateManager.translate(0.58F, 0.0F, 0.0F);
			}
		}
	}

	@SubscribeEvent
	public static void onClickInput(MouseEvent event) {
		if (event.getButton() == 0 && ClientProxy.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (event.getWorld().isRemote) {
			ChunkPos pos = event.getChunk().getPos();

			CameraViewAreaExtension.onChunkUnload(pos.x, pos.z);
		}
	}

	@SubscribeEvent
	public static void onRenderFramePost(RenderTickEvent event) {
		if (event.phase == Phase.END)
			return;

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;

		if (player == null || player.world == null || CameraController.FRAME_CAMERA_FEEDS.isEmpty() || !ConfigHandler.frameFeedViewingEnabled || mc.currentScreen instanceof GuiVideoSettings)
			return;

		Profiler profiler = mc.profiler;
		Map<GlobalPos, CameraFeed> activeFrameCameraFeeds;
		List<GlobalPos> erroringFrameCameraFeeds = new ArrayList<>();
		//+1 helps to reduce stuttering when many frames are active at once
		double feedsToRender = CameraController.FRAME_CAMERA_FEEDS.size() + 1;
		double fpsCap = ConfigHandler.frameFeedFpsLimit;
		double currentTime = System.nanoTime() / 1E9D;
		double frameInterval = 1.0D / fpsCap; //A fps cap of 30 means that one thirtieth of a second needs to pass before the same frame may be rendered again
		double timeBetweenFrames = frameInterval / feedsToRender; //A fps cap of 30 with three feeds to render means that 1/90 of a second needs to pass between two different frame feed captures
		double activeFramesPerMcFrame = MathHelper.ceil((fpsCap * feedsToRender) / Minecraft.getDebugFPS());

		if (fpsCap < 260.0D) {
			activeFrameCameraFeeds = new HashMap<>();

			for (Entry<GlobalPos, CameraFeed> cameraView : CameraController.FRAME_CAMERA_FEEDS.entrySet()) {
				double lastActiveTime = cameraView.getValue().lastActiveTime().get();

				if (currentTime < lastActiveTime + frameInterval || currentTime < CameraController.lastFrameRendered + timeBetweenFrames || activeFramesPerMcFrame-- <= 0)
					continue;

				cameraView.getValue().lastActiveTime().set(currentTime);
				activeFrameCameraFeeds.put(cameraView.getKey(), cameraView.getValue());
			}
		}
		else
			activeFrameCameraFeeds = new HashMap<>(CameraController.FRAME_CAMERA_FEEDS);

		if (activeFrameCameraFeeds.isEmpty())
			return;

		CameraController.lastFrameRendered = currentTime;
		profiler.endSection(); //out of "render"
		profiler.startSection("gameRenderer");
		profiler.startSection("securitycraft:frame_level");

		World level = player.world;
		float partialTick = mc.isGamePaused() ? mc.renderPartialTicksPaused : event.renderTickTime;
		Entity oldCamEntity = mc.getRenderViewEntity();

		int oldWidth = mc.displayWidth;
		int oldHeight = mc.displayHeight;
		int frameFeedResolution = ConfigHandler.frameFeedResolution;
		List<ContainerLocalRenderInformation> oldVisibleSections = new ObjectArrayList<>(mc.renderGlobal.renderInfos);
		int newFrameFeedViewDistance = CameraController.getFrameFeedViewDistance(null);
		float oldXRot = player.cameraPitch;
		float oldXRotO = player.prevCameraPitch;
		float oldYRot = player.cameraYaw;
		float oldYRotO = player.prevCameraYaw;
		float oldEyeHeight = player.eyeHeight;
		boolean oldRenderHand = mc.entityRenderer.renderHand;
		boolean oldRenderBlockOutline = mc.entityRenderer.drawBlockOutline;
		int oldCameraType = mc.gameSettings.thirdPersonView;
		EntityArmorStand securityCamera = new EntityArmorStand(level); //A separate entity is used instead of moving the player to allow the player to see themselves
		Frustum playerFrustum = getCurrentFrustum(oldCamEntity); //Saved once before the loop, because the frustum changes depending on which camera is viewed

		mc.entityRenderer.drawBlockOutline = false;
		mc.entityRenderer.renderHand = false;
		mc.displayWidth = frameFeedResolution;
		mc.displayHeight = frameFeedResolution;
		mc.gameSettings.thirdPersonView = 0;

		for (Entry<GlobalPos, CameraFeed> cameraView : activeFrameCameraFeeds.entrySet()) {
			GlobalPos cameraPos = cameraView.getKey();
			CameraFeed feed = cameraView.getValue();

			if (feed.usesVbo() != OpenGlHelper.useVbo()) //If the player switches their VBO setting, update + recollect all render chunks in range, or else the game crashes
				CameraController.FRAME_CAMERA_FEEDS.put(cameraPos, CameraController.setUpCameraSections(cameraPos));
			else if (cameraPos.dimension() == level.provider.getDimension()) {
				BlockPos pos = cameraPos.pos();
				TileEntity te = level.getTileEntity(pos);

				if (te instanceof SecurityCameraBlockEntity) {
					if (playerFrustum != null && !feed.hasFrameInFrustum(playerFrustum))
						continue;

					SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;
					Framebuffer frameTarget = feed.renderTarget();
					Vec3d cameraEntityPos = new Vec3d(pos.getX() + 0.5D, pos.getY() - player.getEyeHeight() + 0.5D, pos.getZ() + 0.5D);
					float cameraXRot = be.getDefaultXRotation();
					float cameraYRot = be.getDefaultYRotation(be.getWorld().getBlockState(be.getPos()).getValue(SecurityCameraBlock.FACING)) + (float) Utils.lerp(partialTick, be.getOriginalCameraRotation(), be.getCameraRotation()) * (180F / (float) Math.PI);

					securityCamera.setPosition(cameraEntityPos.x, cameraEntityPos.y, cameraEntityPos.z);
					mc.setRenderViewEntity(securityCamera);
					securityCamera.rotationPitch = cameraXRot;
					securityCamera.rotationYaw = cameraYRot;
					securityCamera.prevRotationPitch = cameraXRot;
					securityCamera.prevRotationYaw = cameraYRot;
					CameraController.currentlyCapturedCamera = cameraPos;
					feed.applyVisibleSections(mc.renderGlobal.renderInfos, mc.renderGlobal.chunksToUpdate);
					profiler.startSection("securitycraft:discover_frame_sections");
					feed.discoverVisibleSections(cameraPos, newFrameFeedViewDistance);
					mc.renderGlobal.chunksToUpdate.addAll(feed.getDirtyRenderChunks());
					profiler.endStartSection("securitycraft:bind_frame_target");
					frameTarget.framebufferClear();
					frameTarget.bindFramebuffer(true);
					profiler.endSection();

					try {
						mc.entityRenderer.renderWorld(1.0F, System.nanoTime() + 4166666);
					}
					catch (Exception e) {
						SecurityCraft.LOGGER.error("Frame feed at {} threw an exception while rendering the level. Deactivating clientside rendering for this feed", be.getPos());
						e.printStackTrace();
						erroringFrameCameraFeeds.add(cameraPos);
					}

					frameTarget.unbindFramebuffer();
					profiler.startSection("securitycraft:apply_frame_frustum");

					Frustum frustum = getCurrentFrustum(securityCamera);

					if (be.shouldRotate() || !feed.hasVisibleSections() || feed.requiresFrustumUpdate())
						feed.updateVisibleSections(frustum);

					profiler.endSection();
				}
			}
		}

		securityCamera.setDead();
		mc.setRenderViewEntity(oldCamEntity);
		player.cameraPitch = oldXRot;
		player.prevCameraPitch = oldXRotO;
		player.cameraYaw = oldYRot;
		player.prevCameraYaw = oldYRotO;
		player.eyeHeight = oldEyeHeight;
		mc.gameSettings.thirdPersonView = oldCameraType;
		mc.entityRenderer.drawBlockOutline = oldRenderBlockOutline;
		mc.renderGlobal.renderInfos.clear();
		mc.renderGlobal.renderInfos.addAll(oldVisibleSections);
		mc.entityRenderer.renderHand = oldRenderHand;
		mc.displayWidth = oldWidth;
		mc.displayHeight = oldHeight;
		mc.getFramebuffer().bindFramebuffer(true);
		CameraController.currentlyCapturedCamera = null;

		profiler.endSection();
		profiler.endSection();
		profiler.startSection("render");

		for (GlobalPos erroringFeed : erroringFrameCameraFeeds) {
			CameraController.removeAllFrameLinks(erroringFeed);
		}
	}

	private static Frustum getCurrentFrustum(Entity entity) {
		Frustum frustum = null;

		if (CameraController.lastUsedClippingHelper != null) {
			frustum = new Frustum(CameraController.lastUsedClippingHelper);
			frustum.setPosition(entity.posX, entity.posY, entity.posZ);
		}

		return frustum;
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if ((event.getType() == ElementType.EXPERIENCE || event.getType() == ElementType.JUMPBAR || event.getType() == ElementType.POTION_ICONS) && ClientProxy.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		//calling down() on the render view entity's position because the camera entity sits at y+0.5 by default and getPosition increases y by 0.5 again
		if (event.getType() == ElementType.ALL && ClientProxy.isPlayerMountedOnCamera())
			GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.getResolution(), Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, Minecraft.getMinecraft().getRenderViewEntity().getPosition().down());
	}
}
