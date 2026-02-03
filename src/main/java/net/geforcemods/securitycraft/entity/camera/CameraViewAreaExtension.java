package net.geforcemods.securitycraft.entity.camera;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;

public class CameraViewAreaExtension {
	private static final Long2ObjectOpenHashMap<RenderSection> SECTIONS = new Long2ObjectOpenHashMap<>();
	private static SectionRenderDispatcher sectionRenderDispatcher;
	private static int minSectionY;
	private static int maxSectionY;

	private CameraViewAreaExtension() {}

	public static void allChanged(SectionRenderDispatcher newFactory, Level level) {
		sectionRenderDispatcher = newFactory;
		minSectionY = level.getMinSectionY();
		maxSectionY = level.getMaxSectionY();
	}

	public static RenderSection provideSection(long sectionPos) {
		return SECTIONS.computeIfAbsent(sectionPos, CameraViewAreaExtension::createSection);
	}

	private static RenderSection createSection(long sectionPos) {
		return sectionRenderDispatcher.new RenderSection(0, sectionPos);
	}

	public static void setDirty(int cx, int cy, int cz, boolean playerChanged) {
		SectionRenderDispatcher.RenderSection section = rawFetch(cx, cy, cz, false);

		if (section != null)
			section.setDirty(playerChanged);
	}

	public static void onChunkUnload(int sectionX, int sectionZ) {
		for (int sectionY = minSectionY; sectionY < maxSectionY; sectionY++) {
			long sectionPos = SectionPos.asLong(sectionX, sectionY, sectionZ);
			RenderSection section = SECTIONS.get(sectionPos);

			if (section != null) {
				section.reset();
				SECTIONS.remove(sectionPos);
			}
		}
	}

	public static SectionRenderDispatcher.RenderSection rawFetch(int cx, int cy, int cz, boolean generateNew) {
		if (cy < minSectionY || cy >= maxSectionY)
			return null;

		long sectionPos = SectionPos.asLong(cx, cy, cz);

		return generateNew ? provideSection(sectionPos) : SECTIONS.get(sectionPos);
	}

	public static void clear() {
		for (SectionRenderDispatcher.RenderSection section : SECTIONS.values()) {
			section.reset();
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
