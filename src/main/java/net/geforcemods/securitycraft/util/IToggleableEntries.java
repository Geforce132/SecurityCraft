package net.geforcemods.securitycraft.util;

import java.util.Map;

public interface IToggleableEntries<T> {
	default void toggleFilter(T type) {
		setFilter(type, !getFilter(type));
	}

	void setFilter(T teleportationType, boolean allowed);

	public boolean getFilter(T type);

	Map<T, Boolean> getFilters();

	T getDefaultType();

	default String getDefaultTypeName() {
		return getDefaultType().toString();
	}
}
