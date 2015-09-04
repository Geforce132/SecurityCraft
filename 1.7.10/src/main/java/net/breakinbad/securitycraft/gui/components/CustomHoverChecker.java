package net.breakinbad.securitycraft.gui.components;

import cpw.mods.fml.client.config.HoverChecker;
import net.minecraft.client.gui.GuiButton;

public class CustomHoverChecker extends HoverChecker {
	
	private int xPos = 0, yPos = 0;

	public CustomHoverChecker(int top, int bottom, int left, int right, int threshold) {
		super(top, bottom, left, right, threshold);
		this.xPos = left;
		this.yPos = top;
	}
	
	public CustomHoverChecker(GuiButton button, int threshold) {
		super(button, threshold);
	}
	
	public int getX(){
		return xPos;
	}
	
	public int getY(){
		return yPos;
	}

}
