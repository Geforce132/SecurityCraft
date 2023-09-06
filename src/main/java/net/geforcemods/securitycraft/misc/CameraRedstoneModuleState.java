package net.geforcemods.securitycraft.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

public enum CameraRedstoneModuleState {
	NOT_INSTALLED((gui, pose, x, y) -> {
		RenderSystem._setShaderTexture(0, SCClientEventHandler.CAMERA_DASHBOARD);
		gui.blit(pose, x, y, 104, 0, 12, 12);
	}),
	DEACTIVATED((gui, pose, x, y) -> {
		RenderSystem._setShaderTexture(0, SCClientEventHandler.CAMERA_DASHBOARD);
		gui.blit(pose, x, y, 90, 0, 12, 12);
	}),
	ACTIVATED((gui, pose, x, y) -> Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(SCClientEventHandler.REDSTONE, x - 2, y - 2));

	private final QuadConsumer<GuiComponent, PoseStack, Integer, Integer> renderer;

	CameraRedstoneModuleState(QuadConsumer<GuiComponent, PoseStack, Integer, Integer> renderer) {
		this.renderer = renderer;
	}

	public void render(GuiComponent gui, PoseStack pose, int x, int y) {
		renderer.accept(gui, pose, x, y);
	}

	public interface QuadConsumer<T, U, V, W> {
		void accept(T t, U u, V v, W w);
	}
}