package net.geforcemods.securitycraft.misc;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public enum CameraRedstoneModuleState {
	NOT_INSTALLED((gui, x, y) -> {
		Minecraft.getMinecraft().getTextureManager().bindTexture(SCClientEventHandler.CAMERA_DASHBOARD);
		gui.drawTexturedModalRect(x, y, 104, 0, 12, 12);
	}),
	DEACTIVATED((gui, x, y) -> {
		Minecraft.getMinecraft().getTextureManager().bindTexture(SCClientEventHandler.CAMERA_DASHBOARD);
		gui.drawTexturedModalRect(x, y, 90, 0, 12, 12);
	}),
	ACTIVATED((gui, x, y) -> GuiUtils.drawItemStackToGui(SCClientEventHandler.REDSTONE, x - 2, y - 2, false));

	private final TriConsumer<Gui, Integer, Integer> renderer;

	CameraRedstoneModuleState(TriConsumer<Gui, Integer, Integer> renderer) {
		this.renderer = renderer;
	}

	public void render(Gui gui, int x, int y) {
		renderer.accept(gui, x, y);
	}
}