package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class HintEditBox extends GuiTextField {
	private String hint;
	private FontRenderer font;

	public HintEditBox(int id, FontRenderer font, int x, int y, int width, int height) {
		super(id, font, x, y, width, height);
		this.font = font;
	}

	@Override
	public void drawTextBox() {
		super.drawTextBox();

		if (getVisible() && hint != null && text.isEmpty() && !isFocused())
			font.drawStringWithShadow(hint, x + 4, y + (height - 8) / 2, 0xE0E0E0);
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
}
