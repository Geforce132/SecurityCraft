package net.geforcemods.securitycraft.misc;

import net.minecraft.item.crafting.Ingredient;

public enum PageGroup {
	NONE(false), //ignored anyways
	REINFORCED(false),
	BLOCK_MINES(true),
	BUTTONS(true),
	PRESSURE_PLATES(true),
	FURNACES(true),
	KEYCARDS(true),
	SECRET_SIGNS(true),
	BLOCK_REINFORCERS(true);

	private final boolean hasRecipeGrid;
	private Ingredient items = Ingredient.EMPTY;

	PageGroup(boolean hasRecipeGrid) {
		this.hasRecipeGrid = hasRecipeGrid;
	}

	public boolean hasRecipeGrid() {
		return hasRecipeGrid;
	}

	public Ingredient getItems() {
		return items;
	}

	public void setItems(Ingredient items) {
		this.items = items;
	}
}
