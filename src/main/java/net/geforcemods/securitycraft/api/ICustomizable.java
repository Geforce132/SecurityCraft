package net.geforcemods.securitycraft.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Implement this to be able to add options to the object
 *
 * @author bl4ckscor3
 */
public interface ICustomizable {
	/**
	 * @return An array of what custom {@link Option}s this bject has.
	 */
	public Option<?>[] customOptions();

	/**
	 * Called whenever an {@link Option} in this object changes its value
	 *
	 * @param option The changed Option
	 */
	public default void onOptionChanged(Option<?> option) {
		if (this instanceof BlockEntity be)
			be.setChanged();
	}

	/**
	 * Used for reading the options from a tag. Use in conjunction with writeOptions.
	 *
	 * @param tag The tag to read the options from
	 */
	public default void readOptions(CompoundTag tag) {
		Option<?>[] customOptions = customOptions();

		if (customOptions.length > 0) {
			for (Option<?> option : customOptions) {
				option.load(tag);
			}
		}
	}

	/**
	 * Used for writing the options to a tag. Use in conjunction with readOptions.
	 *
	 * @param tag The tag to write the options to
	 * @return The modified CompoundTag
	 */
	public default CompoundTag writeOptions(CompoundTag tag) {
		Option<?>[] customOptions = customOptions();

		if (customOptions.length > 0) {
			for (Option<?> option : customOptions) {
				option.save(tag);
			}
		}

		return tag;
	}
}
