package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;
import java.util.function.IntFunction;

public class ToggleComponentButton extends ClickButton implements IToggleableButton {
	private final IntFunction<String> onValueChange;
	private int currentIndex = 0;
	private final int toggleCount;

	public ToggleComponentButton(int id, int xPos, int yPos, int width, int height, IntFunction<String> onValueChange, int initialValue, int toggleCount, Consumer<ClickButton> onPress) {
		super(id, xPos, yPos, width, height, "", button -> {
			onPress.accept(button);
			((ToggleComponentButton) button).cycleIndex(1);
		});

		this.onValueChange = onValueChange;
		this.currentIndex = initialValue;
		this.toggleCount = toggleCount;
		onValueChange();
	}

	public void cycleIndex(int value) {
		setCurrentIndex(Math.floorMod(currentIndex + value, toggleCount));
		onValueChange();
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void setCurrentIndex(int newIndex) {
		currentIndex = newIndex % toggleCount;
		onValueChange();
	}

	public void onValueChange() {
		displayString = onValueChange.apply(currentIndex);
	}
}
