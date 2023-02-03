package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.Minecraft;

public class ColorChooserButton extends ClickButton {
	private final ColorChooser colorChooser;

	public ColorChooserButton(int id, int x, int y, int width, int height, ColorChooser colorChooser) {
		super(id, x, y, width, height, "", button -> colorChooser.disabled = !colorChooser.disabled);

		this.colorChooser = colorChooser;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		int color = colorChooser.getRGBColor();

		super.drawButton(mc, mouseX, mouseY, partialTicks);
		drawGradientRect(x + 2, y + 2, x + width - 2, y + height - 2, color, color);
	}
}
