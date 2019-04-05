package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemKeypadChestRenderer extends TileEntityItemStackRenderer {

	@Override
	public void renderByItem(ItemStack item) {
		Block block = Block.getBlockFromItem(item.getItem());

		if (block == SCContent.keypadChest)
			TileEntityRendererDispatcher.instance.renderAsItem(new TileEntityKeypadChest());
		else
			super.renderByItem(item);

	}

}
