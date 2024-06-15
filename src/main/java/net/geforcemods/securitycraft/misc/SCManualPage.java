package net.geforcemods.securitycraft.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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

	public Item item() {
		return item;
	}

	public PageGroup group() {
		return group;
	}

	public TranslationTextComponent title() {
		return title;
	}

	public TranslationTextComponent helpInfo() {
		return helpInfo;
	}

	public String designedBy() {
		return designedBy;
	}

	public boolean hasRecipeDescription() {
		return hasRecipeDescription;
	}

	public Object getInWorldObject() {
		if (item instanceof BlockItem) {
			Block block = ((BlockItem) item).getBlock();

			if (block.hasTileEntity(block.defaultBlockState())) {
				BlockState state = block.defaultBlockState();
				TileEntity te = block.createTileEntity(state, Minecraft.getInstance().level);

				te.blockState = state;
				return te;
			}
		}

		return null;
	}
}
