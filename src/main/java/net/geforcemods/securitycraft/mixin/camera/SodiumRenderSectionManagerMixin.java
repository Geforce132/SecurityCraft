package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import net.geforcemods.securitycraft.compat.sodium.ISodiumRenderSectionManagerAccessor;

/**
 * Allows switching the render section list of the render section manager when a frame feed is captured
 */
@Mixin(RenderSectionManager.class)
public class SodiumRenderSectionManagerMixin implements ISodiumRenderSectionManagerAccessor {
	@Shadow
	@Final
	private ChunkRenderList chunkRenderList;

	@Override
	public void securitycraft$clearRenderList() {
		chunkRenderList.clear();
	}
}
