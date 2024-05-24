/*
 * Minecraft Forge Copyright (c) 2016. This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software Foundation version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package net.geforcemods.securitycraft.screen.components;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;

/**
 * This class is blatantly stolen from iChunUtils with permission. and blatantly edited by bl4ckscor3 to fit SC's needs
 *
 * @author iChun
 */
public class Slider extends GuiButtonExt {
	/** The value of this slider control. */
	private double sliderValue;
	/** Is this slider control being dragged. */
	private boolean dragging = false;
	private double minValue = 0.0D;
	private double maxValue = 5.0D;
	@Nullable
	private ISlider parent = null;
	private boolean drawString = true;
	private String denotation;
	private String prefix;

	public Slider(String initialString, String denotation, int id, int xPos, int yPos, int width, int height, String prefix, double minVal, double maxVal, double currentVal, boolean drawStr, @Nullable ISlider par) {
		super(id, xPos, yPos, width, height, prefix);
		setMinValue(minVal);
		setMaxValue(maxVal);
		parent = par;
		this.denotation = denotation;
		setSliderValue((currentVal - minVal) / (maxVal - minVal));
		this.prefix = prefix;
		displayString = initialString;
		drawString = drawStr;

		if (!drawString)
			displayString = "";
	}

	@Override
	public int getHoverState(boolean par1) {
		return 0;
	}

	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			if (isDragging()) {
				setSliderValue((mouseX - (x + 4)) / (double) (width - 8));
				updateSlider();
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(x + (int) (getSliderValue() * (width - 8)), y, 0, 66, 4, 20);
			drawTexturedModalRect(x + (int) (getSliderValue() * (width - 8)) + 4, y, 196, 66, 4, 20);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (super.mousePressed(mc, mouseX, mouseY)) {
			setSliderValue((double) (mouseX - (x + 4)) / (double) (width - 8));
			updateSlider();
			setDragging(true);
			return true;
		}
		else
			return false;
	}

	public void updateSlider() {
		if (getSliderValue() < 0.0F)
			setSliderValue(0.0F);

		if (getSliderValue() > 1.0F)
			setSliderValue(1.0F);

		if (parent != null)
			parent.onChangeSliderValue(this, denotation, id);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		setDragging(false);

		if (parent != null)
			parent.onMouseRelease(id);
	}

	public int getValueInt() {
		return (int) Math.round(getValue());
	}

	public double getValue() {
		return getSliderValue() * (maxValue - minValue) + minValue;
	}

	public void setValue(double newValue) {
		setSliderValue((newValue - minValue) / (maxValue - minValue));
	}

	public boolean isDragging() {
		return dragging;
	}

	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public double getSliderValue() {
		return sliderValue;
	}

	public void setSliderValue(double sliderValue) {
		this.sliderValue = sliderValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public String getPrefix() {
		return prefix;
	}

	public static interface ISlider {
		void onChangeSliderValue(Slider slider, String denotation, int id);

		void onMouseRelease(int id);
	}
}
