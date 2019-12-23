package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class BouncingBettyRenderer extends EntityRenderer<BouncingBettyEntity> {

	public BouncingBettyRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
		shadowSize = 0.5F;
	}

	@Override
	public void func_225623_a_(BouncingBettyEntity entity, float p_225623_2_, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		double x = entity.getPosition().getX();
		double y = entity.getPosition().getY();
		double z = entity.getPosition().getZ();
		int overlayTextureToUse;

		matrix.func_227860_a_(); //push
		matrix.func_227861_a_(x, y + 0.5D, z); //translate

		if (entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			float alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			matrix.func_227862_a_(scale, scale, scale); //scale
		}

		if (entity.fuse / 5 % 2 == 0)
			overlayTextureToUse = OverlayTexture.func_229201_a_(OverlayTexture.func_229199_a_(1.0F), 10);
		else
			overlayTextureToUse = OverlayTexture.field_229196_a_;

		matrix.func_227861_a_(-0.5D, -0.5D, 0.5D); //translate
		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(SCContent.bouncingBetty.getDefaultState(), matrix, buffer, p_225623_6_, overlayTextureToUse, EmptyModelData.INSTANCE);
		matrix.func_227865_b_(); //pop
		super.func_225623_a_(entity, p_225623_2_, partialTicks, matrix, buffer, p_225623_6_);
	}

	@Override
	public ResourceLocation getEntityTexture(BouncingBettyEntity entity)
	{
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}