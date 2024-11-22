package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.components.AbstractWidget;

public class HoverChecker {
	private int top;
	private int bottom;
	private int left;
	private int right;
	private AbstractWidget widget;

	public HoverChecker(int top, int bottom, int left, int right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public HoverChecker(AbstractWidget widget) {
		this(widget.y, widget.y + widget.getHeight(), widget.x, widget.x + widget.getWidth());
		this.widget = widget;
	}

	public boolean checkHover(double mouseX, double mouseY) {
		if (widget != null)
			return widget.visible && widget.isHoveredOrFocused();
		else
			return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}

	public AbstractWidget getWidget() {
		return widget;
	}
}
