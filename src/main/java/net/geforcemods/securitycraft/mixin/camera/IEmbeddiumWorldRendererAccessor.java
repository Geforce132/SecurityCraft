package net.geforcemods.securitycraft.mixin.camera;

import org.embeddedt.embeddium.impl.render.EmbeddiumWorldRenderer;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Helper for accessing the render section manager in order to switch the render section list when a frame feed is captured
 */
@Mixin(value = EmbeddiumWorldRenderer.class, remap = false)
public interface IEmbeddiumWorldRendererAccessor {
    @Accessor("renderSectionManager")
    RenderSectionManager securitycraft$getRenderSectionManager();
}
