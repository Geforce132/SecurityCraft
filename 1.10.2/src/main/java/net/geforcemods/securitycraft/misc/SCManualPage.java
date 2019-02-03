package net.geforcemods.securitycraft.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SCManualPage {

	private Item item;
	private String helpInfo;
	private ItemStack[] customRecipe;
	private boolean configValue = true;
	private String designedBy = "";

	public SCManualPage(Item item, String helpInfo){
		this.item = item;
		this.helpInfo = helpInfo;
		customRecipe = null;
	}

	public SCManualPage(Item item, String helpInfo, boolean configValue){
		this.item = item;
		this.helpInfo = helpInfo;
		this.configValue = configValue;
	}

	public SCManualPage(Item item, String helpInfo, ItemStack[] customRecipe){
		this.item = item;
		this.helpInfo = helpInfo;
		this.customRecipe = customRecipe;
	}

	public Item getItem() {
		return item;
	}

	public String getItemName() {
		return item.getUnlocalizedName().substring(5).split("\\.")[0];
	}

	public String getHelpInfo() {
		return helpInfo;
	}

	public ItemStack[] getRecipe() {
		return customRecipe;
	}

	public boolean hasCustomRecipe() {
		return (customRecipe != null);
	}

	public boolean isRecipeDisabled()
	{
		return !configValue;
	}

	public void designedBy(String designedBy)
	{
		this.designedBy = designedBy;
	}

	public String designedBy()
	{
		return designedBy;
	}
}
