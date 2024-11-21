package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class HintEditBox extends EditBox {
	private Component hint;
	private Font font;

	public HintEditBox(Font font, int x, int y, int width, int height, Component message) {
		super(font, x, y, width, height, message);
		this.font = font;
	}

	@Override
	public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		super.renderButton(pose, mouseX, mouseY, partialTick);

		if (isVisible() && hint != null && value.isEmpty() && !isFocused())
			font.drawShadow(pose, hint, x + 4, y + (height - 8) / 2, 0xE0E0E0);
	}

	public void setHint(Component hint) {
		this.hint = hint;
	}
}
