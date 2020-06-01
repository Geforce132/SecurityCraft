package net.geforcemods.securitycraft.api;

import net.minecraft.nbt.CompoundNBT;

/**
 * Let your TileEntity implement this to be able to add options to it
 * @author bl4ckscor3
 */
public interface ICustomizable
{
	/**
	 * @return An array of what custom {@link Option}s this TileEntity has.
	 */
	public Option<?>[] customOptions();

	/**
	 * Called whenever an {@link Option} in this TileEntity changes its value
	 * @param option The changed Option
	 */
	public default void onOptionChanged(Option<?> option) {}

	/**
	 * Call this from your read method. Used for reading the options from a tag. Use in conjunction with writeOptions.
	 * @param tag The tag to read the options from
	 */
	public default void readOptions(CompoundNBT tag)
	{
		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.readFromNBT(tag);
	}

	/**
	 * Call this from your write method. Used for writing the options to a tag. Use in conjunction with readOptions.
	 * @param tag The tag to write the options to
	 * @return The modified CompoundNBT
	 */
	public default CompoundNBT writeOptions(CompoundNBT tag)
	{
		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.writeToNBT(tag);

		return tag;
	}
}
