package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SecurityCameraItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static final int LENS_COLOR = ARGB.color(255, 0x70FFFF);
	private SecurityCameraModel model;

	public SecurityCameraItemRenderer() {
		super(null, null);
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		model = null;
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (model == null) {
			Minecraft mc = Minecraft.getInstance();
			BlockEntityRendererProvider.Context renderingContext = new BlockEntityRendererProvider.Context(mc.getBlockEntityRenderDispatcher(), mc.getBlockRenderer(), mc.getItemRenderer(), mc.getEntityRenderDispatcher(), mc.getEntityModels(), mc.font);

			model = new SecurityCameraModel(renderingContext.bakeLayer(ClientHandler.SECURITY_CAMERA_LOCATION));
			model.cameraRotationPoint2.visible = false;
			model.rotateCameraY(0.0F);
		}

		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(SecurityCameraRenderer.TEXTURE)), packedLight, packedOverlay, LENS_COLOR);
	}
}
