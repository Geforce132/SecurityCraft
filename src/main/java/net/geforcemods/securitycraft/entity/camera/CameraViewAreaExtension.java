package net.geforcemods.securitycraft.entity.camera;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;

public class CameraViewAreaExtension {
	private static final Long2ObjectOpenHashMap<RenderChunk> SECTIONS = new Long2ObjectOpenHashMap<>();
	private static ChunkRenderDispatcher chunkRenderDispatcher;

	private CameraViewAreaExtension() {}

	public static void allChanged(ChunkRenderDispatcher newFactory) {
		chunkRenderDispatcher = newFactory;
	}

	public static RenderChunk provideSection(long sectionPos) {
		return SECTIONS.computeIfAbsent(sectionPos, CameraViewAreaExtension::createSection);
	}

	private static RenderChunk createSection(long sectionPos) {
		Minecraft mc = Minecraft.getMinecraft();
		BlockPos sectionOrigin = SectionPos.of(sectionPos).origin();
		RenderChunk chunkRender = new RenderChunk(mc.world, mc.renderGlobal, 0); //index is unused

		chunkRender.setPosition(sectionOrigin.getX(), sectionOrigin.getY(), sectionOrigin.getZ());
		return chunkRender;
	}

	public static void setDirty(int cx, int cy, int cz, boolean playerChanged) {
		RenderChunk section = rawFetch(cx, cy, cz, false);

		if (section != null)
			section.setNeedsUpdate(playerChanged);
	}

	public static void onChunkUnload(int sectionX, int sectionZ) {
		for (int sectionY = 0; sectionY < 16; sectionY++) {
			long sectionPos = SectionPos.asLong(sectionX, sectionY, sectionZ);
			RenderChunk section = SECTIONS.get(sectionPos);

			if (section != null) {
				section.deleteGlResources();
				SECTIONS.remove(sectionPos);
			}
		}
	}

	public static RenderChunk rawFetch(int cx, int cy, int cz, boolean generateNew) {
		if (cy < 0 || cy >= 16)
			return null;

		long sectionPos = SectionPos.asLong(cx, cy, cz);

		return generateNew ? provideSection(sectionPos) : SECTIONS.get(sectionPos);
	}

	public static void clear() {
		for (RenderChunk section : SECTIONS.values()) {
			section.deleteGlResources();
		}

		SECTIONS.clear();
	}
}
