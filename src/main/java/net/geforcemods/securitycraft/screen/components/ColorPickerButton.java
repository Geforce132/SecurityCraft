package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ColorPickerButton extends Button {
	public ColorPickerButton(int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress);
	}
}
