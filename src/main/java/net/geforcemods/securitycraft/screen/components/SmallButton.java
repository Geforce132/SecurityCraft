package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class SmallButton extends Button.Plain {
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
	public void extractDefaultLabel(ActiveTextCollector textCollector) {
		textCollector.accept(TextAlignment.CENTER, getX() + width / 2, getY() + (height - 8) / 2, getMessage().getVisualOrderText());
	}
}
