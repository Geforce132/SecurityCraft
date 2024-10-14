package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class BouncingBettyRenderer extends EntityRenderer<BouncingBetty, BouncingBettyRenderState> {
	public BouncingBettyRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(BouncingBettyRenderState state, PoseStack pose, MultiBufferSource buffer, int packedLight) {
		pose.pushPose();
		pose.translate(0.0D, 0.5D, 0.0D);

		if (state.fuse < 10.0F) {
			float alpha = 1.0F - state.fuse / 10.0F;
			alpha = Mth.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			pose.scale(scale, scale, scale);
		}

		pose.mulPose(Axis.YP.rotationDegrees(-90.0F));
		pose.translate(-0.5D, -0.5D, 0.5D);
		pose.mulPose(Axis.YP.rotationDegrees(90.0F));
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(SCContent.BOUNCING_BETTY.get().defaultBlockState(), pose, buffer, packedLight, OverlayTexture.NO_OVERLAY);
		pose.popPose();
		super.render(state, pose, buffer, packedLight);
	}

	@Override
	public BouncingBettyRenderState createRenderState() {
		return new BouncingBettyRenderState();
	}

	@Override
	public void extractRenderState(BouncingBetty entity, BouncingBettyRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.fuse = entity.getFuse() - partialTicks + 1.0F;
	}
}