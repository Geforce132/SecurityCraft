package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.widget.button.Button;

public class StringHoverChecker extends HoverChecker
{
	private List<String> lines;
	private final TogglePictureButton button;

	public StringHoverChecker(int top, int bottom, int left, int right, String line)
	{
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public StringHoverChecker(int top, int bottom, int left, int right, List<String> lines)
	{
		super(top, bottom, left, right);
		this.lines = lines;
		button = null;
	}

	public StringHoverChecker(Button button, String line)
	{
		this(button, Arrays.asList(line));
	}

	public StringHoverChecker(Button button, List<String> lines)
	{
		super(button);
		this.lines = lines;
		this.button = button instanceof TogglePictureButton ? (TogglePictureButton)button : null;
	}

	public String getName()
	{
		return lines.get(button == null ? 0 : button.getCurrentIndex());
	}

	public List<String> getLines()
	{
		return lines;
	}
}
