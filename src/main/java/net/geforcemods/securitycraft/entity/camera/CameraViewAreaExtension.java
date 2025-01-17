package net.geforcemods.securitycraft.entity.camera;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.ChunkRender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;

public class CameraViewAreaExtension {
	private static final Long2ObjectOpenHashMap<ChunkRender> SECTIONS = new Long2ObjectOpenHashMap<>();
	private static ChunkRenderDispatcher chunkRenderDispatcher;

	private CameraViewAreaExtension() {}

	public static void allChanged(ChunkRenderDispatcher newFactory) {
		chunkRenderDispatcher = newFactory;
	}

	public static ChunkRender provideSection(long sectionPos) {
		return SECTIONS.computeIfAbsent(sectionPos, CameraViewAreaExtension::createSection);
	}

	private static ChunkRender createSection(long sectionPos) {
		BlockPos sectionOrigin = SectionPos.of(sectionPos).origin();
		ChunkRender chunkRender = chunkRenderDispatcher.new ChunkRender();

		chunkRender.setOrigin(sectionOrigin.getX(), sectionOrigin.getY(), sectionOrigin.getZ());
		return chunkRender;
	}

	public static void setDirty(int cx, int cy, int cz, boolean playerChanged) {
		ChunkRender section = rawFetch(cx, cy, cz, false);

		if (section != null)
			section.setDirty(playerChanged);
	}

	public static void onChunkUnload(int sectionX, int sectionZ) {
		for (int sectionY = 0; sectionY < 16; sectionY++) {
			long sectionPos = SectionPos.asLong(sectionX, sectionY, sectionZ);
			ChunkRender section = SECTIONS.get(sectionPos);

			if (section != null) {
				section.releaseBuffers();
				SECTIONS.remove(sectionPos);
			}
		}
	}

	public static ChunkRender rawFetch(int cx, int cy, int cz, boolean generateNew) {
		if (cy < 0 || cy >= 16)
			return null;

		long sectionPos = SectionPos.asLong(cx, cy, cz);

		return generateNew ? provideSection(sectionPos) : SECTIONS.get(sectionPos);
	}

	public static void clear() {
		for (ChunkRender section : SECTIONS.values()) {
			section.releaseBuffers();
		}

		SECTIONS.clear();
	}
}
