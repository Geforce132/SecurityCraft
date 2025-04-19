package net.geforcemods.securitycraft.entity.camera;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.CompiledSection;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.world.phys.AABB;

public class CameraFeed {
	private final Set<BlockPos> linkedFrames = new HashSet<>();
	private final AtomicDouble lastActiveTime = new AtomicDouble();
	private final List<RenderSection> sectionsInRange = new ArrayList<>();
	private final Set<Long> sectionsInRangePositions = new HashSet<>();
	private final List<RenderSection> visibleSections = new ArrayList<>();
	private final List<RenderSection> compilingSectionsQueue = new ArrayList<>();
	private final RenderTarget renderTarget;
	private Vector3f backgroundColor = new Vector3f();
	private boolean requiresFrustumUpdate = false;

	public CameraFeed(GlobalPos globalPos, RenderSection startingSection) {
		int resolution = ConfigHandler.CLIENT.frameFeedResolution.get();

		this.renderTarget = new TextureTarget("securitycraft:frame", resolution, resolution, true);
		compilingSectionsQueue.add(startingSection);
		sectionsInRange.add(startingSection);
		sectionsInRangePositions.add(startingSection.getRenderOrigin().asLong());
		discoverVisibleSections(globalPos, FrameFeedHandler.getFrameFeedViewDistance(null));
	}

	public void requestFrustumUpdate() {
		requiresFrustumUpdate = true;
	}

	public boolean requiresFrustumUpdate() {
		return requiresFrustumUpdate;
	}

	public void discoverVisibleSections(GlobalPos cameraPos, int viewDistance) {
		SectionPos cameraSectionPos = SectionPos.of(cameraPos.pos());
		Deque<RenderSection> queueToCheck = new ArrayDeque<>(compilingSectionsQueue);

		compilingSectionsQueue.clear();

		while (!queueToCheck.isEmpty()) {
			RenderSection currentSection = queueToCheck.poll();
			BlockPos origin = currentSection.getRenderOrigin();
			CompiledSection currentCompiledSection = currentSection.getCompiled();

			if (currentCompiledSection == CompiledSection.UNCOMPILED) {
				compilingSectionsQueue.add(currentSection);
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
						long neighbourPosAsLong = neighbourSection.getRenderOrigin().asLong();

						if (!sectionsInRangePositions.contains(neighbourPosAsLong) && canSeeNeighborFace(currentCompiledSection, dir)) {
							sectionsInRange.add(neighbourSection); //Yet uncompiled render sections are added to the sections-in-range list, so Minecraft will schedule to compile them
							sectionsInRangePositions.add(neighbourSection.getRenderOrigin().asLong());
							compilingSectionsQueue.add(neighbourSection);
							requestFrustumUpdate();
						}
					}
				}
			}
		}
	}

	private boolean canSeeNeighborFace(CompiledSection currentCompiledSection, Direction dir) {
		for (int j = 0; j < Direction.values().length; j++) {
			if (currentCompiledSection.facesCanSeeEachother(Direction.values()[j].getOpposite(), dir))
				return true;
		}

		return false;
	}

	public void updateVisibleSections(Frustum frustum) {
		requiresFrustumUpdate = false;
		visibleSections.clear();

		for (RenderSection section : sectionsInRange) {
			if (frustum.isVisible(section.getBoundingBox()))
				visibleSections.add(section);
		}
	}

	public void applyVisibleSections(List<RenderSection> currentVisibleSections) {
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

	public void setBackgroundColor(Vector4f backgroundColor) {
		backgroundColor.xyz(this.backgroundColor);
	}

	public Vector3f backgroundColor() {
		return backgroundColor;
	}
}
