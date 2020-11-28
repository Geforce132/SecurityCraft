package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.client.gui.HoverChecker;

public class StringHoverChecker extends HoverChecker
{
	private List<String> lines;
	private final TogglePictureButton button;

	public StringHoverChecker(int top, int bottom, int left, int right, int threshold, String line)
	{
		this(top, bottom, left, right, threshold, Arrays.asList(line));
	}

	public StringHoverChecker(int top, int bottom, int left, int right, int threshold, List<String> lines)
	{
		super(top, bottom, left, right, threshold);
		this.lines = lines;
		button = null;
	}

	public StringHoverChecker(Button button, int threshold, String line)
	{
		this(button, threshold, Arrays.asList(line));
	}

	public StringHoverChecker(Button button, int threshold, List<String> lines)
	{
		super(button, threshold);
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
