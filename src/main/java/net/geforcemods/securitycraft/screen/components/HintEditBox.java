package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

public class HintEditBox extends TextFieldWidget {
	private ITextComponent hint;
	private FontRenderer font;

	public HintEditBox(FontRenderer font, int x, int y, int width, int height, ITextComponent message) {
		super(font, x, y, width, height, message);
		this.font = font;
	}

	@Override
	public void renderButton(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
		super.renderButton(pose, mouseX, mouseY, partialTick);

		if (isVisible() && hint != null && value.isEmpty() && !isFocused())
			font.drawShadow(pose, hint, x + 4, y + (height - 8) / 2, 0xE0E0E0);
	}

	public void setHint(ITextComponent hint) {
		this.hint = hint;
	}
}
