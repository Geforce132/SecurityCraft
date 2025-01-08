package net.geforcemods.securitycraft.compat.sodium;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.geforcemods.securitycraft.mixin.camera.ISodiumWorldRendererAccessor;
import net.minecraft.client.Minecraft;

public class SodiumCompat {
	private SodiumCompat() {}

	public static void clearRenderList() {
		SodiumWorldRenderer worldRenderer = ((WorldRendererExtended) Minecraft.getInstance().levelRenderer).getSodiumWorldRenderer();
		RenderSectionManager renderSectionManager = ((ISodiumWorldRendererAccessor) worldRenderer).securitycraft$getRenderSectionManager();

		((ISodiumRenderSectionManagerAccessor) renderSectionManager).securitycraft$clearRenderList();
	}
}
