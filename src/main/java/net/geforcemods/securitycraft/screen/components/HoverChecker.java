package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.widget.Widget;

public class HoverChecker
{
	private int top;
	private int bottom;
	private int left;
	private int right;
	private Widget widget;

	public HoverChecker(int top, int bottom, int left, int right)
	{
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public HoverChecker(Widget widget)
	{
		this(widget.x, widget.x + widget.getHeightRealms(), widget.y, widget.y + widget.getWidth());

		this.widget = widget;
	}

	public boolean checkHover(int mouseX, int mouseY)
	{
		if(widget != null)
		{
			if(!widget.visible)
				return false;
			else return widget.isHovered();
		}
		else return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}
}
