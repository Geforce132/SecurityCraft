package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

// TODO: Use this for more classes to be more in line with how tooltips work in 1.19.4+
public class Tooltip {
	protected final GuiScreen screen;
	protected final FontRenderer font;
	protected final String text;

	public Tooltip(GuiScreen screen, FontRenderer font, ITextComponent text) {
		this.screen = screen;
		this.font = font;
		this.text = text.getFormattedText();
	}

	public void render(GuiButton button, int mouseX, int mouseY) {
		screen.drawHoveringText(font.listFormattedStringToWidth(text, 150), mouseX, mouseY);
		GlStateManager.disableLighting();
	}

	public GuiScreen screen() {
		return screen;
	}

	public FontRenderer font() {
		return font;
	}

	public String text() {
		return text;
	}
}