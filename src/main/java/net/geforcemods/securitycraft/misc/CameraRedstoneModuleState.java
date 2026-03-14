package net.geforcemods.securitycraft.misc;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

public enum CameraRedstoneModuleState {
	NOT_INSTALLED((guiGraphics, x, y) -> guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCClientEventHandler.REDSTONE_MODULE_NOT_PRESENT_SPRITE, x, y, 12, 12)),
	DEACTIVATED((guiGraphics, x, y) -> guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCClientEventHandler.REDSTONE_MODULE_PRESENT_SPRITE, x, y, 12, 12)),
	ACTIVATED((guiGraphics, x, y) -> guiGraphics.item(SCClientEventHandler.REDSTONE.create(), x - 2, y - 2));

	private final TriConsumer<GuiGraphicsExtractor, Integer, Integer> renderer;

	CameraRedstoneModuleState(TriConsumer<GuiGraphicsExtractor, Integer, Integer> renderer) {
		this.renderer = renderer;
	}

	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y) {
		renderer.accept(guiGraphics, x, y);
	}
}