package net.geforcemods.securitycraft.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;

public interface PositionComponent<T extends PositionEntry, E> {
	public List<T> positions();

	public T createEntry(int index, GlobalPos globalPos, E extra);

	public void setOnStack(ItemStack stack, List<T> newPositionList);

	public default int size() {
		return positions().size();
	}

	public default boolean isEmpty() {
		return positions().isEmpty();
	}

	public default boolean isPositionAdded(GlobalPos pos) {
		return positions().stream().map(T::globalPos).anyMatch(pos::equals);
	}

	public default boolean add(ItemStack stack, GlobalPos globalPos, int maximum, E extra) {
		List<T> positions = positions();

		if (positions.size() < maximum) {
			List<T> sortedPositions = positions.stream().sorted(Comparator.comparing(c -> c.index())).toList();
			int nextFreeIndex = 0;

			for (int i = 1; i <= maximum; i++) {
				if (i > sortedPositions.size() || sortedPositions.get(i - 1).index() != i) {
					nextFreeIndex = i;
					break;
				}
			}

			if (nextFreeIndex > 0) {
				List<T> newPositionsList = new ArrayList<>(positions);

				newPositionsList.add(createEntry(nextFreeIndex, globalPos, extra));
				setOnStack(stack, newPositionsList);
				return true;
			}
		}

		return false;
	}

	public default boolean remove(ItemStack stack, GlobalPos pos) {
		List<T> positions = positions();

		if (!positions.isEmpty()) {
			List<T> newPositionsList = new ArrayList<>(positions);

			newPositionsList.removeIf(position -> position.globalPos().equals(pos));

			if (newPositionsList.size() != positions.size()) {
				setOnStack(stack, newPositionsList);
				return true;
			}
		}

		return false;
	}

	public default List<T> filledOrderedList(int maximumEntries) {
		List<T> sortedPositions = new ArrayList<>(positions());
		List<T> toReturn = new ArrayList<>();
		int indexToCheck = 0;

		sortedPositions.sort(Comparator.comparing(c -> c.index()));

		for (int i = 1; i <= maximumEntries; i++) {
			if (indexToCheck >= sortedPositions.size())
				toReturn.add(null);
			else {
				T existingPosition = sortedPositions.get(indexToCheck);

				if (existingPosition.index() != i)
					toReturn.add(null);
				else {
					toReturn.add(existingPosition);
					indexToCheck++;
				}
			}
		}

		return toReturn;
	}
}
