package net.geforcemods.securitycraft.compat.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;

/**
 * Helper for switching the render section list when a frame feed is captured
 */
public interface ISodiumRenderSectionManagerAccessor {
	SortedRenderLists securitycraft$switchRenderLists(SortedRenderLists context);
}
