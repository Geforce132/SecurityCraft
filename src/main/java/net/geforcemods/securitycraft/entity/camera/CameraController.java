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
import java.util.stream.Collectors;

import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.util.concurrent.AtomicDouble;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Side.CLIENT)
public class CameraController {
	public static int previousCameraType;
	//@formatter:off
	private static final ViewMovementKeyHandler[] MOVE_KEY_HANDLERS = {
			new ViewMovementKeyHandler(Minecraft.getMinecraft().gameSettings.keyBindForward, CameraController::moveViewUp),
			new ViewMovementKeyHandler(Minecraft.getMinecraft().gameSettings.keyBindBack, CameraController::moveViewDown),
			new ViewMovementKeyHandler(Minecraft.getMinecraft().gameSettings.keyBindLeft, cam -> moveViewHorizontally(cam, cam.rotationYaw - getMovementSpeed(cam) * cam.getZoomAmount())),
			new ViewMovementKeyHandler(Minecraft.getMinecraft().gameSettings.keyBindRight, cam -> moveViewHorizontally(cam, cam.rotationYaw + getMovementSpeed(cam) * cam.getZoomAmount()))
	};
	//@formatter:on
	private static int screenshotSoundCooldown = 0;
	public static final Map<GlobalPos, Set<BlockPos>> FRAME_LINKS = new HashMap<>();
	public static final Map<GlobalPos, CameraFeed> FRAME_CAMERA_FEEDS = new ConcurrentHashMap<>();
	public static final Set<GlobalPos> FEED_FRUSTUM_UPDATE_REQUIRED = new HashSet<>();
	public static Pair<GlobalPos, CameraFeed> currentlyCapturedCamera;
	public static ClippingHelper lastUsedClippingHelper;
	public static double lastFrameRendered = 0.0D;

	private CameraController() {}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();

		if (renderViewEntity instanceof SecurityCamera) {
			SecurityCamera cam = (SecurityCamera) renderViewEntity;

			if (event.phase == Phase.END) {
				for (ViewMovementKeyHandler handler : MOVE_KEY_HANDLERS) {
					handler.tickEnd(cam);
				}

				if (KeyBindings.cameraZoomIn.isKeyDown())
					zoomIn(cam);
				else if (KeyBindings.cameraZoomOut.isKeyDown())
					zoomOut(cam);
				else
					cam.zooming = false;

				KeyBindings.cameraEmitRedstone.tick(cam);
				KeyBindings.cameraActivateNightVision.tick(cam);
				KeyBindings.setDefaultViewingDirection.tick(cam);
				screenshotSoundCooldown--;

				//update other players with the head rotation
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				double yRotChange = player.rotationYaw - player.lastReportedYaw;
				double xRotChange = player.rotationPitch - player.lastReportedPitch;

				if (yRotChange != 0.0D || xRotChange != 0.0D)
					player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));
			}
		}
	}

	public static void handleKeybinds() {
		GameSettings options = Minecraft.getMinecraft().gameSettings;

		for (ViewMovementKeyHandler handler : MOVE_KEY_HANDLERS) {
			handler.tickStart();
		}

		if (options.keyBindSneak.isKeyDown()) {
			dismount();
			KeyBinding.setKeyBindState(options.keyBindSneak.getKeyCode(), false);
		}
	}

	@SubscribeEvent
	public static void onScreenshot(ScreenshotEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (PlayerUtils.isPlayerMountedOnCamera(player) && screenshotSoundCooldown <= 0) {
			screenshotSoundCooldown = 7;
			Minecraft.getMinecraft().world.playSound(player.getPosition(), SCSounds.CAMERASNAP.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
		}
	}

	private static void dismount() {
		SecurityCraft.network.sendToServer(new DismountCamera());
	}

	public static void moveViewUp(SecurityCamera cam) {
		float next = cam.rotationPitch - getMovementSpeed(cam) * cam.getZoomAmount();

		if (cam.isCameraDown()) {
			if (next > 40F)
				cam.setRotation(cam.rotationYaw, next);
		}
		else if (next > -25F)
			cam.setRotation(cam.rotationYaw, next);
	}

	public static void moveViewDown(SecurityCamera cam) {
		float next = cam.rotationPitch + getMovementSpeed(cam) * cam.getZoomAmount();

		if (cam.isCameraDown()) {
			if (next < 90F)
				cam.setRotation(cam.rotationYaw, next);
		}
		else if (next < 60F)
			cam.setRotation(cam.rotationYaw, next);
	}

	public static void moveViewHorizontally(SecurityCamera cam, float next) {
		IBlockState state = cam.world.getBlockState(new BlockPos(cam.posX, cam.posY, cam.posZ));

		if (state.getProperties().containsKey(SecurityCameraBlock.FACING)) {
			float checkNext = next;

			if (checkNext < 0)
				checkNext += 360;

			boolean shouldSetRotation = false;

			switch (state.getValue(SecurityCameraBlock.FACING)) {
				case NORTH:
					shouldSetRotation = checkNext > 90F && checkNext < 270F;
					break;
				case SOUTH:
					shouldSetRotation = checkNext > 270F || checkNext < 90F;
					break;
				case EAST:
					shouldSetRotation = checkNext > 180F && checkNext < 360F;
					break;
				case WEST:
					shouldSetRotation = checkNext > 0F && checkNext < 180F;
					break;
				case DOWN:
					shouldSetRotation = true;
					break;
				default:
					shouldSetRotation = false;
					break;
			}

			if (shouldSetRotation)
				cam.rotationYaw = next;
		}
	}

	public static void zoomIn(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getMinecraft().world.playSound(new BlockPos(cam.posX, cam.posY, cam.posZ), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.setZoomAmount(Math.max(cam.getZoomAmount() - 0.1F, 0.1F));
	}

	public static void zoomOut(SecurityCamera cam) {
		if (!cam.zooming)
			Minecraft.getMinecraft().world.playSound(new BlockPos(cam.posX, cam.posY, cam.posZ), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.setZoomAmount(Math.min(cam.getZoomAmount() + 0.1F, 1.4F));
	}

	public static void toggleRedstone(SecurityCamera cam) {
		BlockPos pos = new BlockPos(cam.posX, cam.posY, cam.posZ);
		TileEntity be = cam.world.getTileEntity(pos);

		if (be instanceof IModuleInventory && ((IModuleInventory) be).isModuleEnabled(ModuleType.REDSTONE))
			SecurityCraft.network.sendToServer(new SetCameraPowered(pos, !cam.world.getBlockState(pos).getValue(SecurityCameraBlock.POWERED)));
	}

	public static void toggleNightVision(SecurityCamera cam) {
		SecurityCraft.network.sendToServer(new ToggleNightVision());
	}

	public static void setDefaultViewingDirection(SecurityCamera cam) {
		SecurityCraft.network.sendToServer(new SetDefaultCameraViewingDirection(cam));
	}

	public static boolean isLinked(FrameBlockEntity be, GlobalPos cameraPos) {
		return FRAME_LINKS.containsKey(cameraPos) && FRAME_LINKS.get(cameraPos).contains(be.getPos());
	}

	public static void addFrameLink(FrameBlockEntity be, GlobalPos cameraPos) {
		Set<BlockPos> bes = FRAME_LINKS.computeIfAbsent(cameraPos, p -> new HashSet<>());

		bes.add(be.getPos());
		FRAME_CAMERA_FEEDS.computeIfAbsent(cameraPos, CameraController::setUpCameraSections);
	}

	private static CameraFeed setUpCameraSections(GlobalPos cameraPos) {
		int resolution = ConfigHandler.frameFeedResolution;
		BlockPos pos = cameraPos.pos();
		ContainerLocalRenderInformation startingSection = Minecraft.getMinecraft().renderGlobal.new ContainerLocalRenderInformation(CameraViewAreaExtension.rawFetch(pos.getX() >> 4, MathHelper.clamp(pos.getY() >> 4, 0, 15), pos.getZ() >> 4, true), null, 0);
		CameraFeed cameraFeed = new CameraFeed(new Framebuffer(resolution, resolution, true), new AtomicDouble(), new ArrayList<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>());

		cameraFeed.compilingSectionsQueue.add(Pair.of(startingSection.renderChunk, false));
		cameraFeed.sectionsInRange.add(startingSection);
		cameraFeed.sectionsInRangePositions.add(startingSection.renderChunk.getPosition().toLong());
		CameraController.discoverVisibleSections(cameraPos, getFrameFeedViewDistance(null), cameraFeed);
		return cameraFeed;
	}

	public static void removeFrameLink(FrameBlockEntity be, GlobalPos cameraPos) {
		if (FRAME_LINKS.containsKey(cameraPos)) {
			Set<BlockPos> linkedFrames = FRAME_LINKS.get(cameraPos);

			linkedFrames.remove(be.getPos());

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

	public static void discoverVisibleSections(GlobalPos cameraPos, int viewDistance, CameraFeed feed) {
		ChunkPos cameraSectionPos = new ChunkPos(cameraPos.pos());
		List<ContainerLocalRenderInformation> visibleSections = feed.sectionsInRange;
		List<Pair<RenderChunk, Boolean>> sectionQueue = feed.compilingSectionsQueue;
		Set<Long> visibleSectionPositions = feed.sectionsInRangePositions;
		Deque<Pair<RenderChunk, Boolean>> queueToCheck = new ArrayDeque<>(sectionQueue);

		sectionQueue.clear();

		while (!queueToCheck.isEmpty()) {
			RenderChunk currentSection = queueToCheck.poll().getLeft();
			BlockPos origin = currentSection.getPosition();
			CompiledChunk currentCompiledSection = currentSection.getCompiledChunk();

			if (!hasAllNeighbors(currentSection)) {
				sectionQueue.add(Pair.of(currentSection, false));
				continue;
			}
			else if (currentCompiledSection == CompiledChunk.DUMMY) {
				sectionQueue.add(Pair.of(currentSection, true));
				continue;
			}

			//Once a section in the queue is compiled, it knows which neighbours it can and cannot see. This information is used to more cleverly determine which chunks the player can actually see
			for (EnumFacing dir : EnumFacing.values()) {
				int cx = (origin.getX() >> 4) + dir.getXOffset();
				int cy = (origin.getY() >> 4) + dir.getYOffset();
				int cz = (origin.getZ() >> 4) + dir.getZOffset();

				if (Utils.isInViewDistance(cameraSectionPos.x, cameraSectionPos.z, viewDistance, cx, cz)) {
					RenderChunk neighbourSection = CameraViewAreaExtension.rawFetch(cx, cy, cz, true);

					if (neighbourSection != null) {
						long neighbourPosAsLong = neighbourSection.getPosition().toLong();

						if (!visibleSectionPositions.contains(neighbourPosAsLong) && canSeeNeighborFace(currentCompiledSection, dir)) {
							ContainerLocalRenderInformation neighbourChunkInfo = Minecraft.getMinecraft().renderGlobal.new ContainerLocalRenderInformation(neighbourSection, null, 0);

							visibleSections.add(neighbourChunkInfo); //Yet uncompiled render sections are added to the sections-in-range list, so Minecraft will schedule to compile them
							visibleSectionPositions.add(neighbourSection.getPosition().toLong());
							sectionQueue.add(Pair.of(neighbourChunkInfo.renderChunk, false));
							FEED_FRUSTUM_UPDATE_REQUIRED.add(cameraPos);
						}
					}
				}
			}
		}
	}

	private static boolean hasAllNeighbors(RenderChunk renderChunk) {
		World world = Minecraft.getMinecraft().world;
		BlockPos chunkBlockPos = renderChunk.getPosition();
		int chunkX = chunkBlockPos.getX() >> 4;
		int chunkZ = chunkBlockPos.getZ() >> 4;

		return !world.getChunk(chunkX + 1, chunkZ).isEmpty() && !world.getChunk(chunkX - 1, chunkZ).isEmpty() && !world.getChunk(chunkX, chunkZ + 1).isEmpty() && !world.getChunk(chunkX, chunkZ - 1).isEmpty();
	}

	private static boolean canSeeNeighborFace(CompiledChunk currentCompiledSection, EnumFacing dir) {
		for (int j = 0; j < EnumFacing.values().length; j++) {
			if (currentCompiledSection.isVisible(EnumFacing.values()[j].getOpposite(), dir))
				return true;
		}

		return false;
	}

	public static int getFrameFeedViewDistance(FrameBlockEntity be) {
		int frameSpecificRenderDistance = be == null ? 32 : be.getChunkLoadingDistanceOption();

		return Math.min(frameSpecificRenderDistance, Math.min(ConfigHandler.frameFeedRenderDistance, Math.min(ConfigHandler.frameFeedViewDistance, Minecraft.getMinecraft().gameSettings.renderDistanceChunks)));
	}

	public static List<RenderChunk> getDirtyRenderChunks(CameraFeed feed) {
		List<RenderChunk> dirtyRenderChunks = new ArrayList<>();

		for (ContainerLocalRenderInformation container : feed.sectionsInRange) {
			RenderChunk renderChunk = container.renderChunk;

			if (renderChunk.needsUpdate())
				dirtyRenderChunks.add(renderChunk);
		}

		return dirtyRenderChunks;
	}

	public static float getMovementSpeed(SecurityCamera cam) {
		SecurityCameraBlockEntity be = cam.getBlockEntity();

		if (be != null)
			return (float) be.getMovementSpeed();

		return 0.0F;
	}

	public static class ViewMovementKeyHandler {
		private final KeyBinding key;
		private final Consumer<SecurityCamera> action;
		private boolean wasPressed;

		public ViewMovementKeyHandler(KeyBinding key, Consumer<SecurityCamera> action) {
			this.key = key;
			this.action = action;
		}

		public void tickStart() {
			wasPressed = key.isKeyDown();

			if (wasPressed)
				KeyBinding.setKeyBindState(key.getKeyCode(), false);
		}

		public void tickEnd(SecurityCamera cam) {
			if (wasPressed) {
				action.accept(cam);
				KeyBinding.setKeyBindState(key.getKeyCode(), true);
			}
		}
	}

	public static class CameraFeed {
		protected final Framebuffer renderTarget;
		protected final AtomicDouble lastActiveTime;
		protected final List<ContainerLocalRenderInformation> sectionsInRange;
		protected final Set<Long> sectionsInRangePositions;
		protected final List<ContainerLocalRenderInformation> visibleSections;
		protected final List<Pair<RenderChunk, Boolean>> compilingSectionsQueue;
		protected Vector3f backgroundColor = new Vector3f(0, 0, 0);

		public CameraFeed(Framebuffer renderTarget, AtomicDouble lastActiveTime, List<ContainerLocalRenderInformation> sectionsInRange, Set<Long> sectionsInRangePositions, List<ContainerLocalRenderInformation> visibleSections, List<Pair<RenderChunk, Boolean>> compilingSectionsQueue) {
			this.renderTarget = renderTarget;
			this.lastActiveTime = lastActiveTime;
			this.sectionsInRange = sectionsInRange;
			this.sectionsInRangePositions = sectionsInRangePositions;
			this.visibleSections = visibleSections;
			this.compilingSectionsQueue = compilingSectionsQueue;
		}

		public Framebuffer renderTarget() {
			return renderTarget;
		}

		public AtomicDouble lastActiveTime() {
			return lastActiveTime;
		}

		public List<ContainerLocalRenderInformation> sectionsInRange() {
			return sectionsInRange;
		}

		public Set<Long> sectionsInRangePositions() {
			return sectionsInRangePositions;
		}

		public List<ContainerLocalRenderInformation> visibleSections() {
			return visibleSections;
		}

		public List<RenderChunk> getSectionsToCompile() {
			return compilingSectionsQueue.stream().filter(p -> p.getRight() && (p.getLeft().compileTask == null || !p.getLeft().compileTask.isFinished())).map(Pair::getLeft).collect(Collectors.toList());
		}

		public Vector3f getBackgroundColor() {
			return backgroundColor;
		}

		public void setBackgroundColor(Vector3f newColor) {
			backgroundColor = newColor;
		}
	}
}
