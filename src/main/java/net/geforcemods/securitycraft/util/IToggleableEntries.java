package net.geforcemods.securitycraft.util;

import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * Interface for all block entities which contain a toggleable list, which gets displayed in a
 * {@link net.geforcemods.securitycraft.screen.RiftStabilizerScreen}.
 *
 * @param <T> The type of the list to be displayed. There are no limitations to the type, however its implementation of
 *            {@link Object#toString} should return a translation key in order to be displayed in the scroll list.
 */
public interface IToggleableEntries<T> {
	/**
	 * Enables a filter if it was disabled, and vice versa
	 *
	 * @param type The type of which the filter should be toggled
	 */
	default void toggleFilter(T type) {
		setFilter(type, !getFilter(type));
	}

	/**
	 * Sets the state of a filter to a specific value
	 *
	 * @param type The type of which the filter's state should be changed
	 * @param allowed The state to set the filter to
	 */
	void setFilter(T type, boolean allowed);

	/**
	 * @param type The type the filter state should be checked of
	 * @return Whether the filter of the given type is enabled or not
	 */
	public boolean getFilter(T type);

	/**
	 * @return The full list of all filters of the block entity. Each map entry represents a filter, containing the type and its
	 *         respective filter state
	 */
	Map<T, Boolean> getFilters();

	/**
	 * @return A function which maps all possible types to their corresponding comparator output, or 0 if no comparator output
	 *         should be present.
	 */
	default ToIntFunction<T> getComparatorOutputFunction() {
		return t -> 0;
	}

	/**
	 * @return The "fallback" type that gets used for all things not specified within the other types. This type is treated
	 *         differently by the screen, like getting rendered at the bottom of the list.
	 */
	T getDefaultType();

	/**
	 * @return The name of the default type, if {@link Object#toString} does not return the correct translation key
	 */
	default String getDefaultTypeName() {
		return getDefaultType().toString();
	}
}
