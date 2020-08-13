package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class TextHoverChecker extends HoverChecker
{
	private List<ITextComponent> lines;

	public TextHoverChecker(int top, int bottom, int left, int right, ITextComponent line)
	{
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public TextHoverChecker(int top, int bottom, int left, int right, List<ITextComponent> lines)
	{
		super(top, bottom, left, right);
		this.lines = lines;
	}

	public TextHoverChecker(Button button, ITextComponent line)
	{
		this(button, Arrays.asList(line));
	}

	public TextHoverChecker(Button button, List<ITextComponent> lines)
	{
		super(button);
		this.lines = lines;
	}

	public ITextComponent getName()
	{
		return lines.get(0);
	}

	public List<ITextComponent> getLines()
	{
		return lines;
	}
}
