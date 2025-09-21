package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.models.SentryModel;
import net.geforcemods.securitycraft.renderers.state.SentryRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SentryRenderer extends EntityRenderer<Sentry, SentryRenderState> {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/entity/sentry.png");
	private final SentryModel model;

	public SentryRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);

		model = new SentryModel(ctx.bakeLayer(ClientHandler.SENTRY_LOCATION));
	}

	@Override
	public void submit(SentryRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		RenderType renderType = RenderType.entitySolid(TEXTURE);
		float scale = state.scale;

		pose.pushPose();
		pose.scale(scale, scale, scale);
		pose.translate(0.0D, 1.5D, 0.0D);
		pose.scale(-1, -1, 1); //rotate model rightside up
		model.submitBase(state, pose, collector, renderType);
		pose.mulPose(Axis.YP.rotationDegrees(state.headRotation));
		pose.translate(0.0F, state.headY, 0.0F);
		model.submitHead(state, pose, collector, renderType);
		pose.popPose();
	}

	@Override
	public SentryRenderState createRenderState() {
		return new SentryRenderState();
	}

	@Override
	public void extractRenderState(Sentry sentry, SentryRenderState state, float partialTicks) {
		super.extractRenderState(sentry, state, partialTicks);
		state.headRotation = Mth.lerp(partialTicks, sentry.getOriginalHeadRotation(), sentry.getHeadRotation());
		state.headY = sentry.getHeadYTranslation(partialTicks);
	}
}
