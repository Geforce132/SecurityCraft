package net.geforcemods.securitycraft.misc;

import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

public enum CameraRedstoneModuleState {
	NOT_INSTALLED((pose, x, y) -> {
		RenderSystem._setShaderTexture(0, SCClientEventHandler.CAMERA_DASHBOARD);
		GuiComponent.blit(pose, x, y, 104, 0, 12, 12);
	}),
	DEACTIVATED((pose, x, y) -> {
		RenderSystem._setShaderTexture(0, SCClientEventHandler.CAMERA_DASHBOARD);
		GuiComponent.blit(pose, x, y, 90, 0, 12, 12);
	}),
	ACTIVATED((pose, x, y) -> Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(pose, SCClientEventHandler.REDSTONE, x - 2, y - 2));

	private final TriConsumer<PoseStack, Integer, Integer> renderer;

	CameraRedstoneModuleState(TriConsumer<PoseStack, Integer, Integer> renderer) {
		this.renderer = renderer;
	}

	public void render(PoseStack pose, int x, int y) {
		renderer.accept(pose, x, y);
	}
}