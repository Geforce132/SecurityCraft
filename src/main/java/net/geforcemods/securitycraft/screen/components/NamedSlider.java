package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraftforge.fml.client.gui.widget.Slider;

public class NamedSlider extends Slider
{
	public int id;
	private String blockName;
	private Consumer<NamedSlider> consumer;

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, int minVal, int maxVal, int currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
	{
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(initialString);
		blockName = bN;
		this.id = id;
	}

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
	{
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(initialString);
		blockName = bN;
		this.id = id;
	}
	
	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, int minVal, int maxVal, int currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par, Consumer<NamedSlider> method)
	{
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(initialString);
		blockName = bN;
		this.id = id;
		consumer = method;
	}
	
	@Override
    public void onRelease(double mouseX, double mouseY)
    {
        super.onRelease(mouseX, mouseY);
        
        if(consumer != null)
        	consumer.accept(this);
    }

	public String getBlockName()
	{
		return blockName;
	}
}
