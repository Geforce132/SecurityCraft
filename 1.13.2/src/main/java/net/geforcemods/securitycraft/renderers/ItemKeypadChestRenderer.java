package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class ItemKeypadChestRenderer extends TileEntityItemStackRenderer {

	@Override
	public void renderByItem(ItemStack item) {
		Block block = Block.getBlockFromItem(item.getItem());

		if (block == SCContent.keypadChest)
			TileEntityRendererDispatcher.instance.render(new TileEntityKeypadChest(), 0.0D, 0.0D, 0.0D, 0.0F);
		else
			super.renderByItem(item);

	}

}
