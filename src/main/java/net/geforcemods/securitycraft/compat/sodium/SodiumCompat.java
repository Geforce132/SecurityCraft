package net.geforcemods.securitycraft.compat.sodium;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkStatus;
import me.jellysquid.mods.sodium.client.render.chunk.map.ChunkTrackerHolder;
import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.geforcemods.securitycraft.mixin.camera.ISodiumWorldRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class SodiumCompat {
	private static SortedRenderLists oldRenderLists;

	private SodiumCompat() {}

	public static void onChunkStatusAdded(ClientLevel level, int x, int z) {
		ChunkTrackerHolder.get(level).onChunkStatusAdded(x, z, ChunkStatus.FLAG_HAS_BLOCK_DATA);
	}

	public static void onChunkStatusRemoved(ClientLevel level, int x, int z) {
		ChunkTrackerHolder.get(level).onChunkStatusRemoved(x, z, ChunkStatus.FLAG_HAS_BLOCK_DATA);
	}

	public static void switchToEmptyRenderLists() {
		switchRenderLists(SortedRenderLists.empty());
	}

	public static void switchToPreviousRenderLists() {
		switchRenderLists(oldRenderLists);
	}

	private static void switchRenderLists(SortedRenderLists newRenderLists) {
		SodiumWorldRenderer worldRenderer = ((WorldRendererExtended) Minecraft.getInstance().levelRenderer).sodium$getWorldRenderer();
		RenderSectionManager renderSectionManager = ((ISodiumWorldRendererAccessor) worldRenderer).securitycraft$getRenderSectionManager();

		worldRenderer.scheduleTerrainUpdate();
		oldRenderLists = ((ISodiumRenderSectionManagerAccessor) renderSectionManager).securitycraft$switchRenderLists(newRenderLists);
		worldRenderer.scheduleTerrainUpdate();
	}
}
