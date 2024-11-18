package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

public class BouncingBettyRenderer extends EntityRenderer<BouncingBetty> {
	public BouncingBettyRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(BouncingBetty entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
		pose.pushPose();
		pose.translate(0.0F, 0.5F, 0.0F);

		int fuse = entity.getFuse();

		if (fuse - partialTicks + 1.0F < 10.0F) {
			float scale = 1.0F - (fuse - partialTicks + 1.0F) / 10.0F;

			scale = Mth.clamp(scale, 0.0F, 1.0F);
			scale *= scale;
			scale *= scale;
			scale = 1.0F + scale * 0.3F;
			pose.scale(scale, scale, scale);
		}

		pose.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
		pose.translate(-0.5D, -0.5D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		TntMinecartRenderer.renderWhiteSolidBlock(SCContent.BOUNCING_BETTY.get().defaultBlockState(), pose, buffer, packedLight, fuse / 5 % 2 == 0);
		pose.popPose();
		super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(BouncingBetty entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}