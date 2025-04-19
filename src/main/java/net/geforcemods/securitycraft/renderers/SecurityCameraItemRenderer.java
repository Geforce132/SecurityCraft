package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class SecurityCameraItemRenderer extends ItemStackTileEntityRenderer {
	private final SecurityCameraModel model;

	public SecurityCameraItemRenderer() {
		model = new SecurityCameraModel();
		model.cameraRotationPoint2.visible = false;
		model.rotateCameraY(0.0F);
	}

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(SecurityCameraRenderer.TEXTURE)), packedLight, packedOverlay, 0.4392156862745098F, 1.0F, 1.0F, 1.0F);
	}
}
