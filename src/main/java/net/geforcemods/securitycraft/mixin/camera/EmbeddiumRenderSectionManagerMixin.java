package net.geforcemods.securitycraft.mixin.camera;

import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.chunk.lists.SortedRenderLists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.geforcemods.securitycraft.compat.embeddium.IEmbeddiumRenderSectionManagerAccessor;

// TODO: Javadoc
@Mixin(RenderSectionManager.class)
public class EmbeddiumRenderSectionManagerMixin implements IEmbeddiumRenderSectionManagerAccessor {
	@Shadow
	private SortedRenderLists renderLists;

	@Override
	public SortedRenderLists securitycraft$switchRenderLists(SortedRenderLists newRenderLists) {
		SortedRenderLists oldRenderLists = renderLists;

		renderLists = newRenderLists;
		return oldRenderLists;
	}
}
