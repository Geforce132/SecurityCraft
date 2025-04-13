package net.geforcemods.securitycraft.entity.camera;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.util.concurrent.AtomicDouble;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer.LocalRenderInformationContainer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.ChunkRender;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.SectionPos;

public class CameraFeed {
	private final Set<BlockPos> linkedFrames = new HashSet<>();
	private final AtomicDouble lastActiveTime = new AtomicDouble();
	private final List<LocalRenderInformationContainer> sectionsInRange = new ArrayList<>();
	private final Set<Long> sectionsInRangePositions = new HashSet<>();
	private final List<LocalRenderInformationContainer> visibleSections = new ArrayList<>();
	private final List<Pair<ChunkRender, Boolean>> compilingSectionsQueue = new ArrayList<>();
	private final Framebuffer renderTarget;
	private boolean requiresFrustumUpdate = false;

	public CameraFeed(GlobalPos globalPos, LocalRenderInformationContainer startingSection) {
		int resolution = ConfigHandler.CLIENT.frameFeedResolution.get();

		this.renderTarget = new Framebuffer(resolution, resolution, true, Minecraft.ON_OSX);
		compilingSectionsQueue.add(Pair.of(startingSection.chunk, false));
		sectionsInRange.add(startingSection);
		sectionsInRangePositions.add(startingSection.chunk.getOrigin().asLong());
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
		Deque<Pair<ChunkRender, Boolean>> queueToCheck = new ArrayDeque<>(compilingSectionsQueue);

		compilingSectionsQueue.clear();

		while (!queueToCheck.isEmpty()) {
			ChunkRender currentSection = queueToCheck.poll().getLeft();
			BlockPos origin = currentSection.getOrigin();
			CompiledChunk currentCompiledSection = currentSection.getCompiledChunk();

			if (!currentSection.hasAllNeighbors()) {
				compilingSectionsQueue.add(Pair.of(currentSection, false));
				continue;
			}
			else if (currentCompiledSection == CompiledChunk.UNCOMPILED) {
				compilingSectionsQueue.add(Pair.of(currentSection, true));
				continue;
			}

			//Once a section in the queue is compiled, it knows which neighbours it can and cannot see. This information is used to more cleverly determine which chunks the player can actually see
			for (Direction dir : Direction.values()) {
				int cx = SectionPos.blockToSectionCoord(origin.getX()) + dir.getStepX();
				int cy = SectionPos.blockToSectionCoord(origin.getY()) + dir.getStepY();
				int cz = SectionPos.blockToSectionCoord(origin.getZ()) + dir.getStepZ();

				if (Utils.isInViewDistance(cameraSectionPos.x(), cameraSectionPos.z(), viewDistance, cx, cz)) {
					ChunkRender neighbourSection = CameraViewAreaExtension.rawFetch(cx, cy, cz, true);

					if (neighbourSection != null) {
						long neighbourPosAsLong = neighbourSection.getOrigin().asLong();

						if (!sectionsInRangePositions.contains(neighbourPosAsLong) && canSeeNeighborFace(currentCompiledSection, dir)) {
							LocalRenderInformationContainer neighbourChunkInfo = Minecraft.getInstance().levelRenderer.new LocalRenderInformationContainer(neighbourSection, null, 0);

							sectionsInRange.add(neighbourChunkInfo); //Yet uncompiled render sections are added to the sections-in-range list, so Minecraft will schedule to compile them
							sectionsInRangePositions.add(neighbourSection.getOrigin().asLong());
							compilingSectionsQueue.add(Pair.of(neighbourChunkInfo.chunk, false));
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

	public void updateVisibleSections(ClippingHelper frustum) {
		requiresFrustumUpdate = false;
		visibleSections.clear();

		for (LocalRenderInformationContainer section : sectionsInRange) {
			if (frustum.isVisible(section.chunk.bb))
				visibleSections.add(section);
		}
	}

	public void applyVisibleSections(List<LocalRenderInformationContainer> currentRenderChunks, Set<ChunkRender> currentChunksToCompile) {
		currentRenderChunks.clear();
		currentRenderChunks.addAll(visibleSections);
		currentChunksToCompile.clear();
		currentChunksToCompile.addAll(getSectionsToCompile());
	}

	public boolean hasVisibleSections() {
		return !visibleSections.isEmpty();
	}

	public List<ChunkRender> getSectionsToCompile() {
		return compilingSectionsQueue.stream().filter(p -> p.getRight() && p.getLeft().lastRebuildTask == null).map(Pair::getLeft).collect(Collectors.toList());
	}

	public List<ChunkRender> getDirtyRenderChunks() {
		List<ChunkRender> dirtyRenderChunks = new ArrayList<>();

		for (LocalRenderInformationContainer container : sectionsInRange) {
			ChunkRender renderChunk = container.chunk;

			if (renderChunk.isDirty() && renderChunk.hasAllNeighbors() && renderChunk.lastRebuildTask == null)
				dirtyRenderChunks.add(renderChunk);
		}

		return dirtyRenderChunks;
	}

	public boolean hasFrameInFrustum(ClippingHelper frustum) {
		for (BlockPos framePos : linkedFrames) {
			if (frustum.isVisible(new AxisAlignedBB(framePos)))
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

	public Framebuffer renderTarget() {
		return renderTarget;
	}
}
