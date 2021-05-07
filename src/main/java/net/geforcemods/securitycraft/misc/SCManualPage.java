package net.geforcemods.securitycraft.misc;

import net.minecraft.item.Item;

public class SCManualPage {

	private Item item;
	private String helpInfo;
	private String designedBy = "";
	private boolean hasRecipeDescription;

	public SCManualPage(Item item, String helpInfo){
		this.item = item;
		this.helpInfo = helpInfo;
	}

	public Item getItem() {
		return item;
	}

	public String getHelpInfo() {
		return helpInfo;
	}

	public void setDesignedBy(String designedBy)
	{
		this.designedBy = designedBy;
	}

	public String getDesignedBy()
	{
		return designedBy;
	}

	public void setHasRecipeDescription(boolean hasRecipeDescription)
	{
		this.hasRecipeDescription = hasRecipeDescription;
	}

	public boolean hasRecipeDescription()
	{
		return hasRecipeDescription;
	}
}
