package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class DisplayCaseItemRenderer extends ItemStackTileEntityRenderer {
	private static DisplayCaseBlockEntity dummyTe;
	private static DisplayCaseRenderer dummyRenderer = null;

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (dummyRenderer == null)
			dummyRenderer = new DisplayCaseRenderer(TileEntityRendererDispatcher.instance);

		if (dummyTe == null) {
			dummyTe = new DisplayCaseBlockEntity();
			dummyTe.blockState = SCContent.DISPLAY_CASE.get().defaultBlockState();
		}

		dummyRenderer.render(dummyTe, 0.0F, matrix, buffer, combinedLight, combinedOverlay);
	}
}
