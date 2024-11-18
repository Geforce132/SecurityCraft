package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TNTMinecartRenderer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BouncingBettyRenderer extends EntityRenderer<BouncingBetty> {
	public BouncingBettyRenderer(EntityRendererManager renderManager) {
		super(renderManager);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(BouncingBetty entity, float entityYaw, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight) {
		pose.pushPose();
		pose.translate(0.0F, 0.5F, 0.0F);

		int fuse = entity.getFuse();

		if (fuse - partialTicks + 1.0F < 10.0F) {
			float scale = 1.0F - (fuse - partialTicks + 1.0F) / 10.0F;

			scale = MathHelper.clamp(scale, 0.0F, 1.0F);
			scale *= scale;
			scale *= scale;
			scale = 1.0F + scale * 0.3F;
			pose.scale(scale, scale, scale);
		}

		pose.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
		pose.translate(-0.5D, -0.5D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		TNTMinecartRenderer.renderWhiteSolidBlock(SCContent.BOUNCING_BETTY.get().defaultBlockState(), pose, buffer, packedLight, fuse / 5 % 2 == 0);
		pose.popPose();
		super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(BouncingBetty entity) {
		return PlayerContainer.BLOCK_ATLAS;
	}
}