package net.geforcemods.securitycraft.gui.components;

import java.util.function.Consumer;
import java.util.function.IntFunction;

public class ToggleComponentButton extends ClickButton {
	private final IntFunction<String> onValueChange;
	private int currentIndex = 0;
	private final int toggleCount;

	public ToggleComponentButton(int id, int xPos, int yPos, int width, int height, IntFunction<String> onValueChange, int initialValue, int toggleCount, Consumer<ClickButton> onPress) {
		super(id, xPos, yPos, width, height, "", onPress);

		this.onValueChange = onValueChange;
		this.currentIndex = initialValue;
		this.toggleCount = toggleCount;
		onValueChange();
	}

	public void cycleIndex(int value) {
		currentIndex = Math.floorMod(currentIndex + value, toggleCount);
		onValueChange();
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void onValueChange() {
		displayString = onValueChange.apply(currentIndex);
	}
}
