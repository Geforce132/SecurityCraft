package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;

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
		this(widget.x, widget.x + widget.getHeight(), widget.y, widget.y + widget.getWidth());

		this.widget = widget;
	}

	public boolean checkHover(double mouseX, double mouseY, GuiEventListener currentlyFocused) {
		if (widget != null) {
			if (!widget.visible)
				return false;
			else
				return widget.isHoveredOrFocused() && !(widget.isHovered && widget != currentlyFocused);
		}
		else
			return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}

	public AbstractWidget getWidget() {
		return widget;
	}
}
