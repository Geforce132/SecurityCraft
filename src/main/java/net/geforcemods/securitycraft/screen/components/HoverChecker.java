package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class HoverChecker {
	private int top;
	private int bottom;
	private int left;
	private int right;
	private GuiButton button;

	public HoverChecker(int top, int bottom, int left, int right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public HoverChecker(GuiButton button) {
		this(button.y, button.y + button.height, button.x, button.x + button.width);
		this.button = button;
	}

	public HoverChecker(GuiTextField textField) {
		this(textField.y, textField.y + textField.height, textField.x, textField.x + textField.width);
	}

	public boolean checkHover(int mouseX, int mouseY) {
		if (button != null && (!button.visible || (button instanceof Slider && ((Slider) button).isDragging())))
			return false;

		return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}
}
