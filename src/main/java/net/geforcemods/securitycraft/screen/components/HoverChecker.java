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
		this(widget.getX(), widget.getX() + widget.getHeight(), widget.getY(), widget.getY() + widget.getWidth());

		this.widget = widget;
	}

	public boolean checkHover(double mouseX, double mouseY) {
		if (widget != null) {
			if (!widget.visible)
				return false;
			else
				return widget.isHoveredOrFocused();
		}
		else
			return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}

	public AbstractWidget getWidget() {
		return widget;
	}
}
