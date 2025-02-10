package net.geforcemods.securitycraft.misc;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;

public class SCManualPage {
	private final Item item;
	private final PageGroup pageType;
	private final TextComponentTranslation title;
	private final TextComponentTranslation helpInfo;
	private final String designedBy;
	private final boolean hasRecipeDescription;
	private final Supplier<Boolean> configValue;

	public SCManualPage(Item item, PageGroup pageType, TextComponentTranslation title, TextComponentTranslation helpInfo, String designedBy, boolean hasRecipeDescription) {
		this.item = item;
		this.pageType = pageType;
		this.title = title;
		this.helpInfo = helpInfo;
		this.designedBy = designedBy;
		this.hasRecipeDescription = hasRecipeDescription;
		configValue = () -> true;
	}

	public SCManualPage(Item item, PageGroup pageType, TextComponentTranslation title, TextComponentTranslation helpInfo, String designedBy, boolean hasRecipeDescription, Supplier<Boolean> configValue) {
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

	public PageGroup getPageType() {
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
		return !configValue.get();
	}

	public Object getInWorldObject() {
		if (item instanceof ItemBlock) {
			Block block = ((ItemBlock) item).getBlock();

			if (block.hasTileEntity(block.getDefaultState())) {
				TileEntity te = block.createTileEntity(SecurityCraft.proxy.getClientLevel(), block.getDefaultState());

				te.blockType = block;
				return te;
			}
		}

		return null;
	}
}
