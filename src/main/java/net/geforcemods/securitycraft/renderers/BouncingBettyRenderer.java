package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
		pose.translate(0.0D, 0.5D, 0.0D);

		if (entity.getFuse() - partialTicks + 1.0F < 10.0F) {
			float alpha = 1.0F - (entity.getFuse() - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			pose.scale(scale, scale, scale);
		}

		pose.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
		pose.translate(-0.5D, -0.5D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(SCContent.BOUNCING_BETTY.get().defaultBlockState(), pose, buffer, packedLight, OverlayTexture.NO_OVERLAY);
		pose.popPose();
		super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(BouncingBetty entity) {
		return PlayerContainer.BLOCK_ATLAS;
	}
}