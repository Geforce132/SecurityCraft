package net.geforcemods.securitycraft.misc;

import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentTranslation;

public class SCManualPage {
	private final Item item;
	private final PageType pageType;
	private final TextComponentTranslation title;
	private final TextComponentTranslation helpInfo;
	private final String designedBy;
	private final boolean hasRecipeDescription;
	private final boolean configValue;

	public SCManualPage(Item item, PageType pageType, TextComponentTranslation title, TextComponentTranslation helpInfo, String designedBy, boolean hasRecipeDescription) {
		this.item = item;
		this.pageType = pageType;
		this.title = title;
		this.helpInfo = helpInfo;
		this.designedBy = designedBy;
		this.hasRecipeDescription = hasRecipeDescription;
		configValue = true;
	}

	public SCManualPage(Item item, PageType pageType, TextComponentTranslation title, TextComponentTranslation helpInfo, String designedBy, boolean hasRecipeDescription, boolean configValue) {
		this.item = item;
		this.pageType = pageType;
		this.title = title;
		this.helpInfo = helpInfo;
		this.designedBy = designedBy;
		this.hasRecipeDescription = hasRecipeDescription;
		this.configValue = configValue;
	}

	public Item getItem() {
		return item;
	}

	public PageType getPageType() {
		return pageType;
	}

	public TextComponentTranslation getTitle() {
		return title;
	}

	public TextComponentTranslation getHelpInfo() {
		return helpInfo;
	}

	public String getDesignedBy() {
		return designedBy;
	}

	public boolean hasRecipeDescription() {
		return hasRecipeDescription;
	}

	public boolean isRecipeDisabled() {
		return !configValue;
	}
}
