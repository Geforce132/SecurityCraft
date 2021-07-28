package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.fmlclient.gui.widget.Slider;

public class HoverChecker
{
	private int top;
	private int bottom;
	private int left;
	private int right;
	private AbstractWidget widget;

	public HoverChecker(int top, int bottom, int left, int right)
	{
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public HoverChecker(AbstractWidget widget)
	{
		this(widget.x, widget.x + widget.getHeight(), widget.y, widget.y + widget.getWidth());

		this.widget = widget;
	}

	public boolean checkHover(int mouseX, int mouseY)
	{
		if(widget != null)
		{
			if(!widget.visible || (widget instanceof Slider slider && slider.dragging))
				return false;
			else return widget.isHovered();
		}
		else return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}
}
