package net.geforcemods.securitycraft.misc;

import java.util.function.BooleanSupplier;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public class SCManualPage {

	private Item item;
	private String helpInfo;
	private NonNullList<Ingredient> customRecipe;
	private BooleanSupplier configValue = () -> true;
	private String designedBy = "";

	public SCManualPage(Item item, String helpInfo){
		this.item = item;
		this.helpInfo = helpInfo;
		customRecipe = null;
	}

	public SCManualPage(Item item, String helpInfo, BooleanSupplier configValue){
		this.item = item;
		this.helpInfo = helpInfo;
		this.configValue = configValue;
	}

	public Item getItem() {
		return item;
	}

	public String getHelpInfo() {
		return helpInfo;
	}

	public NonNullList<Ingredient> getRecipe() {
		return customRecipe;
	}

	public boolean hasCustomRecipe() {
		return (customRecipe != null);
	}

	public boolean isRecipeDisabled()
	{
		return !configValue.getAsBoolean();
	}

	public void setDesignedBy(String designedBy)
	{
		this.designedBy = designedBy;
	}

	public String getDesignedBy()
	{
		return designedBy;
	}
}
