package net.geforcemods.securitycraft.misc;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public enum CameraRedstoneModuleState {
	NOT_INSTALLED((gui, pose, x, y) -> {
		Minecraft.getInstance().getTextureManager().bind(SCClientEventHandler.CAMERA_DASHBOARD);
		gui.blit(pose, x, y, 104, 0, 12, 12);
	}),
	DEACTIVATED((gui, pose, x, y) -> {
		Minecraft.getInstance().getTextureManager().bind(SCClientEventHandler.CAMERA_DASHBOARD);
		gui.blit(pose, x, y, 90, 0, 12, 12);
	}),
	ACTIVATED((gui, pose, x, y) -> Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(SCClientEventHandler.REDSTONE, x - 2, y - 2));

	private final QuadConsumer<AbstractGui, MatrixStack, Integer, Integer> renderer;

	CameraRedstoneModuleState(QuadConsumer<AbstractGui, MatrixStack, Integer, Integer> renderer) {
		this.renderer = renderer;
	}

	public void render(AbstractGui gui, MatrixStack pose, int x, int y) {
		renderer.accept(gui, pose, x, y);
	}

	public interface QuadConsumer<T, U, V, W> {
		void accept(T t, U u, V v, W w);
	}
}