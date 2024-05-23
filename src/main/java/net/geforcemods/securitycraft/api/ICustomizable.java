package net.geforcemods.securitycraft.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Let your TileEntity implement this to be able to add options to it
 *
 * @author bl4ckscor3
 */
public interface ICustomizable {
	/**
	 * @return The block entity this is for
	 */
	public default BlockEntity getTheBlockEntity() {
		return (BlockEntity) this;
	}

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
	public default <T> void onOptionChanged(Option<T> option) {
		getTheBlockEntity().setChanged();
	}

	/**
	 * Call this from your read method. Used for reading the options from a tag. Use in conjunction with writeOptions.
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
	 * Call this from your write method. Used for writing the options to a tag. Use in conjunction with readOptions.
	 *
	 * @param tag The tag to write the options to
	 * @return The modified CompoundNBT
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
