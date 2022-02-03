package net.geforcemods.securitycraft.misc;

import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;

public class SCManualPage {
	private final Item item;
	private final PageGroup group;
	private final TranslationTextComponent title;
	private final TranslationTextComponent helpInfo;
	private final String designedBy;
	private final boolean hasRecipeDescription;

	public SCManualPage(Item item, PageGroup group, TranslationTextComponent title, TranslationTextComponent helpInfo, String designedBy, boolean hasRecipeDescription) {
		this.item = item;
		this.group = group;
		this.title = title;
		this.helpInfo = helpInfo;
		this.designedBy = designedBy;
		this.hasRecipeDescription = hasRecipeDescription;
	}

	public Item getItem() {
		return item;
	}

	public PageGroup getGroup() {
		return group;
	}

	public TranslationTextComponent getTitle() {
		return title;
	}

	public TranslationTextComponent getHelpInfo() {
		return helpInfo;
	}

	public String getDesignedBy() {
		return designedBy;
	}

	public boolean hasRecipeDescription() {
		return hasRecipeDescription;
	}
}
