package net.geforcemods.securitycraft.misc;

import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;

public class SCManualPage {

	private Item item;
	private TranslationTextComponent helpInfo;
	private String designedBy = "";
	private boolean hasRecipeDescription;

	public SCManualPage(Item item, TranslationTextComponent helpInfo){
		this.item = item;
		this.helpInfo = helpInfo;
	}

	public Item getItem() {
		return item;
	}

	public TranslationTextComponent getHelpInfo() {
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
