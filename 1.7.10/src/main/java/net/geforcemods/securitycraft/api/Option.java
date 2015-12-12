package net.geforcemods.securitycraft.api;

import net.minecraft.nbt.NBTTagCompound;

public class Option<T> {
	
	private String name;
	
	protected T value;
	private T step;
	private T minimum;
	private T maximum;
		
	public Option(String optionName, T value) {
		this.name = optionName;
		this.value = value;
	}
	
	public Option(String optionName, T value, T min, T max, T step) {
		this.name = optionName;
		this.value = value;
		this.step = step;
		this.minimum = min;
		this.maximum = max;
	}
	
	public void toggle() {}
	
	public boolean asBoolean() {
		return (Boolean) value;
	}
	
	public int asInteger() {
		return (Integer) value;
	}
	
	public double asDouble() {
		return (Double) value;
	}
	
	public float asFloat() {
		return (Float) value;
	}
	
	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound compound) {
		if(value instanceof Boolean) {
			value = (T) ((Boolean) compound.getBoolean(name));
		}else if(value instanceof Integer) {
			value = (T) ((Integer) compound.getInteger(name));
		}else if(value instanceof Double) {
			value = (T) ((Double) compound.getDouble(name));
		}else if(value instanceof Float) {
			value = (T) ((Float) compound.getFloat(name));
		}
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		if(value instanceof Boolean) {
			compound.setBoolean(name, asBoolean());
		}else if(value instanceof Integer) {
			compound.setInteger(name, asInteger());
		}else if(value instanceof Double) {
			compound.setDouble(name, asDouble());
		}else if(value instanceof Float) {
			compound.setFloat(name, asFloat());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public T getStep() {
		return step;
	}
	
	public T getMin() {
		return minimum;
	}
	
	public T getMax() {
		return maximum;
	}
	
public static class OptionBoolean extends Option<Boolean>{

	public OptionBoolean(String optionName, Boolean value) {
		super(optionName, value);
	}
	
	public OptionBoolean(String optionName, Boolean value, Boolean min, Boolean max, Boolean step) {
		super(optionName, value, min, max, step);
	}
	
	public void toggle() {
		setValue(!getValue());
	}
	
	public Boolean getValue() {
		return (boolean) value;
	}
	
	public String toString() {
		return ((boolean) value) + "";
	}
}
	
public static class OptionInt extends Option<Integer>{

	public OptionInt(String optionName, Integer value) {
		super(optionName, value);
	}
	
	public OptionInt(String optionName, Integer value, Integer min, Integer max, Integer step) {
		super(optionName, value, min, max, step);
	}
	
	public void toggle() {
		if((getValue() + getStep()) > getMax()) {
			setValue(getMax());
			return;
		}
		
		if(getValue() == getMax()) {
			setValue(getMin());
			return;
		}
		
		setValue(getValue() + getStep());
	}
	
	public Integer getValue() {
		return (int) value;
	}
	
	public String toString() {
		return ((int) value) + "";
	}
}

public static class OptionDouble extends Option<Double>{

	public OptionDouble(String optionName, Double value) {
		super(optionName, value);
	}
	
	public OptionDouble(String optionName, Double value, Double min, Double max, Double step) {
		super(optionName, value, min, max, step);
	}
	
	public void toggle() {
		if((getValue() + getStep()) > getMax()) {
			setValue(getMax());
			return;
		}
		
		if(getValue() == getMax()) {
			setValue(getMin());
			return;
		}
		
		setValue(getValue() + getStep());
	}
	
	public Double getValue() {
		return (double) value;
	}
	
	public String toString() {
		return Double.toString(value).length() > 5 ? Double.toString(value).substring(0, 5) : Double.toString(value);
	}
	
}

public static class OptionFloat extends Option<Float>{

	public OptionFloat(String optionName, Float value) {
		super(optionName, value);
	}
	
	public OptionFloat(String optionName, Float value, Float min, Float max, Float step) {
		super(optionName, value, min, max, step);
	}
	
	public void toggle() {
		if(getValue() != getMax() && (getValue() + getStep()) > getMax()) {
			setValue(getMax());
			return;
		}

		if(getValue() == getMax()) {
			setValue(getMin());
			return;
		}
		
		setValue(getValue() + getStep());
	}
	
	public Float getValue() {
		return (Float) value;
	}
	
	public String toString() {
		return Float.toString(value).length() > 5 ? Float.toString(value).substring(0, 5) : Float.toString(value);
	}
	
}
	
}
