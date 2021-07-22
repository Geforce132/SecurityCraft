package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;

public class ItemKeypadChestRenderer extends BlockEntityWithoutLevelRenderer
{
	private static final KeypadChestTileEntity DUMMY_TE = new KeypadChestTileEntity();
	private static KeypadChestTileEntityRenderer dummyRenderer = null;

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
	{
		if(dummyRenderer == null)
			dummyRenderer = new KeypadChestTileEntityRenderer(BlockEntityRenderDispatcher.instance);

		dummyRenderer.render(DUMMY_TE, 0.0F, matrix, buffer, combinedLight, combinedOverlay);
	}
}
