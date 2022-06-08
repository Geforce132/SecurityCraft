package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public class TextHoverChecker extends HoverChecker {
	private List<Component> lines;
	private final IToggleableButton button;

	public TextHoverChecker(int top, int bottom, int left, int right, Component line) {
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public TextHoverChecker(int top, int bottom, int left, int right, List<? extends Component> lines) {
		super(top, bottom, left, right);
		this.lines = lines.stream().map(component -> (Component) component).toList();
		button = null;
	}

	public TextHoverChecker(AbstractWidget widget, Component line) {
		this(widget, Arrays.asList(line));
	}

	public TextHoverChecker(AbstractWidget widget, List<? extends Component> lines) {
		super(widget);
		this.lines = lines.stream().map(component -> (Component) component).toList();
		this.button = widget instanceof IToggleableButton tb ? tb : null;
	}

	public Component getName() {
		int i = button == null ? 0 : button.getCurrentIndex();

		if (i >= lines.size())
			return Component.empty();

		return lines.get(i);
	}

	public List<Component> getLines() {
		return lines;
	}
}
