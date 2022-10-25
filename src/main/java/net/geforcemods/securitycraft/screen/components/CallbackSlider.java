package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class CallbackSlider extends ForgeSlider {
	private final Consumer<CallbackSlider> onApplyValue;

	public CallbackSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString, Consumer<CallbackSlider> onApplyValue) {
		this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1.0D, 0, drawString, onApplyValue);
	}

	public CallbackSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, Consumer<CallbackSlider> onApplyValue) {
		super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
		this.onApplyValue = onApplyValue;
	}

	@Override
	protected void applyValue() {
		super.applyValue();

		if (onApplyValue != null)
			onApplyValue.accept(this);
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
}
