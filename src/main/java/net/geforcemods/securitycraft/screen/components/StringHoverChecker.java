package net.geforcemods.securitycraft.screen.components;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;

public class StringHoverChecker extends HoverChecker {
	private List<String> lines;
	private final IToggleableButton button;
	private boolean singleTooltip = false;

	public StringHoverChecker(int top, int bottom, int left, int right, String line) {
		this(top, bottom, left, right, Arrays.asList(line));
	}

	public StringHoverChecker(int top, int bottom, int left, int right, List<String> lines) {
		super(top, bottom, left, right);
		this.lines = lines;
		button = null;
	}

	public StringHoverChecker(GuiButton button, String line) {
		this(button, Arrays.asList(line));
	}

	public StringHoverChecker(GuiButton button, List<String> lines) {
		super(button);
		this.lines = lines;
		this.button = button instanceof IToggleableButton ? (IToggleableButton) button : null;
	}

	public void singleTooltip() {
		singleTooltip = true;
	}

	public boolean isSingleTooltip() {
		return singleTooltip;
	}

	public String getName() {
		int i = button == null ? 0 : button.getCurrentIndex();

		if (i >= lines.size())
			return lines.get(Math.min(i, lines.size() - 1));

		return lines.get(i);
	}

	public List<String> getLines() {
		return lines;
	}
}
