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
		this(widget.field_230690_l_, widget.field_230690_l_ + widget.getHeight(), widget.field_230691_m_, widget.field_230691_m_ + widget.func_230998_h_()); //x, y, getWidth

		this.widget = widget;
	}

	public boolean checkHover(int mouseX, int mouseY)
	{
		if(widget != null && !widget.field_230694_p_) //visible
			return false;

		return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}
}
