package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ColorChooserButton extends Button {
	private final ColorChooser colorChooser;

	public ColorChooserButton(int x, int y, int width, int height, ColorChooser colorChooser) {
		super(x, y, width, height, Component.empty(), button -> colorChooser.disabled = !colorChooser.disabled);

		this.colorChooser = colorChooser;
	}

	@Override
	public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		int color = colorChooser.getRGBColor();

		super.renderButton(pose, mouseX, mouseY, partialTick);
		fillGradient(pose, getX() + 2, getY() + 2, getX() + width - 2, getY() + height - 2, color, color, getBlitOffset());
	}
}
