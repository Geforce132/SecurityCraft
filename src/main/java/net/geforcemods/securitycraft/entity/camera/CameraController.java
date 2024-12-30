package net.geforcemods.securitycraft.entity.camera;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.LayerToggleHandler;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.Util;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.CompiledSection;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenshotEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class CameraController {
	public static CameraType previousCameraType;
	public static boolean resetOverlaysAfterDismount = false;
	private static final ViewMovementKeyHandler[] MOVE_KEY_HANDLERS = Util.make(() -> {
		Minecraft mc = Minecraft.getInstance();

		if (mc != null) {
			return new ViewMovementKeyHandler[] {
				//@formatter:off
				new ViewMovementKeyHandler(mc.options.keyUp, CameraController::moveViewUp),
				new ViewMovementKeyHandler(mc.options.keyDown, CameraController::moveViewDown),
				new ViewMovementKeyHandler(mc.options.keyLeft, cam -> moveViewHorizontally(cam, cam.getYRot() - getMovementSpeed(cam) * cam.getZoomAmount())),
				new ViewMovementKeyHandler(mc.options.keyRight, cam -> moveViewHorizontally(cam, cam.getYRot() + getMovementSpeed(cam) * cam.getZoomAmount()))
				//@formatter:on
			};
		}
		else
			return new ViewMovementKeyHandler[0];
	});
	private static int screenshotSoundCooldown = 0;
	public static final Map<GlobalPos, Set<BlockPos>> FRAME_LINKS = new HashMap<>();
	public static final Map<GlobalPos, CameraFeed> FRAME_CAMERA_FEEDS = new ConcurrentHashMap<>();
	public static final Set<GlobalPos> FEED_FRUSTUM_UPDATE_REQUIRED = new HashSet<>();
	public static GlobalPos currentlyCapturedCamera;
	public static ShaderInstance cameraMonitorShader;
	public static double lastFrameRendered = 0.0D;

	private CameraController() {}

	@SubscribeEvent
	public static void onClientTickPre(ClientTickEvent.Pre event) {
		//up/down/left/right handling is split to prevent players who are viewing a camera from moving around in a boat or on a horse
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;

		if (cameraEntity instanceof SecurityCamera) {
			Options options = Minecraft.getInstance().options;

			for (ViewMovementKeyHandler handler : MOVE_KEY_HANDLERS) {
				handler.tickStart();
			}

			if (options.keyShift.isDown()) {
				dismount();
				options.keyShift.setDown(false);
			}
		}
	}

	@SubscribeEvent
	public static void onClientTickPost(ClientTickEvent.Post event) {
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;

		if (cameraEntity instanceof SecurityCamera cam) {
			for (ViewMovementKeyHandler handler : MOVE_KEY_HANDLERS) {
				handler.tickEnd(cam);
			}

			if (KeyBindings.cameraZoomIn.isDown())
				zoomIn(cam);
			else if (KeyBindings.cameraZoomOut.isDown())
				zoomOut(cam);
			else
				cam.zooming = false;

			KeyBindings.cameraEmitRedstone.tick(cam);
			KeyBindings.cameraActivateNightVision.tick(cam);
			KeyBindings.setDefaultViewingDirection.tick(cam);
			screenshotSoundCooldown--;

			//update other players with the head rotation
			LocalPlayer player = Minecraft.getInstance().player;
			double yRotChange = player.getYRot() - player.yRotLast;
			double xRotChange = player.getXRot() - player.xRotLast;

			if (yRotChange != 0.0D || xRotChange != 0.0D)
				player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.onGround()));
		}

		if (resetOverlaysAfterDismount) {
			resetOverlaysAfterDismount = false;
			LayerToggleHandler.disable(ClientHandler.CAMERA_LAYER);
			LayerToggleHandler.enable(VanillaGuiLayers.JUMP_METER);
			LayerToggleHandler.enable(VanillaGuiLayers.EXPERIENCE_BAR);
			LayerToggleHandler.enable(VanillaGuiLayers.EFFECTS);
		}
	}

	@SubscribeEvent
	public static void onScreenshot(ScreenshotEvent event) {
		Player player = Minecraft.getInstance().player;

		if (PlayerUtils.isPlayerMountedOnCamera(player) && screenshotSoundCooldown <= 0) {
			screenshotSoundCooldown = 7;
			Minecraft.getInstance().level.playLocalSound(player.blockPosition(), SCSounds.CAMERASNAP.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);
		}
	}

	private static void dismount() {
		PacketDistributor.sendToServer(new DismountCamera());
	}

	public static void moveViewUp(SecurityCamera cam) {
		float next = cam.getXRot() - getMovementSpeed(cam) * cam.getZoomAmount();

		if (cam.isCameraDown()) {
			if (next > 40F)
				cam.setRotation(cam.getYRot(), next);
		}
		else if (next > -25F)
			cam.setRotation(cam.getYRot(), next);
	}

	public static void moveViewDown(SecurityCamera cam) {
		float next = cam.getXRot() + getMovementSpeed(cam) * cam.getZoomAmount();

		if (cam.isCameraDown()) {
			if (next < 90F)
				cam.setRotation(cam.getYRot(), next);
		}
		else if (next < 60F)
			cam.setRotation(cam.getYRot(), next);
	}

	public static void moveViewHorizontally(SecurityCamera cam, float next) {
		BlockState state = cam.level().getBlockState(cam.blockPosition());

		if (state.hasProperty(SecurityCameraBlock.FACING)) {
			float checkNext = next;

			if (checkNext < 0)
				checkNext += 360;

			boolean shouldSetRotation = switch (state.getValue(SecurityCameraBlock.FACING)) {
				case NORTH -> checkNext > 90F && checkNext < 270F;
				case SOUTH -> checkNext > 270F || checkNext < 90F;
				case EAST -> checkNext > 180F && checkNext < 360F;
				case WEST -> checkNext > 0F && checkNext < 180F;
				case DOWN -> true;
				default -> false;
			};

			if (shouldSetRotation)
				cam.setYRot(next);
		}
	}

	public static void zoomIn(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.setZoomAmount(Math.max(cam.getZoomAmount() - 0.1F, 0.1F));
	}

	public static void zoomOut(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.setZoomAmount(Math.min(cam.getZoomAmount() + 0.1F, 1.4F));
	}

	public static void toggleRedstone(SecurityCamera cam) {
		BlockPos pos = cam.blockPosition();
		Level level = cam.level();

		if (level.getBlockEntity(pos) instanceof IModuleInventory be && be.isModuleEnabled(ModuleType.REDSTONE))
			PacketDistributor.sendToServer(new SetCameraPowered(pos, !level.getBlockState(pos).getValue(SecurityCameraBlock.POWERED)));
	}

	public static void toggleNightVision(SecurityCamera cam) {
		if (ConfigHandler.SERVER.allowCameraNightVision.get())
			PacketDistributor.sendToServer(new ToggleNightVision());
	}

	public static void setDefaultViewingDirection(SecurityCamera cam) {
		PacketDistributor.sendToServer(new SetDefaultCameraViewingDirection(cam.getId(), cam.getXRot(), cam.getYRot(), cam.getZoomAmount()));
	}

	public static boolean isLinked(FrameBlockEntity be, GlobalPos cameraPos) {
		return FRAME_LINKS.containsKey(cameraPos) && FRAME_LINKS.get(cameraPos).contains(be.getBlockPos());
	}

	public static void addFrameLink(FrameBlockEntity be, GlobalPos cameraPos) {
		Set<BlockPos> bes = FRAME_LINKS.computeIfAbsent(cameraPos, p -> new HashSet<>());

		bes.add(be.getBlockPos());
		FRAME_CAMERA_FEEDS.computeIfAbsent(cameraPos, CameraController::setUpCameraSections);
	}

	private static CameraFeed setUpCameraSections(GlobalPos cameraPos) {
		int resolution = ConfigHandler.CLIENT.frameFeedResolution.get();
		BlockPos pos = cameraPos.pos();
		SectionPos cameraSectionPos = SectionPos.of(pos);
		RenderSection startingSection = CameraViewAreaExtension.rawFetch(cameraSectionPos.x(), Mth.clamp(cameraSectionPos.y(), CameraViewAreaExtension.minSectionY(), CameraViewAreaExtension.maxSectionY() - 1), cameraSectionPos.z(), true);
		CameraFeed cameraFeed = new CameraFeed(new TextureTarget(resolution, resolution, true, Minecraft.ON_OSX), new AtomicDouble(), new ArrayList<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>());

		cameraFeed.compilingSectionsQueue.add(startingSection);
		cameraFeed.sectionsInRange.add(startingSection);
		cameraFeed.sectionsInRangePositions.add(startingSection.getOrigin().asLong());
		CameraController.discoverVisibleSections(cameraPos, getFrameFeedViewDistance(null), cameraFeed);
		return cameraFeed;
	}

	public static void removeFrameLink(FrameBlockEntity be, GlobalPos cameraPos) {
		if (FRAME_LINKS.containsKey(cameraPos)) {
			Set<BlockPos> linkedFrames = FRAME_LINKS.get(cameraPos);

			linkedFrames.remove(be.getBlockPos());

			if (linkedFrames.isEmpty())
				removeAllFrameLinks(cameraPos);
		}
	}

	public static void removeAllFrameLinks(GlobalPos cameraPos) {
		if (FRAME_LINKS.containsKey(cameraPos)) {
			FRAME_LINKS.remove(cameraPos);
			FRAME_CAMERA_FEEDS.remove(cameraPos);
		}
	}

	public static RenderTarget getViewForFrame(GlobalPos cameraPos) {
		return FRAME_CAMERA_FEEDS.containsKey(cameraPos) ? FRAME_CAMERA_FEEDS.get(cameraPos).renderTarget : null;
	}

	public static void discoverVisibleSections(GlobalPos cameraPos, int viewDistance, CameraFeed feed) {
		SectionPos cameraSectionPos = SectionPos.of(cameraPos.pos());
		List<RenderSection> visibleSections = feed.sectionsInRange;
		List<RenderSection> sectionQueue = feed.compilingSectionsQueue;
		Set<Long> visibleSectionPositions = feed.sectionsInRangePositions;
		Deque<RenderSection> queueToCheck = new ArrayDeque<>(sectionQueue);

		sectionQueue.clear();

		while (!queueToCheck.isEmpty()) {
			RenderSection currentSection = queueToCheck.poll();
			BlockPos origin = currentSection.getOrigin();
			CompiledSection currentCompiledSection = currentSection.getCompiled();

			if (currentCompiledSection == CompiledSection.UNCOMPILED) {
				sectionQueue.add(currentSection);
				continue;
			}

			//Once a section in the queue is compiled, it knows which neighbours it can and cannot see. This information is used to more cleverly determine which chunks the player can actually see
			for (Direction dir : Direction.values()) {
				int cx = SectionPos.blockToSectionCoord(origin.getX()) + dir.getStepX();
				int cy = SectionPos.blockToSectionCoord(origin.getY()) + dir.getStepY();
				int cz = SectionPos.blockToSectionCoord(origin.getZ()) + dir.getStepZ();

				if (ChunkTrackingView.isInViewDistance(cameraSectionPos.x(), cameraSectionPos.z(), viewDistance, cx, cz)) {
					RenderSection neighbourSection = CameraViewAreaExtension.rawFetch(cx, cy, cz, true);

					if (neighbourSection != null) {
						long neighbourPosAsLong = neighbourSection.getOrigin().asLong();

						if (!visibleSectionPositions.contains(neighbourPosAsLong) && canSeeNeighborFace(currentCompiledSection, dir)) {
							visibleSections.add(neighbourSection); //Yet uncompiled render sections are added to the sections-in-range list, so Minecraft will schedule to compile them
							visibleSectionPositions.add(neighbourSection.getOrigin().asLong());
							sectionQueue.add(neighbourSection);
							FEED_FRUSTUM_UPDATE_REQUIRED.add(cameraPos);
						}
					}
				}
			}
		}
	}

	private static boolean canSeeNeighborFace(CompiledSection currentCompiledSection, Direction dir) {
		for (int j = 0; j < Direction.values().length; j++) {
			if (currentCompiledSection.facesCanSeeEachother(Direction.values()[j].getOpposite(), dir))
				return true;
		}

		return false;
	}

	public static int getFrameFeedViewDistance(FrameBlockEntity be) {
		int frameSpecificRenderDistance = be == null ? 32 : be.getChunkLoadingDistanceOption();

		return Math.min(frameSpecificRenderDistance, Math.min(ConfigHandler.CLIENT.frameFeedRenderDistance.get(), Math.min(ConfigHandler.SERVER.frameFeedViewDistance.get(), Minecraft.getInstance().options.getEffectiveRenderDistance())));
	}

	public static float getMovementSpeed(SecurityCamera cam) {
		SecurityCameraBlockEntity be = cam.getBlockEntity();

		if (be != null)
			return (float) be.getMovementSpeed();

		return 0.0F;
	}

	public static class ViewMovementKeyHandler {
		private final KeyMapping key;
		private final Consumer<SecurityCamera> action;
		private boolean wasPressed;

		public ViewMovementKeyHandler(KeyMapping key, Consumer<SecurityCamera> action) {
			this.key = key;
			this.action = action;
		}

		public void tickStart() {
			wasPressed = key.isDown();

			if (wasPressed)
				key.setDown(false);
		}

		public void tickEnd(SecurityCamera cam) {
			if (wasPressed) {
				action.accept(cam);
				key.setDown(true);
			}
		}
	}

	public record CameraFeed(RenderTarget renderTarget, AtomicDouble lastActiveTime, List<RenderSection> sectionsInRange, Set<Long> sectionsInRangePositions, List<RenderSection> visibleSections, List<RenderSection> compilingSectionsQueue) {}
}
