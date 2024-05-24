package net.geforcemods.securitycraft.api;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.minecraftforge.fml.client.gui.widget.Slider.ISlider;

/**
 * A class that allows blocks that have {@link CustomizableBlockEntity}s to have custom, "per-block" options that are
 * separate from the main SecurityCraft configuration options.
 *
 * @author Geforce
 * @param <T> The Class of the type of value this option should use
 */
public abstract class Option<T> {
	private String name;
	protected T value;
	private T defaultValue;
	private T increment;
	private T minimum;
	private T maximum;

	protected Option(String optionName, T value) {
		this.name = optionName;
		this.value = value;
		this.defaultValue = value;
	}

	protected Option(String optionName, T value, T min, T max, T increment) {
		this.name = optionName;
		this.value = value;
		this.defaultValue = value;
		this.increment = increment;
		this.minimum = min;
		this.maximum = max;
	}

	/**
	 * Called when this option's button in {@link CustomizeBlockScreen} is pressed. Update the option's value here. <p> NOTE:
	 * This gets called on the server side, not on the client! Use TileEntitySCTE.sync() to update values on the client-side.
	 */
	public abstract void toggle();

	public abstract void load(CompoundNBT tag);

	public abstract void save(CompoundNBT tag, T value);

	public void save(CompoundNBT tag) {
		save(tag, value);
	}

	public void copy(Option<?> option) {
		value = (T) option.get();
	}

	/**
	 * @return This option's name.
	 */
	public String getName() {
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
	 * @return If this option is some kind of number (integer, float, etc.), return the amount the number should
	 *         increase/decrease every time the option is toggled in {@link CustomizeBlockScreen}.
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
	 * @param denotation The denotation to use for the option key, usually the block's name
	 * @return The language key for this option
	 */
	public String getKey(String denotation) {
		return "option." + denotation + "." + getName();
	}

	/**
	 * @param denotation The denotation to use for the option key, usually the block's name
	 * @return The language key for the description of this option
	 */
	public String getDescriptionKey(String denotation) {
		return getKey(denotation) + ".description";
	}

	/**
	 * @return A component containing information about the default value and min/max range of this option
	 */
	public ITextComponent getDefaultInfo() {
		return Utils.localize("securitycraft.option.default_with_range", getDefaultValue(), getMin(), getMax()).withStyle(TextFormatting.GRAY);
	}

	/**
	 * @return A textual representation of this option's value
	 */
	public ITextComponent getValueText() {
		return new StringTextComponent(toString());
	}

	@Override
	public String toString() {
		return (value) + "";
	}

	/**
	 * A subclass of {@link Option} set up to handle booleans.
	 */
	public static class BooleanOption extends Option<Boolean> {
		public BooleanOption(String optionName, Boolean value) {
			super(optionName, value);
		}

		@Override
		public void toggle() {
			setValue(!get());
		}

		@Override
		public void load(CompoundNBT tag) {
			if (tag.contains(getName()))
				value = tag.getBoolean(getName());
			else
				value = getDefaultValue();
		}

		@Override
		public void save(CompoundNBT tag, Boolean value) {
			tag.putBoolean(getName(), value);
		}

		@Override
		public ITextComponent getDefaultInfo() {
			return Utils.localize("securitycraft.option.default", Utils.localize(getDefaultValue() ? "gui.securitycraft:invScan.yes" : "gui.securitycraft:invScan.no")).withStyle(TextFormatting.GRAY);
		}

		@Override
		public ITextComponent getValueText() {
			return new TranslationTextComponent(get() ? "gui.securitycraft:invScan.yes" : "gui.securitycraft:invScan.no");
		}
	}

	public static class DisabledOption extends BooleanOption {
		public DisabledOption(Boolean value) {
			super("disabled", value);
		}

		@Override
		public String getKey(String denotation) {
			return "option.generic.disabled";
		}
	}

	public static class IgnoreOwnerOption extends BooleanOption {
		public IgnoreOwnerOption(Boolean value) {
			super("ignoreOwner", value);
		}

		@Override
		public String getKey(String denotation) {
			return "option.generic.ignoreOwner";
		}
	}

	public static class RespectInvisibilityOption extends BooleanOption {
		public RespectInvisibilityOption() {
			this(false);
		}

		public RespectInvisibilityOption(Boolean value) {
			super("respectInvisibility", value);
		}

		@Override
		public String getKey(String denotation) {
			return "option.generic.respectInvisibility";
		}

		public boolean isConsideredInvisible(LivingEntity entity) {
			return get() && entity.hasEffect(Effects.INVISIBILITY);
		}
	}

	public static class SendAllowlistMessageOption extends BooleanOption {
		public SendAllowlistMessageOption(Boolean value) {
			super("sendAllowlistMessage", value);
		}

		@Override
		public String getKey(String denotation) {
			return "option.generic.sendAllowlistMessage";
		}
	}

	public static class SendDenylistMessageOption extends BooleanOption {
		public SendDenylistMessageOption(Boolean value) {
			super("sendDenylistMessage", value);
		}

		@Override
		public String getKey(String denotation) {
			return "option.generic.sendDenylistMessage";
		}
	}

	/**
	 * A subclass of {@link Option} set up to handle integers.
	 */
	public static class IntOption extends Option<Integer> implements ISlider {
		private Supplier<BlockPos> pos;

		public IntOption(Supplier<BlockPos> pos, String optionName, Integer value, Integer min, Integer max, Integer increment) {
			super(optionName, value, min, max, increment);
			this.pos = pos;
		}

		@Override
		public void toggle() {}

		@Override
		public void load(CompoundNBT tag) {
			if (tag.contains(getName()))
				value = tag.getInt(getName());
			else
				value = getDefaultValue();
		}

		@Override
		public void save(CompoundNBT tag, Integer value) {
			tag.putInt(getName(), value);
		}

		@Override
		public boolean isSlider() {
			return true;
		}

		@Override
		public void onChangeSliderValue(Slider slider) {
			if (!(slider instanceof NamedSlider))
				return;

			setValue((int) slider.getValue());
			slider.setMessage(Utils.localize(getKey(((NamedSlider) slider).getDenotation()), toString()));
			SecurityCraft.channel.sendToServer(new UpdateSliderValue(pos.get(), this, get()));
		}
	}

	public static class SmartModuleCooldownOption extends IntOption {
		public SmartModuleCooldownOption(Supplier<BlockPos> pos) {
			super(pos, "smartModuleCooldown", 100, 20, 400, 1);
		}

		@Override
		public String getKey(String denotation) {
			return "option.generic.smartModuleCooldown";
		}
	}

	public static class SignalLengthOption extends IntOption {
		public SignalLengthOption(Supplier<BlockPos> pos, int defaultLength) {
			super(pos, "signalLength", defaultLength, 0, 400, 5); //20 seconds max
		}

		@Override
		public String getKey(String denotation) {
			return "option.generic.signalLength";
		}
	}

	/**
	 * A subclass of {@link Option} set up to handle doubles.
	 */
	public static class DoubleOption extends Option<Double> implements ISlider {
		private Supplier<BlockPos> pos;

		public DoubleOption(Supplier<BlockPos> pos, String optionName, Double value, Double min, Double max, Double increment) {
			super(optionName, value, min, max, increment);
			this.pos = pos;
		}

		@Override
		public void toggle() {}

		@Override
		public void load(CompoundNBT tag) {
			if (tag.contains(getName()))
				value = tag.getDouble(getName());
			else
				value = getDefaultValue();
		}

		@Override
		public void save(CompoundNBT tag, Double value) {
			tag.putDouble(getName(), value);
		}

		@Override
		public String toString() {
			return Double.toString(value).length() > 5 ? Double.toString(value).substring(0, 5) : Double.toString(value);
		}

		@Override
		public boolean isSlider() {
			return true;
		}

		@Override
		public void onChangeSliderValue(Slider slider) {
			if (!(slider instanceof NamedSlider))
				return;

			setValue(slider.getValue());
			slider.setMessage(Utils.localize(getKey(((NamedSlider) slider).getDenotation()), toString()));
			SecurityCraft.channel.sendToServer(new UpdateSliderValue(pos.get(), this, get()));
		}
	}

	public static class EnumOption<T extends Enum<T>> extends Option<T> {
		private final Class<T> enumClass;

		public EnumOption(String optionName, T value, Class<T> enumClass) {
			super(optionName, value);
			this.enumClass = enumClass;
		}

		@Override
		public void toggle() {
			T[] enumConstants = enumClass.getEnumConstants();
			int next = (value.ordinal() + 1) % enumConstants.length;

			value = enumConstants[next];
		}

		@Override
		public void load(CompoundNBT tag) {
			T[] enumConstants = enumClass.getEnumConstants();
			int ordinal = tag.getInt(getName());

			if (ordinal >= 0 && ordinal < enumConstants.length)
				value = enumConstants[ordinal];
			else
				value = getDefaultValue();
		}

		@Override
		public void save(CompoundNBT tag, T value) {
			tag.putInt(getName(), value.ordinal());
		}

		@Override
		public ITextComponent getValueText() {
			return new StringTextComponent(value.name());
		}

		@Override
		public ITextComponent getDefaultInfo() {
			return new TranslationTextComponent("securitycraft.option.default", getValueText()).withStyle(TextFormatting.GRAY);
		}
	}

	public static class TargetingModeOption extends EnumOption<TargetingMode> {
		public TargetingModeOption(TargetingMode defaultValue) {
			super("targetingMode", defaultValue, TargetingMode.class);
		}

		@Override
		public String getKey(String denotation) {
			return "option.generic.targetingMode";
		}

		@Override
		public ITextComponent getValueText() {
			return value.translate();
		}
	}
}