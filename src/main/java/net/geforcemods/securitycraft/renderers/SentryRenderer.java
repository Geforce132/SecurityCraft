package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.models.SentryModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
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
	public void func_225623_a_(SentryEntity entity, float partialTicks, float p_225623_3_, MatrixStack stack, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		IVertexBuilder builder = buffer.getBuffer(RenderType.func_228634_a_(getEntityTexture(entity)));

		stack.func_227861_a_(0.0D, 1.5D, 0.0D); //translate
		stack.func_227862_a_(-1, -1, 1); //rotate model rightside up (scale)
		Minecraft.getInstance().textureManager.bindTexture(getEntityTexture(entity));
		MODEL.renderBase(stack, builder, p_225623_6_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
		stack.func_227863_a_(new Quaternion(Vector3f.field_229181_d_, entity.getDataManager().get(SentryEntity.HEAD_ROTATION), true)); //rotate, Y_AXIS
		stack.func_227861_a_(0.0F, entity.getHeadYTranslation(), 0.0F);
		MODEL.func_225598_a_(stack, builder, p_225623_6_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.popMatrix();
	}

	@Override
	public ResourceLocation getEntityTexture(SentryEntity entity)
	{
		return TEXTURE;
	}
}
