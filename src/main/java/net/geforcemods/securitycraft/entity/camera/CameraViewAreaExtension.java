package net.geforcemods.securitycraft.entity.camera;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;

public class CameraViewAreaExtension {
	private static final Long2ObjectOpenHashMap<RenderChunk> SECTIONS = new Long2ObjectOpenHashMap<>();

	private CameraViewAreaExtension() {}

	public static RenderChunk provideSection(long sectionPos) {
		return SECTIONS.computeIfAbsent(sectionPos, CameraViewAreaExtension::createSection);
	}

	private static RenderChunk createSection(long sectionPos) {
		Minecraft mc = Minecraft.getMinecraft();
		BlockPos sectionOrigin = sectionLongToBlockPos(sectionPos);
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
			SECTIONS.remove(sectionPosToLong(sectionX, sectionY, sectionZ));
		}
	}

	public static RenderChunk rawFetch(int cx, int cy, int cz, boolean generateNew) {
		if (cy < 0 || cy >= 16)
			return null;

		long sectionPos = sectionPosToLong(cx, cy, cz);

		return generateNew ? provideSection(sectionPos) : SECTIONS.get(sectionPos);
	}

	private static long sectionPosToLong(int sx, int sy, int sz) {
		long sectionPosAsLong = 0L; //Filled with: 22 bits x position, 20 bits y position, 22 bits z position

		sectionPosAsLong |= ((long) sx & 0x3FFFFF) << 42;
		sectionPosAsLong |= ((long) sy & 0x0FFFFF);
		sectionPosAsLong |= ((long) sz & 0x3FFFFF) << 20;

		return sectionPosAsLong;
	}

	private static BlockPos sectionLongToBlockPos(long sectionPosAsLong) {
		return new BlockPos((sectionPosAsLong >> 42) << 4, (sectionPosAsLong << 44 >> 44) << 4, (sectionPosAsLong << 22 >> 42) << 4);
	}

	public static void clear() {
		for (RenderChunk section : SECTIONS.values()) {
			section.deleteGlResources();
		}

		SECTIONS.clear();
	}
}
