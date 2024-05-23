package net.geforcemods.securitycraft.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;

public record SCManualPage(Item item, PageGroup group, TranslatableComponent title, TranslatableComponent helpInfo, String designedBy, boolean hasRecipeDescription) {
	public Object getInWorldObject() {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();

			if (block.defaultBlockState().hasBlockEntity())
				return ((EntityBlock) block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
		}

		return null;
	}
}
