package net.geforcemods.securitycraft.renderers;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ItemTileEntityRenderer extends TileEntityItemStackRenderer {
	private final TileEntity dummyTe;

	public ItemTileEntityRenderer(TileEntity dummyTe) {
		this.dummyTe = dummyTe;
	}

	@Override
	public void renderByItem(ItemStack item) {
		TileEntityRendererDispatcher.instance.render(dummyTe, 0.0D, 0.0D, 0.0D, 0.0F);
	}
}
