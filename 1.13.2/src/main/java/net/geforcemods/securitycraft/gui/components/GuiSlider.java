/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.geforcemods.securitycraft.gui.components;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;

/**
 * This class is blatantly stolen from iChunUtils with permission.
 *
 * and blatantly edited by bl4ckscor3 to fit SC's needs
 *
 * @author iChun
 */
public class GuiSlider extends GuiButtonExt
{
	/** The value of this slider control. */
	public double sliderValue;

	public String dispString = "";

	/** Is this slider control being dragged. */
	public boolean dragging = false;
	public boolean showDecimal = true;

	public double minValue = 0.0D;
	public double maxValue = 5.0D;
	public int precision = 1;

	@Nullable
	public ISlider parent = null;

	public String suffix = "";

	public boolean drawString = true;

	private String blockName;

	public GuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr)
	{
		this("whyareyoudoingthis", "seriouslywhy", id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, null);
	}

	public GuiSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
	{
		super(id, xPos, yPos, width, height, prefix);
		minValue = minVal;
		maxValue = maxVal;
		dispString = prefix;
		parent = par;
		suffix = suf;
		showDecimal = showDec;
		blockName = bN;
		String val;
		sliderValue = (currentVal - minVal) / (maxVal - minVal);

		if (showDecimal)
		{
			val = Double.toString(getValue());
			precision = Math.min(val.substring(val.indexOf(".") + 1).length(), 4);
		}
		else
		{
			val = Integer.toString(getValueInt());
			precision = 0;
		}

		displayString = initialString;

		drawString = drawStr;
		if(!drawString)
			displayString = "";
	}

	public GuiSlider(int id, int xPos, int yPos, String displayStr, double minVal, double maxVal, double currentVal, ISlider par)
	{
		this("whyareyoudoingthis", "seriouslywhy", id, xPos, yPos, 150, 20, displayStr, "", minVal, maxVal, currentVal, true, true, par);
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
	 * this button.
	 */
	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
	 * this button.
	 */
	@Override
	public int getHoverState(boolean par1)
	{
		return 0;
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
	 */
	/**
	 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
	 */
	@Override
	protected void onDrag(double mouseX, double mouseY, double mouseDX, double mouseDY)
	{
		if (visible)
		{
			if (dragging)
			{
				sliderValue = (mouseX - (x + 4)) / (width - 8);
				updateSlider();
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(x + (int)(sliderValue * (width - 8)), y, 0, 66, 4, 20);
			this.drawTexturedModalRect(x + (int)(sliderValue * (width - 8)) + 4, y, 196, 66, 4, 20);
		}
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	@Override
	public void onClick(double mouseX, double mouseY)
	{
		sliderValue = (mouseX - (x + 4)) / (width - 8);
		updateSlider();
		dragging = true;
	}

	public void updateSlider()
	{
		if (sliderValue < 0.0F)
			sliderValue = 0.0F;

		if (sliderValue > 1.0F)
			sliderValue = 1.0F;

		String val;

		if (showDecimal)
		{
			val = Double.toString(getValue());

			if (val.substring(val.indexOf(".") + 1).length() > precision)
			{
				val = val.substring(0, val.indexOf(".") + precision + 1);

				if (val.endsWith("."))
					val = val.substring(0, val.indexOf(".") + precision);
			}
			else
				while (val.substring(val.indexOf(".") + 1).length() < precision)
					val = val + "0";
		}
		else
			val = Integer.toString(getValueInt());

		if (parent != null)
			parent.onChangeSliderValue(this, blockName, id);
	}

	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void onRelease(double mouseX, double mouseY)
	{
		dragging = false;
	}

	public int getValueInt()
	{
		return (int)Math.round(getValue());
	}

	public double getValue()
	{
		return sliderValue * (maxValue - minValue) + minValue;
	}

	public void setValue(double d)
	{
		sliderValue = (d - minValue) / (maxValue - minValue);
	}

	public static interface ISlider
	{
		void onChangeSliderValue(GuiSlider slider, String blockName, int id);
	}
}
