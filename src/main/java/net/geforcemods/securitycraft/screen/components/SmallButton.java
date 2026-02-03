package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class SmallButton extends Button {
	private SmallButton(int xPos, int yPos, Component text, OnPress handler) {
		super(xPos, yPos, 8, 8, text, handler, DEFAULT_NARRATION);
	}

	public static SmallButton create(int xPos, int yPos, Component text, OnPress handler) {
		return new SmallButton(xPos, yPos, text, handler);
	}

	public static SmallButton createWithX(int xPos, int yPos, OnPress handler) {
		return new SmallButton(xPos, yPos, Component.literal("x"), handler);
	}

	@Override
	public void renderString(GuiGraphics guiGraphics, Font font, int color) {
		guiGraphics.drawCenteredString(font, Language.getInstance().getVisualOrder(getMessage()), getX() + width / 2, getY() + (height - 8) / 2, color);
	}
}
