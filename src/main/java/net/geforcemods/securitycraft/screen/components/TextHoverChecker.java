package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextProperties;

public class TextHoverChecker extends HoverChecker
{
	private List<ITextProperties> lines;

	public TextHoverChecker(int top, int bottom, int left, int right, ITextProperties line)
	{
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public TextHoverChecker(int top, int bottom, int left, int right, List<ITextProperties> lines)
	{
		super(top, bottom, left, right);
		this.lines = lines;
	}

	public TextHoverChecker(Button button, ITextProperties line)
	{
		this(button, Arrays.asList(line));
	}

	public TextHoverChecker(Button button, List<ITextProperties> lines)
	{
		super(button);
		this.lines = lines;
	}

	public ITextProperties getName()
	{
		return lines.get(0);
	}

	public List<ITextProperties> getLines()
	{
		return lines;
	}
}
