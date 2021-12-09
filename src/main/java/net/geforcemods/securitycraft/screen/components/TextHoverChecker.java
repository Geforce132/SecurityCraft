package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TextHoverChecker extends HoverChecker
{
	private List<ITextComponent> lines;
	private final TogglePictureButton button;

	public TextHoverChecker(int top, int bottom, int left, int right, ITextComponent line)
	{
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public TextHoverChecker(int top, int bottom, int left, int right, List<ITextComponent> lines)
	{
		super(top, bottom, left, right);
		this.lines = lines;
		button = null;
	}

	public TextHoverChecker(Widget button, ITextComponent line)
	{
		this(button, Arrays.asList(line));
	}

	public TextHoverChecker(Widget button, List<ITextComponent> lines)
	{
		super(button);
		this.lines = lines;
		this.button = button instanceof TogglePictureButton ? (TogglePictureButton)button : null;
	}

	public ITextComponent getName()
	{
		int i = button == null ? 0 : button.getCurrentIndex();

		if(i >= lines.size())
			return StringTextComponent.EMPTY;

		return lines.get(i);
	}

	public List<ITextComponent> getLines()
	{
		return lines;
	}
}
