package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;

/**
 * Helper for accessing the render section manager in order to switch the render section list when a frame feed is captured
 */
@Mixin(value = SodiumWorldRenderer.class, remap = false)
public interface ISodiumWorldRendererAccessor {
	@Accessor("renderSectionManager")
	RenderSectionManager securitycraft$getRenderSectionManager();
}
