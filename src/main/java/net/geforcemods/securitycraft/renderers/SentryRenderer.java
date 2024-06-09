package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
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

public class SentryRenderer extends EntityRenderer<Sentry> {
	private static final ResourceLocation TEXTURE = SecurityCraft.mcResLoc(SecurityCraft.MODID + ":textures/entity/sentry.png");
	private final SentryModel model;

	public SentryRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);

		model = new SentryModel(ctx.bakeLayer(ClientHandler.SENTRY_LOCATION));
	}

	@Override
	public void render(Sentry entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
		VertexConsumer builder = buffer.getBuffer(RenderType.entitySolid(getTextureLocation(entity)));
		float scale = entity.getScale();

		pose.pushPose();
		pose.scale(scale, scale, scale);
		pose.translate(0.0D, 1.5D, 0.0D);
		pose.scale(-1, -1, 1); //rotate model rightside up
		RenderSystem._setShaderTexture(0, getTextureLocation(entity));
		model.renderBase(pose, builder, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.getOriginalHeadRotation(), entity.getHeadRotation())));
		pose.translate(0.0F, entity.getHeadYTranslation(partialTicks), 0.0F);
		model.renderToBuffer(pose, builder, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(Sentry entity) {
		return TEXTURE;
	}
}
