package net.geforcemods.securitycraft.screen.components;

public class HoverChecker {
	private int top;
	private int bottom;
	private int left;
	private int right;

	public HoverChecker(int top, int bottom, int left, int right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public boolean checkHover(double mouseX, double mouseY) {
		return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
	}
}
