package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.util.Mth;

public class BouncingBettyRenderer extends EntityRenderer<BouncingBetty, BouncingBettyRenderState> {
	private final BlockRenderDispatcher blockRenderer;

	public BouncingBettyRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		shadowRadius = 0.5F;
		blockRenderer = ctx.getBlockRenderDispatcher();
	}

	@Override
	public void render(BouncingBettyRenderState state, PoseStack pose, MultiBufferSource buffer, int packedLight) {
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
		TntMinecartRenderer.renderWhiteSolidBlock(blockRenderer, SCContent.BOUNCING_BETTY.get().defaultBlockState(), pose, buffer, packedLight, state.fuseRemainingInTicks / 5 % 2 == 0);
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
		state.fuseRemainingInTicks = entity.getFuse() - partialTicks + 1.0F;
	}
}