package net.geforcemods.securitycraft.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SCManualPage {
	
	private Item item;
	private String itemName;
	private String helpInfo;
	private ItemStack[] customRecipe;

	public SCManualPage(Item item, String itemName, String helpInfo){
		this.item = item;
		this.itemName = itemName;
		this.helpInfo = helpInfo;
		this.customRecipe = null;
	}
	
	public SCManualPage(Item item, String itemName, String helpInfo, ItemStack[] customRecipe){
		this.item = item;
		this.itemName = itemName;
		this.helpInfo = helpInfo;
		this.customRecipe = customRecipe;
	}

	public Item getItem() {
		return item;
	}

	public String getItemName() {
		return itemName;
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

}
