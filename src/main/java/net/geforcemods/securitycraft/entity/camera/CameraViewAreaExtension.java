package net.geforcemods.securitycraft.entity.camera;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;

public class CameraViewAreaExtension {
	private static final Long2ObjectOpenHashMap<RenderChunk> SECTIONS = new Long2ObjectOpenHashMap<>();
	private static ChunkRenderDispatcher chunkRenderDispatcher;
	private static int minSectionY;
	private static int maxSectionY;

	private CameraViewAreaExtension() {}

	public static void allChanged(ChunkRenderDispatcher newFactory, Level level) {
		chunkRenderDispatcher = newFactory;
		minSectionY = level.getMinSection();
		maxSectionY = level.getMaxSection();
	}

	public static RenderChunk provideSection(long sectionPos) {
		return SECTIONS.computeIfAbsent(sectionPos, CameraViewAreaExtension::createSection);
	}

	private static RenderChunk createSection(long sectionPos) {
		BlockPos sectionOrigin = SectionPos.of(sectionPos).origin();

		return chunkRenderDispatcher.new RenderChunk(0, sectionOrigin.getX(), sectionOrigin.getY(), sectionOrigin.getZ());
	}

	public static void setDirty(int cx, int cy, int cz, boolean playerChanged) {
		RenderChunk section = rawFetch(cx, cy, cz, false);

		if (section != null)
			section.setDirty(playerChanged);
	}

	public static void onChunkUnload(int sectionX, int sectionZ) {
		for (int sectionY = minSectionY; sectionY < maxSectionY; sectionY++) {
			long sectionPos = SectionPos.asLong(sectionX, sectionY, sectionZ);
			RenderChunk section = SECTIONS.get(sectionPos);

			if (section != null) {
				section.releaseBuffers();
				SECTIONS.remove(sectionPos);
			}
		}
	}

	public static RenderChunk rawFetch(int cx, int cy, int cz, boolean generateNew) {
		if (cy < minSectionY || cy >= maxSectionY)
			return null;

		long sectionPos = SectionPos.asLong(cx, cy, cz);

		return generateNew ? provideSection(sectionPos) : SECTIONS.get(sectionPos);
	}

	public static void clear() {
		for (RenderChunk section : SECTIONS.values()) {
			section.releaseBuffers();
		}

		SECTIONS.clear();
	}

	public static int minSectionY() {
		return minSectionY;
	}

	public static int maxSectionY() {
		return maxSectionY;
	}
}
