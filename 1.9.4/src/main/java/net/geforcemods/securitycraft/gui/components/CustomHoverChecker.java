package net.geforcemods.securitycraft.gui.components;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.HoverChecker;

public class CustomHoverChecker extends HoverChecker {
	
	private int xPos = 0, yPos = 0;
	private String name;
	
	public CustomHoverChecker(int top, int bottom, int left, int right, int threshold, String name) {
		super(top, bottom, left, right, threshold);
		this.xPos = left;
		this.yPos = top;
		this.name = name;
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

	public String getName()
	{
		return name;
	}
}
