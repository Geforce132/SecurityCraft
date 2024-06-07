package net.geforcemods.securitycraft.screen.components;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public class TextHoverChecker extends HoverChecker {
	private List<ITextComponent> lines;
	private final IToggleableButton button;

	public TextHoverChecker(int top, int bottom, int left, int right, ITextComponent line) {
		this(top, bottom, left, right, Collections.singletonList(line));
	}

	public TextHoverChecker(int top, int bottom, int left, int right, List<ITextComponent> lines) {
		super(top, bottom, left, right);
		this.lines = lines;
		button = null;
	}

	public TextHoverChecker(Widget widget, ITextComponent line) {
		this(widget, Collections.singletonList(line));
	}

	public TextHoverChecker(Widget widget, List<ITextComponent> lines) {
		super(widget);
		this.lines = lines;
		this.button = widget instanceof IToggleableButton ? (IToggleableButton) widget : null;
	}

	public ITextComponent getName() {
		int i = button == null ? 0 : button.getCurrentIndex();

		if (i >= lines.size())
			return lines.get(Math.min(i, lines.size() - 1));

		return lines.get(i);
	}

	public List<ITextComponent> getLines() {
		return lines;
	}
}
