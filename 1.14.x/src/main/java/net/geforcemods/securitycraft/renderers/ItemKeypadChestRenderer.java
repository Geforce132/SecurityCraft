package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemKeypadChestRenderer extends ItemStackTileEntityRenderer {
	private static final TileEntityKeypadChest DUMMY = new TileEntityKeypadChest();

	@Override
	public void renderByItem(ItemStack item) {
		Block block = Block.getBlockFromItem(item.getItem());

		if (block == SCContent.keypadChest)
			TileEntityRendererDispatcher.instance.renderAsItem(DUMMY);
		else
			super.renderByItem(item);

	}

}
