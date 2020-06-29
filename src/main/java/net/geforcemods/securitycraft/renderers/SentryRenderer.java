package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.models.SentryModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SentryRenderer extends EntityRenderer<SentryEntity>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/sentry.png");
	private static final SentryModel MODEL = new SentryModel();

	public SentryRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void render(SentryEntity entity, float partialTicks, float p_225623_3_, MatrixStack stack, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		IVertexBuilder builder = buffer.getBuffer(RenderType.getEntitySolid(getEntityTexture(entity)));

		stack.translate(0.0D, 1.5D, 0.0D);
		stack.scale(-1, -1, 1); //rotate model rightside up
		Minecraft.getInstance().textureManager.bindTexture(getEntityTexture(entity));
		MODEL.renderBase(stack, builder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		stack.rotate(Vector3f.YP.rotationDegrees(entity.getDataManager().get(SentryEntity.HEAD_ROTATION)));
		stack.translate(0.0F, entity.getHeadYTranslation(), 0.0F);
		MODEL.render(stack, builder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public ResourceLocation getEntityTexture(SentryEntity entity)
	{
		return TEXTURE;
	}
}
