package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
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
	public void render(BouncingBettyEntity entity, float p_225623_2_, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		int overlayTextureToUse;

		matrix.push();
		matrix.translate(0.0D, 0.5D, 0.0D);

		if (entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			float alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			matrix.scale(scale, scale, scale);
		}

		if (entity.fuse / 5 % 2 == 0)
			overlayTextureToUse = OverlayTexture.packLight(OverlayTexture.lightToInt(1.0F), 10);
		else
			overlayTextureToUse = OverlayTexture.DEFAULT_LIGHT;

		matrix.rotate(Vector3f.YP.rotationDegrees(-90.0F));
		matrix.translate(-0.5D, -0.5D, 0.5D);
		matrix.rotate(Vector3f.YP.rotationDegrees(90.0F));
		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(SCContent.bouncingBetty.getDefaultState(), matrix, buffer, p_225623_6_, overlayTextureToUse, EmptyModelData.INSTANCE);
		matrix.pop();
		super.render(entity, p_225623_2_, partialTicks, matrix, buffer, p_225623_6_);
	}

	@Override
	public ResourceLocation getEntityTexture(BouncingBettyEntity entity)
	{
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}