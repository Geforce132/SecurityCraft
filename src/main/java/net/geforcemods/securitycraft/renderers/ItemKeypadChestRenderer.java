package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class ItemKeypadChestRenderer extends ItemStackTileEntityRenderer {
	private static KeypadChestBlockEntity dummyTe;
	private static KeypadChestRenderer dummyRenderer = null;

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (dummyRenderer == null)
			dummyRenderer = new KeypadChestRenderer(TileEntityRendererDispatcher.instance);

		if (dummyTe == null)
			dummyTe = new KeypadChestBlockEntity();

		dummyRenderer.render(dummyTe, 0.0F, matrix, buffer, combinedLight, combinedOverlay);
	}
}
