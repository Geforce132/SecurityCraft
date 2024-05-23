package net.geforcemods.securitycraft.api;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Let your TileEntity implement this to be able to add options to it
 *
 * @author bl4ckscor3
 */
public interface ICustomizable {
	/**
	 * @return An array of what custom {@link Option}s this TileEntity has.
	 */
	public Option<?>[] customOptions();

	/**
	 * Called whenever an {@link Option} in this TileEntity changes its value
	 *
	 * @param <T> The type the Option stores
	 * @param option The changed Option
	 */
	public default <T> void onOptionChanged(Option<T> option) {}

	/**
	 * Call this from your read method. Used for reading the options from a tag. Use in conjunction with writeOptions.
	 *
	 * @param tag The tag to read the options from
	 */
	public default void readOptions(NBTTagCompound tag) {
		Option<?>[] options = customOptions();

		if (options.length > 0) {
			for (Option<?> option : options) {
				option.load(tag);
			}
		}
	}

	/**
	 * Call this from your write method. Used for writing the options to a tag. Use in conjunction with readOptions.
	 *
	 * @param tag The tag to write the options to
	 * @return The modified CompoundNBT
	 */
	public default NBTTagCompound writeOptions(NBTTagCompound tag) {
		Option<?>[] options = customOptions();

		if (options.length > 0) {
			for (Option<?> option : options) {
				option.save(tag);
			}
		}

		return tag;
	}
}
