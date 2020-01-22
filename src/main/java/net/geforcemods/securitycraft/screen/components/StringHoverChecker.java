package net.geforcemods.securitycraft.screen.components;

import net.minecraftforge.fml.client.gui.HoverChecker;

public class StringHoverChecker extends HoverChecker
{
	private String name;

	public StringHoverChecker(int top, int bottom, int left, int right, int threshold, String name)
	{
		super(top, bottom, left, right, threshold);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
