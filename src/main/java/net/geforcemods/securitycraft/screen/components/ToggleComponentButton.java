package net.geforcemods.securitycraft.screen.components;

import java.util.function.IntFunction;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class ToggleComponentButton extends ExtendedButton {
	private final IntFunction<String> onValueChange;
	private int currentIndex = 0;
	private final int toggleCount;

	public ToggleComponentButton(int xPos, int yPos, int width, int height, IntFunction<String> onValueChange, int initialValue, int toggleCount, IPressable onPress) {
		super(xPos, yPos, width, height, "", onPress);

		this.onValueChange = onValueChange;
		this.currentIndex = initialValue;
		this.toggleCount = toggleCount;
		onValueChange();
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (Screen.hasShiftDown())
			cycleIndex(-1);
		else
			cycleIndex(1);

		super.onClick(mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (clicked(mouseX, mouseY)) {
			cycleIndex(-(int) Math.signum(delta));
			onPress();
			return true;
		}

		return false;
	}

	public void cycleIndex(int value) {
		currentIndex = Math.floorMod(currentIndex + value, toggleCount);
		onValueChange();
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void onValueChange() {
		setMessage(onValueChange.apply(currentIndex));
	}
}
