package net.geforcemods.securitycraft.misc;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;

public class SCManualPage {

	private Item item;
	private TranslatableComponent helpInfo;
	private String designedBy = "";
	private boolean hasRecipeDescription;

	public SCManualPage(Item item, TranslatableComponent helpInfo){
		this.item = item;
		this.helpInfo = helpInfo;
	}

	public Item getItem() {
		return item;
	}

	public TranslatableComponent getHelpInfo() {
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
