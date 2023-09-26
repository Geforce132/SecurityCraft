package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class KeypadChestItemRenderer extends ItemStackTileEntityRenderer {
	private static KeypadChestBlockEntity dummyBe;
	private static KeypadChestRenderer dummyRenderer = null;

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (dummyRenderer == null)
			dummyRenderer = new KeypadChestRenderer(TileEntityRendererDispatcher.instance);

		if (dummyBe == null)
			dummyBe = new KeypadChestBlockEntity();

		dummyRenderer.render(dummyBe, 0.0F, pose, buffer, combinedLight, combinedOverlay);
	}
}
