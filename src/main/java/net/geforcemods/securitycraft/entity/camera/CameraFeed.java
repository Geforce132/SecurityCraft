package net.geforcemods.securitycraft.entity.camera;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.util.concurrent.AtomicDouble;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class CameraFeed {
	private final Set<BlockPos> linkedFrames = new HashSet<>();
	private final AtomicDouble lastActiveTime = new AtomicDouble();
	private final List<ContainerLocalRenderInformation> sectionsInRange = new ArrayList<>();
	private final Set<Long> sectionsInRangePositions = new HashSet<>();
	private final List<ContainerLocalRenderInformation> visibleSections = new ArrayList<>();
	private final List<Pair<RenderChunk, Boolean>> compilingSectionsQueue = new ArrayList<>();
	private final Framebuffer renderTarget;
	private final boolean createdUsingVbo;
	private boolean requiresFrustumUpdate = false;
	private Vector3f backgroundColor = new Vector3f(0, 0, 0);

	public CameraFeed(GlobalPos globalPos, ContainerLocalRenderInformation startingSection) {
		int resolution = ConfigHandler.frameFeedResolution;

		this.renderTarget = new Framebuffer(resolution, resolution, true);
		compilingSectionsQueue.add(Pair.of(startingSection.renderChunk, false));
		sectionsInRange.add(startingSection);
		sectionsInRangePositions.add(startingSection.renderChunk.getPosition().toLong());
		createdUsingVbo = OpenGlHelper.useVbo();
		discoverVisibleSections(globalPos, FrameFeedHandler.getFrameFeedViewDistance(null));
	}

	public void requestFrustumUpdate() {
		requiresFrustumUpdate = true;
	}

	public boolean requiresFrustumUpdate() {
		return requiresFrustumUpdate;
	}

	public void discoverVisibleSections(GlobalPos cameraPos, int viewDistance) {
		ChunkPos cameraSectionPos = new ChunkPos(cameraPos.pos());
		Deque<Pair<RenderChunk, Boolean>> queueToCheck = new ArrayDeque<>(compilingSectionsQueue);

		compilingSectionsQueue.clear();

		while (!queueToCheck.isEmpty()) {
			RenderChunk currentSection = queueToCheck.poll().getLeft();
			BlockPos origin = currentSection.getPosition();
			CompiledChunk currentCompiledSection = currentSection.getCompiledChunk();

			if (!hasAllNeighbors(currentSection)) {
				compilingSectionsQueue.add(Pair.of(currentSection, false));
				continue;
			}
			else if (currentCompiledSection == CompiledChunk.DUMMY) {
				compilingSectionsQueue.add(Pair.of(currentSection, true));
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

						if (!sectionsInRangePositions.contains(neighbourPosAsLong) && canSeeNeighborFace(currentCompiledSection, dir)) {
							ContainerLocalRenderInformation neighbourChunkInfo = Minecraft.getMinecraft().renderGlobal.new ContainerLocalRenderInformation(neighbourSection, null, 0);

							sectionsInRange.add(neighbourChunkInfo); //Yet uncompiled render sections are added to the sections-in-range list, so Minecraft will schedule to compile them
							sectionsInRangePositions.add(neighbourSection.getPosition().toLong());
							compilingSectionsQueue.add(Pair.of(neighbourChunkInfo.renderChunk, false));
							requestFrustumUpdate();
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

	public void updateVisibleSections(Frustum frustum) {
		requiresFrustumUpdate = false;
		visibleSections.clear();

		for (ContainerLocalRenderInformation section : sectionsInRange) {
			AxisAlignedBB bb = section.renderChunk.boundingBox;

			if (frustum == null || frustum.isBoxInFrustum(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ))
				visibleSections.add(section);
		}
	}

	public void applyVisibleSections(List<ContainerLocalRenderInformation> currentRenderChunks, Set<RenderChunk> currentChunksToCompile) {
		currentRenderChunks.clear();
		currentRenderChunks.addAll(visibleSections);
		currentChunksToCompile.clear();
		currentChunksToCompile.addAll(getSectionsToCompile());
	}

	public boolean hasVisibleSections() {
		return !visibleSections.isEmpty();
	}

	public List<RenderChunk> getSectionsToCompile() {
		return compilingSectionsQueue.stream().filter(p -> p.getRight() && (p.getLeft().compileTask == null || !p.getLeft().compileTask.isFinished())).map(Pair::getLeft).collect(Collectors.toList());
	}

	public List<RenderChunk> getDirtyRenderChunks() {
		List<RenderChunk> dirtyRenderChunks = new ArrayList<>();

		for (ContainerLocalRenderInformation container : sectionsInRange) {
			RenderChunk renderChunk = container.renderChunk;

			if (renderChunk.needsUpdate())
				dirtyRenderChunks.add(renderChunk);
		}

		return dirtyRenderChunks;
	}

	public boolean hasFrameInFrustum(Frustum frustum) {
		for (BlockPos framePos : linkedFrames) {
			AxisAlignedBB bb = new AxisAlignedBB(framePos);

			if (frustum.isBoxInFrustum(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ))
				return true;
		}

		return false;
	}

	public void linkFrame(FrameBlockEntity be) {
		linkedFrames.add(be.getPos());
	}

	public void unlinkFrame(FrameBlockEntity be) {
		linkedFrames.remove(be.getPos());
	}

	public boolean isFrameLinked(FrameBlockEntity be) {
		return linkedFrames.contains(be.getPos());
	}

	public void markForRemoval() {
		linkedFrames.clear();
	}

	public boolean shouldBeRemoved() {
		return linkedFrames.isEmpty();
	}

	public AtomicDouble lastActiveTime() {
		return lastActiveTime;
	}

	public Framebuffer renderTarget() {
		return renderTarget;
	}

	public Vector3f backgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Vector3f newColor) {
		backgroundColor = newColor;
	}

	public boolean usesVbo() {
		return createdUsingVbo;
	}
}
