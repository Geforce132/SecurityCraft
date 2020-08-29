package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiCustomizeBlock;
import net.geforcemods.securitycraft.gui.components.GuiSlider;
import net.geforcemods.securitycraft.gui.components.GuiSlider.ISlider;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateSliderValue;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A class that allows blocks that have
 * {@link CustomizableSCTE}s to have custom, "per-block"
 * options that are separate from the main SecurityCraft
 * configuration options.
 *
 * @author Geforce
 *
 * @param <T> The Class of the type of value this option should use
 */
public abstract class Option<T> {

	private String name;

	protected T value;
	private T defaultValue;
	private T increment;
	private T minimum;
	private T maximum;

	public Option(String optionName, T value) {
		this.name = optionName;
		this.value = value;
		this.defaultValue = value;
	}

	public Option(String optionName, T value, T min, T max, T increment) {
		this.name = optionName;
		this.value = value;
		this.defaultValue = value;
		this.increment = increment;
		this.minimum = min;
		this.maximum = max;
	}

	/**
	 * Called when this option's button in {@link GuiCustomizeBlock} is pressed.
	 * Update the option's value here. <p>
	 *
	 * NOTE: This gets called on the server side, not on the client!
	 * Use TileEntitySCTE.sync() to update values on the client-side.
	 */
	public abstract void toggle();

	public abstract void readFromNBT(NBTTagCompound tag);

	public abstract void writeToNBT(NBTTagCompound tag);

	public void copy(Option<?> option) {
		value = (T) option.get();
	}

	/**
	 * @return This option's name.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return This option's value.
	 */
	public T get() {
		return value;
	}

	/**
	 * Set this option's new value here.
	 *
	 * @param value The new value
	 */
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * @return This option's default value.
	 */
	public T getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return If this option is some kind of number (integer, float, etc.),
	 *         return the amount the number should increase/decrease every time
	 *         the option is toggled in {@link GuiCustomizeBlock}.
	 */
	public T getIncrement() {
		return increment;
	}

	/**
	 * @return The lowest value this option can be set to.
	 */
	public T getMin() {
		return minimum;
	}

	/**
	 * @return The highest value this option can be set to.
	 */
	public T getMax() {
		return maximum;
	}

	/**
	 * @return Whether this Option should be displayed as a slider
	 */
	public boolean isSlider() {
		return false;
	}

	/**
	 * A subclass of {@link Option}, already setup to handle booleans.
	 */
	public static class OptionBoolean extends Option<Boolean>{

		public OptionBoolean(String optionName, Boolean value) {
			super(optionName, value);
		}

		@Override
		public void toggle() {
			setValue(!get());
		}

		@Override
		public void readFromNBT(NBTTagCompound tag)
		{
			if(tag.hasKey(getName()))
				value = tag.getBoolean(getName());
			else
				value = getDefaultValue();
		}

		@Override
		public void writeToNBT(NBTTagCompound tag)
		{
			tag.setBoolean(getName(), value);
		}

		@Override
		public String toString() {
			return (value) + "";
		}
	}

	/**
	 * A subclass of {@link Option}, already setup to handle integers.
	 */
	public static class OptionInt extends Option<Integer> implements ISlider{
		private boolean slider;
		private CustomizableSCTE tileEntity;

		public OptionInt(String optionName, Integer value) {
			super(optionName, value);
			slider = false;
		}

		public OptionInt(String optionName, Integer value, Integer min, Integer max, Integer increment) {
			super(optionName, value, min, max, increment);
			slider = false;
		}

		public OptionInt(CustomizableSCTE te, String optionName, Integer value, Integer min, Integer max, Integer increment, boolean s) {
			super(optionName, value, min, max, increment);
			slider = s;
			tileEntity = te;
		}

		@Override
		public void toggle() {
			if(isSlider())
				return;

			if(get() >= getMax()) {
				setValue(getMin());
				return;
			}

			if((get() + getIncrement()) >= getMax()) {
				setValue(getMax());
				return;
			}

			setValue(get() + getIncrement());
		}

		@Override
		public void readFromNBT(NBTTagCompound tag)
		{
			if(tag.hasKey(getName()))
				value = tag.getInteger(getName());
			else
				value = getDefaultValue();
		}

		@Override
		public void writeToNBT(NBTTagCompound tag)
		{
			tag.setInteger(getName(), value);
		}

		@Override
		public String toString() {
			return (value) + "";
		}

		@Override
		public boolean isSlider()
		{
			return slider;
		}

		@Override
		public void onChangeSliderValue(GuiSlider slider, String blockName, int id)
		{
			if(!isSlider())
				return;

			setValue((int)slider.getValue());
			slider.displayString = (ClientUtils.localize("option." + blockName + "." + getName()) + " ").replace("#", toString());
		}

		@Override
		public void onMouseRelease(int id)
		{
			SecurityCraft.network.sendToServer(new PacketSUpdateSliderValue(tileEntity.getPos(), id, get()));
		}
	}

	/**
	 * A subclass of {@link Option}, already setup to handle doubles.
	 */
	public static class OptionDouble extends Option<Double> implements ISlider{
		private boolean slider;
		private CustomizableSCTE tileEntity;

		public OptionDouble(String optionName, Double value) {
			super(optionName, value);
			slider = false;
		}

		public OptionDouble(String optionName, Double value, Double min, Double max, Double increment) {
			super(optionName, value, min, max, increment);
			slider = false;
		}

		public OptionDouble(CustomizableSCTE te, String optionName, Double value, Double min, Double max, Double increment, boolean s) {
			super(optionName, value, min, max, increment);
			slider = s;
			tileEntity = te;
		}

		@Override
		public void toggle() {
			if(isSlider())
				return;

			if(get() >= getMax()) {
				setValue(getMin());
				return;
			}

			if((get() + getIncrement()) >= getMax()) {
				setValue(getMax());
				return;
			}

			setValue(get() + getIncrement());
		}

		@Override
		public void readFromNBT(NBTTagCompound tag)
		{
			if(tag.hasKey(getName()))
				value = tag.getDouble(getName());
			else
				value = getDefaultValue();
		}

		@Override
		public void writeToNBT(NBTTagCompound tag)
		{
			tag.setDouble(getName(), value);
		}

		@Override
		public String toString() {
			return Double.toString(value).length() > 5 ? Double.toString(value).substring(0, 5) : Double.toString(value);
		}

		@Override
		public boolean isSlider()
		{
			return slider;
		}

		@Override
		public void onChangeSliderValue(GuiSlider slider, String blockName, int id)
		{
			if(!isSlider())
				return;

			setValue(slider.getValue());
			slider.displayString = (ClientUtils.localize("option." + blockName + "." + getName()) + " ").replace("#", toString());
		}

		@Override
		public void onMouseRelease(int id)
		{
			SecurityCraft.network.sendToServer(new PacketSUpdateSliderValue(tileEntity.getPos(), id, get()));
		}
	}

	/**
	 * A subclass of {@link Option}, already setup to handle floats.
	 */
	public static class OptionFloat extends Option<Float>{

		public OptionFloat(String optionName, Float value) {
			super(optionName, value);
		}

		public OptionFloat(String optionName, Float value, Float min, Float max, Float increment) {
			super(optionName, value, min, max, increment);
		}

		@Override
		public void toggle() {
			if(get() >= getMax()) {
				setValue(getMin());
				return;
			}

			if((get() + getIncrement()) >= getMax()) {
				setValue(getMax());
				return;
			}

			setValue(get() + getIncrement());
		}

		@Override
		public void readFromNBT(NBTTagCompound tag)
		{
			if(tag.hasKey(getName()))
				value = tag.getFloat(getName());
			else
				value = getDefaultValue();
		}

		@Override
		public void writeToNBT(NBTTagCompound tag)
		{
			tag.setFloat(getName(), value);
		}

		@Override
		public String toString() {
			return Float.toString(value).length() > 5 ? Float.toString(value).substring(0, 5) : Float.toString(value);
		}

	}

}
