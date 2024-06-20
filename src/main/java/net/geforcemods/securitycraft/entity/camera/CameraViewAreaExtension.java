package net.geforcemods.securitycraft.entity.camera;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class CameraViewAreaExtension { //plagiarized from Immersive Portals
	private static final Long2ObjectOpenHashMap<Column> SECTION_COLUMNS = new Long2ObjectOpenHashMap<>();
	private static SectionRenderDispatcher sectionRenderDispatcher;
	public static int minHeight;
	public static int minSectionY;
	public static int maxSectionY;
	public static int sectionsCountY;

	public static void allChanged(SectionRenderDispatcher newFactory, Level level) {
		sectionRenderDispatcher = newFactory;
		minHeight = level.getMinBuildHeight();
		minSectionY = level.getMinSection();
		maxSectionY = level.getMaxSection();
		sectionsCountY = maxSectionY - minSectionY;
	}

	public static SectionRenderDispatcher.RenderSection provideRenderSectionByChunkPos(int cx, int cy, int cz) {
		Column column = provideColumn(ChunkPos.asLong(cx, cz));
		int offsetChunkY = Mth.clamp(cy - minSectionY, 0, sectionsCountY - 1);

		return column.sections[offsetChunkY];
	}

	public static Column provideColumn(long sectionPos) {
		return SECTION_COLUMNS.computeIfAbsent(sectionPos, CameraViewAreaExtension::createColumn);
	}

	private static Column createColumn(long sectionPos) {
		SectionRenderDispatcher.RenderSection[] array = new SectionRenderDispatcher.RenderSection[sectionsCountY];
		int sectionX = ChunkPos.getX(sectionPos);
		int sectionZ = ChunkPos.getZ(sectionPos);

		for (int offsetCY = 0; offsetCY < sectionsCountY; offsetCY++) {
			array[offsetCY] = sectionRenderDispatcher.new RenderSection(0, sectionX << 4, (offsetCY << 4) + minHeight, sectionZ << 4);
		}

		return new Column(array);
	}

	public static void setDirty(int cx, int cy, int cz, boolean playerChanged) {
		provideRenderSectionByChunkPos(cx, cy, cz).setDirty(playerChanged);
	}

	public static void onChunkUnload(int sectionX, int sectionZ) {
		long sectionPos = ChunkPos.asLong(sectionX, sectionZ);
		Column column = SECTION_COLUMNS.get(sectionPos);

		if (column != null) {
			for (SectionRenderDispatcher.RenderSection renderSection : column.sections) {
				renderSection.reset();
			}
		}
	}

	public static SectionRenderDispatcher.RenderSection rawFetch(int cx, int cy, int cz, boolean generateNew) {
		if (cy < minSectionY || cy >= maxSectionY)
			return null;

		long chunkPos = ChunkPos.asLong(cx, cz);
		int yOffset = cy - minSectionY;
		Column column = generateNew ? provideColumn(chunkPos) : SECTION_COLUMNS.get(chunkPos);

		if (column == null)
			return null;

		return column.sections[yOffset];
	}

	public static void clear() {
		SECTION_COLUMNS.clear();
	}

	public static class Column {
		public SectionRenderDispatcher.RenderSection[] sections;

		public Column(SectionRenderDispatcher.RenderSection[] sections) {
			this.sections = sections;
		}
	}
}
