package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.network.chat.Component;

public class TextHoverChecker extends HoverChecker {
	private List<Component> lines;

	public TextHoverChecker(int top, int bottom, int left, int right, Component line) {
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public TextHoverChecker(int top, int bottom, int left, int right, List<? extends Component> lines) {
		super(top, bottom, left, right);
		this.lines = lines.stream().map(Component.class::cast).toList();
	}

	public Component getName() {
		return lines.get(0);
	}

	public List<Component> getLines() {
		return lines;
	}
}
