package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.ITooltip;
import net.minecraft.util.text.ITextComponent;

// TODO: Use this for more classes to be more in line with how tooltips work in 1.19.4+
public class Tooltip implements ITooltip {
	protected final Screen screen;
	protected final FontRenderer font;
	protected final ITextComponent text;

	public Tooltip(Screen screen, FontRenderer font, ITextComponent text) {
		this.screen = screen;
		this.font = font;
		this.text = text;
	}

	@Override
	public void onTooltip(Button button, MatrixStack poseStack, int mouseX, int mouseY) {
		screen.renderTooltip(poseStack, font.split(text, 150), mouseX, mouseY);
	}

	public Screen screen() {
		return screen;
	}

	public FontRenderer font() {
		return font;
	}

	public ITextComponent text() {
		return text;
	}
}