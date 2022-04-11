package net.geforcemods.securitycraft.screen.components;

import java.util.function.IntFunction;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class ToggleComponentButton extends ExtendedButton implements IToggleableButton {
	private final IntFunction<ITextComponent> onValueChange;
	private int currentIndex = 0;
	private final int toggleCount;

	public ToggleComponentButton(int xPos, int yPos, int width, int height, IntFunction<ITextComponent> onValueChange, int initialValue, int toggleCount, IPressable onPress) {
		super(xPos, yPos, width, height, StringTextComponent.EMPTY, onPress);

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
		setCurrentIndex(Math.floorMod(currentIndex + value, toggleCount));
		onValueChange();
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void setCurrentIndex(int newIndex) {
		currentIndex = Math.floorMod(newIndex, toggleCount);
	}

	public void onValueChange() {
		setMessage(onValueChange.apply(currentIndex));
	}
}
