package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public class TextHoverChecker extends HoverChecker
{
	private List<Component> lines;
	private final TogglePictureButton button;

	public TextHoverChecker(int top, int bottom, int left, int right, Component line)
	{
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public TextHoverChecker(int top, int bottom, int left, int right, List<Component> lines)
	{
		super(top, bottom, left, right);
		this.lines = lines;
		button = null;
	}

	public TextHoverChecker(AbstractWidget button, Component line)
	{
		this(button, Arrays.asList(line));
	}

	public TextHoverChecker(AbstractWidget button, List<Component> lines)
	{
		super(button);
		this.lines = lines;
		this.button = button instanceof TogglePictureButton tpb ? tpb : null;
	}

	public Component getName()
	{
		return lines.get(button == null ? 0 : button.getCurrentIndex());
	}

	public List<Component> getLines()
	{
		return lines;
	}
}
