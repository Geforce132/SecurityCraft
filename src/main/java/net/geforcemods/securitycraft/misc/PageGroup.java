package net.geforcemods.securitycraft.misc;

import net.minecraft.item.crafting.Ingredient;

public enum PageGroup {
	NO_PAGE(false, "", ""), //ignored anyway
	SINGLE_ITEM(false, "", ""), //ignored anyway
	REINFORCED(false, "gui.securitycraft:scManual.reinforced", "help.securitycraft.reinforced.info"),
	BLOCK_MINES(true, "gui.securitycraft:scManual.block_mines", "help.securitycraft.block_mines.info"),
	BUTTONS(true, "gui.securitycraft:scManual.reinforced_buttons", "help.securitycraft.reinforced_buttons.info"),
	PRESSURE_PLATES(true, "gui.securitycraft:scManual.reinforced_pressure_plates", "help.securitycraft.reinforced_pressure_plates.info"),
	KEYCARDS(true, "gui.securitycraft:scManual.keycards", "help.securitycraft.keycards.info"),
	BLOCK_REINFORCERS(true, "gui.securitycraft:scManual.block_reinforcers", "help.securitycraft.block_reinforcers.info");

	private final boolean hasRecipeGrid;
	private final String title;
	private final String specialInfoKey;
	private Ingredient items = Ingredient.EMPTY;

	PageGroup(boolean hasRecipeGrid, String title, String specialInfoKey) {
		this.hasRecipeGrid = hasRecipeGrid;
		this.title = title;
		this.specialInfoKey = specialInfoKey;
	}

	public boolean hasRecipeGrid() {
		return hasRecipeGrid;
	}

	public String getTitle() {
		return title;
	}

	public String getSpecialInfoKey() {
		return specialInfoKey;
	}

	public Ingredient getItems() {
		return items;
	}

	public void setItems(Ingredient items) {
		this.items = items;
	}
}
