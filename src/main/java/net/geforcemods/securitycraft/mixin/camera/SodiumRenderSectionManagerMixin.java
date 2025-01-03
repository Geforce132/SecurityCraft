package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import net.geforcemods.securitycraft.compat.sodium.ISodiumRenderSectionManagerAccessor;

/**
 * Allows switching the render section list of the render section manager when a frame feed is captured
 */
@Mixin(RenderSectionManager.class)
public class SodiumRenderSectionManagerMixin implements ISodiumRenderSectionManagerAccessor {
	@Shadow
	private SortedRenderLists renderLists;

	@Override
	public SortedRenderLists securitycraft$switchRenderLists(SortedRenderLists newRenderLists) {
		SortedRenderLists oldRenderLists = renderLists;

		renderLists = newRenderLists;
		return oldRenderLists;
	}
}
