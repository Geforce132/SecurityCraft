package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;
import java.util.function.IntFunction;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ToggleComponentButton extends IdButton {
	private final IntFunction<Component> componentFunction;
	private int currentIndex = 0;
	private final int toggleCount;

	public ToggleComponentButton(int id, int xPos, int yPos, int width, int height, IntFunction<Component> componentFunction, int initialValue, int toggleCount, Consumer<IdButton> onClick) {
		super(id, xPos, yPos, width, height, "", onClick);

		this.componentFunction = componentFunction;
		this.currentIndex = initialValue;
		this.toggleCount = toggleCount;

		updateComponent();
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if(Screen.hasShiftDown())
			cycleIndex(-1);
		else
			cycleIndex(1);

		super.onClick(mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		cycleIndex(-(int)Math.signum(delta));
		onPress.onPress(this);
		return true;
	}

	public void cycleIndex(int value) {
		currentIndex = Math.floorMod(currentIndex + value, toggleCount);
		updateComponent();
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	private void updateComponent() {
		setMessage(componentFunction.apply(currentIndex));
	}
}
