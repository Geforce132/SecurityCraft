package net.geforcemods.securitycraft.compat.embeddium;

import org.embeddedt.embeddium.impl.render.chunk.lists.SortedRenderLists;

/**
 * Helper for switching the render section list when a frame feed is captured
 */
public interface IEmbeddiumRenderSectionManagerAccessor {
    SortedRenderLists securitycraft$switchRenderLists(SortedRenderLists context);
}
