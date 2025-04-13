package net.geforcemods.securitycraft.entity.camera;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer.RenderChunkInfo;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.AABB;

public class CameraFeed {
	private final Set<BlockPos> linkedFrames = new HashSet<>();
	private final AtomicDouble lastActiveTime = new AtomicDouble();
	private final List<RenderChunkInfo> sectionsInRange = new ArrayList<>();
	private final Set<Long> sectionsInRangePositions = new HashSet<>();
	private final List<RenderChunkInfo> visibleSections = new ArrayList<>();
	private final List<RenderChunkInfo> compilingSectionsQueue = new ArrayList<>();
	private final RenderTarget renderTarget;
	private boolean requiresFrustumUpdate = false;

	public CameraFeed(GlobalPos globalPos, RenderChunkInfo startingSection) {
		int resolution = ConfigHandler.CLIENT.frameFeedResolution.get();

		this.renderTarget = new TextureTarget(resolution, resolution, true, Minecraft.ON_OSX);
		compilingSectionsQueue.add(startingSection);
		sectionsInRange.add(startingSection);
		sectionsInRangePositions.add(startingSection.chunk.getOrigin().asLong());
		discoverVisibleSections(globalPos, CameraController.getFrameFeedViewDistance(null));
	}

	public void requestFrustumUpdate() {
		requiresFrustumUpdate = true;
	}

	public boolean requiresFrustumUpdate() {
		return requiresFrustumUpdate;
	}

	public void discoverVisibleSections(GlobalPos cameraPos, int viewDistance) {
		SectionPos cameraSectionPos = SectionPos.of(cameraPos.pos());
		Deque<RenderChunkInfo> queueToCheck = new ArrayDeque<>(compilingSectionsQueue);

		compilingSectionsQueue.clear();

		while (!queueToCheck.isEmpty()) {
			RenderChunkInfo currentSectionInfo = queueToCheck.poll();
			RenderChunk currentSection = currentSectionInfo.chunk;
			BlockPos origin = currentSection.getOrigin();
			CompiledChunk currentCompiledSection = currentSection.getCompiledChunk();

			if (currentCompiledSection == CompiledChunk.UNCOMPILED) {
				compilingSectionsQueue.add(currentSectionInfo);
				continue;
			}

			//Once a section in the queue is compiled, it knows which neighbours it can and cannot see. This information is used to more cleverly determine which chunks the player can actually see
			for (Direction dir : Direction.values()) {
				int cx = SectionPos.blockToSectionCoord(origin.getX()) + dir.getStepX();
				int cy = SectionPos.blockToSectionCoord(origin.getY()) + dir.getStepY();
				int cz = SectionPos.blockToSectionCoord(origin.getZ()) + dir.getStepZ();

				if (Utils.isInViewDistance(cameraSectionPos.x(), cameraSectionPos.z(), viewDistance, cx, cz)) {
					RenderChunk neighbourSection = CameraViewAreaExtension.rawFetch(cx, cy, cz, true);

					if (neighbourSection != null) {
						long neighbourPosAsLong = neighbourSection.getOrigin().asLong();

						if (!sectionsInRangePositions.contains(neighbourPosAsLong) && canSeeNeighborFace(currentCompiledSection, dir)) {
							RenderChunkInfo neighbourChunkInfo = new RenderChunkInfo(neighbourSection, null, 0);

							sectionsInRange.add(neighbourChunkInfo); //Yet uncompiled render sections are added to the sections-in-range list, so Minecraft will schedule to compile them
							sectionsInRangePositions.add(neighbourSection.getOrigin().asLong());
							compilingSectionsQueue.add(neighbourChunkInfo);
							requestFrustumUpdate();
						}
					}
				}
			}
		}
	}

	private boolean canSeeNeighborFace(CompiledChunk currentCompiledSection, Direction dir) {
		for (int j = 0; j < Direction.values().length; j++) {
			if (currentCompiledSection.facesCanSeeEachother(Direction.values()[j].getOpposite(), dir))
				return true;
		}

		return false;
	}

	public void updateVisibleSections(Frustum frustum) {
		requiresFrustumUpdate = false;
		visibleSections.clear();

		for (RenderChunkInfo section : sectionsInRange) {
			if (frustum.isVisible(section.chunk.getBoundingBox()))
				visibleSections.add(section);
		}
	}

	public void applyVisibleSections(List<RenderChunkInfo> currentVisibleSections) {
		currentVisibleSections.clear();
		currentVisibleSections.addAll(visibleSections);
	}

	public boolean hasVisibleSections() {
		return !visibleSections.isEmpty();
	}

	public boolean hasFrameInFrustum(Frustum frustum) {
		for (BlockPos framePos : linkedFrames) {
			if (frustum.isVisible(new AABB(framePos)))
				return true;
		}

		return false;
	}

	public void linkFrame(FrameBlockEntity be) {
		linkedFrames.add(be.getBlockPos());
	}

	public void unlinkFrame(FrameBlockEntity be) {
		linkedFrames.remove(be.getBlockPos());
	}

	public boolean isFrameLinked(FrameBlockEntity be) {
		return linkedFrames.contains(be.getBlockPos());
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

	public RenderTarget renderTarget() {
		return renderTarget;
	}
}
