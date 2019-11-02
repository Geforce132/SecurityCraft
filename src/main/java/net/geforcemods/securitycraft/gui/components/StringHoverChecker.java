package net.geforcemods.securitycraft.gui.components;

import net.minecraftforge.fml.client.config.HoverChecker;

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
