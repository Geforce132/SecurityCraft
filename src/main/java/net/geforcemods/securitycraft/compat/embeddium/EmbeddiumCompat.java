package net.geforcemods.securitycraft.compat.embeddium;

import org.embeddedt.embeddium.impl.render.EmbeddiumWorldRenderer;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.chunk.lists.SortedRenderLists;
import org.embeddedt.embeddium.impl.world.WorldRendererExtended;

import net.geforcemods.securitycraft.mixin.camera.IEmbeddiumWorldRendererAccessor;
import net.minecraft.client.Minecraft;

// TODO: Sodium support
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
}
