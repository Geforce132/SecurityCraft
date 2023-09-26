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
	private static DisplayCaseBlockEntity dummyBe;
	private static DisplayCaseRenderer dummyRenderer = null;

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (dummyRenderer == null)
			dummyRenderer = new DisplayCaseRenderer(TileEntityRendererDispatcher.instance);

		if (dummyBe == null) {
			dummyBe = new DisplayCaseBlockEntity();
			dummyBe.blockState = SCContent.DISPLAY_CASE.get().defaultBlockState();
		}

		dummyRenderer.render(dummyBe, 0.0F, pose, buffer, combinedLight, combinedOverlay);
	}
}
