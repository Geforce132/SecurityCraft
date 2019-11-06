package net.geforcemods.securitycraft.screen.components;

import javax.annotation.Nullable;

import net.minecraftforge.fml.client.config.GuiSlider;

public class NamedSlider extends GuiSlider
{
	public int id;
	private String blockName;

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
	{
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(initialString);
		blockName = bN;
		this.id = id;
	}

	public String getBlockName()
	{
		return blockName;
	}
}
