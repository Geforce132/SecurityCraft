package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.models.SentryModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
	public void render(SentryRenderState state, PoseStack pose, MultiBufferSource buffer, int packedLight) {
		VertexConsumer builder = buffer.getBuffer(RenderType.entitySolid(TEXTURE));
		float scale = state.scale;

		pose.pushPose();
		pose.scale(scale, scale, scale);
		pose.translate(0.0D, 1.5D, 0.0D);
		pose.scale(-1, -1, 1); //rotate model rightside up
		model.renderBase(pose, builder, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		pose.mulPose(Axis.YP.rotationDegrees(state.headRotation));
		pose.translate(0.0F, state.headY, 0.0F);
		model.renderHead(pose, builder, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
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
