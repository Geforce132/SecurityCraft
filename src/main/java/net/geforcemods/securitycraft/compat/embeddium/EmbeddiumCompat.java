package net.geforcemods.securitycraft.compat.embeddium;

import org.embeddedt.embeddium.impl.render.EmbeddiumWorldRenderer;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.chunk.lists.SortedRenderLists;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkStatus;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder;
import org.embeddedt.embeddium.impl.world.WorldRendererExtended;

import net.geforcemods.securitycraft.mixin.camera.IEmbeddiumWorldRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class EmbeddiumCompat {
	private static SortedRenderLists oldRenderLists;

	private EmbeddiumCompat() {}

	public static void setEmptyRenderLists() {
		switchRenderLists(SortedRenderLists.empty());
	}

	public static void setPreviousRenderLists() {
		switchRenderLists(oldRenderLists);
	}

	private static void switchRenderLists(SortedRenderLists newRenderLists) {
		EmbeddiumWorldRenderer worldRenderer = ((WorldRendererExtended) Minecraft.getInstance().levelRenderer).sodium$getWorldRenderer();
		RenderSectionManager renderSectionManager = ((IEmbeddiumWorldRendererAccessor) worldRenderer).securitycraft$getRenderSectionManager();

		worldRenderer.scheduleTerrainUpdate();
		oldRenderLists = ((IEmbeddiumRenderSectionManagerAccessor) renderSectionManager).securitycraft$switchRenderLists(newRenderLists);
		worldRenderer.scheduleTerrainUpdate();
	}

	public static void onChunkStatusAdded(ClientLevel level, int x, int z) {
		ChunkTrackerHolder.get(level).onChunkStatusAdded(x, z, ChunkStatus.FLAG_HAS_BLOCK_DATA);
	}

	public static void onChunkStatusRemoved(ClientLevel level, int x, int z) {
		ChunkTrackerHolder.get(level).onChunkStatusRemoved(x, z, ChunkStatus.FLAG_HAS_BLOCK_DATA);
	}
}
