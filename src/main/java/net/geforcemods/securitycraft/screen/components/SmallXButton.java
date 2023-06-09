package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class SmallXButton extends Button {
	public SmallXButton(int xPos, int yPos, OnPress handler) {
		super(xPos, yPos, 8, 8, Component.literal("x"), handler, DEFAULT_NARRATION);
	}

	@Override
	public void renderString(GuiGraphics guiGraphics, Font font, int color) {
		guiGraphics.drawCenteredString(font, Language.getInstance().getVisualOrder(getMessage()), getX() + width / 2, getY() + (height - 8) / 2, color);
	}
}
