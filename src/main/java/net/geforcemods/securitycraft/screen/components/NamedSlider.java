package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class NamedSlider extends Slider
{
	public int id;
	private String blockName;
	private Consumer<NamedSlider> consumer;

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, int minVal, int maxVal, int currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
	{
		super(xPos, yPos, width, height, new StringTextComponent(prefix), new StringTextComponent(suf), minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		func_238482_a_(new StringTextComponent(initialString));
		blockName = bN;
		this.id = id;
	}

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
	{
		super(xPos, yPos, width, height, new StringTextComponent(prefix), new StringTextComponent(suf), minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		func_238482_a_(new StringTextComponent(initialString));
		blockName = bN;
		this.id = id;
	}

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, int minVal, int maxVal, int currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par, Consumer<NamedSlider> method)
	{
		super(xPos, yPos, width, height, new StringTextComponent(prefix), new StringTextComponent(suf), minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		func_238482_a_(new StringTextComponent(initialString));
		blockName = bN;
		this.id = id;
		consumer = method;
	}

	@Override
	public void func_231000_a__(double mouseX, double mouseY)
	{
		super.func_231000_a__(mouseX, mouseY);

		if(consumer != null)
			consumer.accept(this);
	}

	public String getBlockName()
	{
		return blockName;
	}
}
