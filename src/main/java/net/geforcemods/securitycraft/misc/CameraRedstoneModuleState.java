package net.geforcemods.securitycraft.misc;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

public enum CameraRedstoneModuleState {
	NOT_INSTALLED((guiGraphics, x, y) -> guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCClientEventHandler.REDSTONE_MODULE_NOT_PRESENT_SPRITE, x, y, 12, 12)),
	DEACTIVATED((guiGraphics, x, y) -> guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCClientEventHandler.REDSTONE_MODULE_PRESENT_SPRITE, x, y, 12, 12)),
	ACTIVATED((guiGraphics, x, y) -> guiGraphics.renderItem(SCClientEventHandler.REDSTONE, x - 2, y - 2));

	private final TriConsumer<GuiGraphics, Integer, Integer> renderer;

	CameraRedstoneModuleState(TriConsumer<GuiGraphics, Integer, Integer> renderer) {
		this.renderer = renderer;
	}

	public void render(GuiGraphics guiGraphics, int x, int y) {
		renderer.accept(guiGraphics, x, y);
	}
}