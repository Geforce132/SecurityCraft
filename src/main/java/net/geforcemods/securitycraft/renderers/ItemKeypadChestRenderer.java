package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class ItemKeypadChestRenderer extends ItemStackTileEntityRenderer
{
	private static final KeypadChestTileEntity DUMMY_TE = new KeypadChestTileEntity();
	private static KeypadChestTileEntityRenderer dummyRenderer = null;

	@Override
	public void render(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
	{
		if(dummyRenderer == null)
			dummyRenderer = new KeypadChestTileEntityRenderer(TileEntityRendererDispatcher.instance);

		dummyRenderer.render(DUMMY_TE, 0.0F, matrix, buffer, combinedLight, combinedOverlay);
	}
}
