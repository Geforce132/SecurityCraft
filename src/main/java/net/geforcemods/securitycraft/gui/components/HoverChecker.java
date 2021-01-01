package net.geforcemods.securitycraft.gui.components;

import net.minecraft.client.gui.GuiButton;

public class HoverChecker
{
	private int top;
	private int bottom;
	private int left;
	private int right;
	private GuiButton button;

	public HoverChecker(int top, int bottom, int left, int right)
	{
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public HoverChecker(GuiButton button)
	{
		this(button.x, button.x + button.height, button.y, button.y + button.width);

		this.button = button;
	}

	public boolean checkHover(int mouseX, int mouseY)
	{
		if(button != null)
		{
			if(!button.visible || (button instanceof GuiSlider && ((GuiSlider)button).dragging))
				return false;
			else return mouseX >= button.x && mouseX <= button.x + button.width && mouseY >= button.y && mouseY <= button.y + button.height;
		}
		else return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}
}
