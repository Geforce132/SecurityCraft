package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.models.SentryModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SentryRenderer extends EntityRenderer<SentryEntity>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/sentry.png");
	private final SentryModel model;

	public SentryRenderer(EntityRendererProvider.Context ctx)
	{
		super(ctx);

		model = new SentryModel(ctx.bakeLayer(ClientHandler.SENTRY_LOCATION));
	}

	@Override
	public void render(SentryEntity entity, float partialTicks, float p_225623_3_, PoseStack stack, MultiBufferSource buffer, int p_225623_6_)
	{
		VertexConsumer builder = buffer.getBuffer(RenderType.entitySolid(getTextureLocation(entity)));

		stack.translate(0.0D, 1.5D, 0.0D);
		stack.scale(-1, -1, 1); //rotate model rightside up
		RenderSystem._setShaderTexture(0, getTextureLocation(entity));
		model.renderBase(stack, builder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		stack.mulPose(Vector3f.YP.rotationDegrees(entity.getEntityData().get(SentryEntity.HEAD_ROTATION)));
		stack.translate(0.0F, entity.getHeadYTranslation(), 0.0F);
		model.renderToBuffer(stack, builder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(SentryEntity entity)
	{
		return TEXTURE;
	}
}
