package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemKeypadChestRenderer implements IItemRenderer {

	protected ModelChest model;

	public ItemKeypadChestRenderer(){

		model = new ModelChest();

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityKeypadChest(), 0.0D, 0.0D, 0.0D, 0.0F);

	}

}
