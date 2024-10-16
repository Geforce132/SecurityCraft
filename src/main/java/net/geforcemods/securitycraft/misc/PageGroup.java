package net.geforcemods.securitycraft.misc;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public enum PageGroup {
	NONE(false, "", ""), //ignored anyway
	REINFORCED(false, "gui.securitycraft:scManual.reinforced", "securitycraft.reinforced.info"),
	BLOCK_MINES(true, "gui.securitycraft:scManual.block_mines", "securitycraft.block_mines.info"),
	BUTTONS(true, "gui.securitycraft:scManual.reinforced_buttons", "securitycraft.reinforced_buttons.info"),
	PRESSURE_PLATES(true, "gui.securitycraft:scManual.reinforced_pressure_plates", "securitycraft.reinforced_pressure_plates.info"),
	FURNACE_MINES(true, "block.securitycraft.furnace_mine", "securitycraft.furnace_mines.info"),
	KEYCARDS(true, "gui.securitycraft:scManual.keycards", "securitycraft.keycards.info"),
	SECRET_SIGNS(true, "gui.securitycraft:scManual.secret_signs", "securitycraft.secret_signs.info"),
	SECRET_HANGING_SIGNS(true, "gui.securitycraft:scManual.secret_hanging_signs", "securitycraft.secret_signs.info"),
	BLOCK_REINFORCERS(true, "gui.securitycraft:scManual.block_reinforcers", "securitycraft.block_reinforcers.info"),
	DISPLAY_CASES(true, "gui.securitycraft:scManual.display_cases", "securitycraft.display_cases.info"),
	FENCE_GATES(true, "gui.securitycraft:scManual.reinforced_fence_gates", "securitycraft.reinforced_fence_gates.info"),
	SECURITY_SEA_BOATS(true, "gui.securitycraft:scManual.security_sea_boats", "securitycraft.security_sea_boats.info");

	private final boolean hasRecipeGrid;
	private final String title;
	private final String specialInfoKey;
	private Supplier<List<ItemStack>> itemGetter;
	private List<ItemStack> items = null;

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

	public List<ItemStack> getItems() {
		if (items == null)
			items = itemGetter.get();

		return items;
	}

	public void setItems(Ingredient ingredient) {
		itemGetter = () -> ingredient.display().resolveForStacks(SlotDisplayContext.fromLevel(Minecraft.getInstance().level));
	}
}
