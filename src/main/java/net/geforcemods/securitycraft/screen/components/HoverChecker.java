package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class HoverChecker {
	private int top;
	private int bottom;
	private int left;
	private int right;
	private Widget widget;

	public HoverChecker(int top, int bottom, int left, int right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public HoverChecker(Widget widget) {
		this(widget.y, widget.y + widget.getHeight(), widget.x, widget.x + widget.getWidth());
		this.widget = widget;
	}

	public boolean checkHover(double mouseX, double mouseY) {
		if (widget != null)
			return widget.visible && !(widget instanceof Slider && ((Slider) widget).dragging) && widget.isHovered();
		else
			return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}
}
