package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ItemKeypadChestRenderer extends TileEntityItemStackRenderer {
	private static final TileEntity DUMMY_TE = new TileEntityKeypadChest();

	@Override
	public void renderByItem(ItemStack item) {
		TileEntityRendererDispatcher.instance.render(DUMMY_TE, 0.0D, 0.0D, 0.0D, 0.0F);
	}
}
