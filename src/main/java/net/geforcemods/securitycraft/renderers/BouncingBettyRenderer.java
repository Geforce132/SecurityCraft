package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.renderers.state.BouncingBettyRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Mth;

public class BouncingBettyRenderer extends EntityRenderer<BouncingBetty, BouncingBettyRenderState> {
	public BouncingBettyRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		shadowRadius = 0.5F;
	}

	@Override
	public void submit(BouncingBettyRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		pose.pushPose();
		pose.translate(0.0F, 0.5F, 0.0F);

		if (state.fuseRemainingInTicks < 10.0F) {
			float scale = 1.0F - state.fuseRemainingInTicks / 10.0F;

			scale = Mth.clamp(scale, 0.0F, 1.0F);
			scale *= scale;
			scale *= scale;
			scale = 1.0F + scale * 0.3F;
			pose.scale(scale, scale, scale);
		}

		pose.mulPose(Axis.YP.rotationDegrees(-90.0F));
		pose.translate(-0.5F, -0.5F, 0.5F);
		pose.mulPose(Axis.YP.rotationDegrees(90.0F));
		TntMinecartRenderer.submitWhiteSolidBlock(SCContent.BOUNCING_BETTY.get().defaultBlockState(), pose, collector, state.lightCoords, state.fuseRemainingInTicks / 5 % 2 == 0, state.outlineColor);
		pose.popPose();
		super.submit(state, pose, collector, camera);
	}

	@Override
	public BouncingBettyRenderState createRenderState() {
		return new BouncingBettyRenderState();
	}

	@Override
	public void extractRenderState(BouncingBetty entity, BouncingBettyRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.fuseRemainingInTicks = entity.getFuse() - partialTicks + 1.0F;
	}
}