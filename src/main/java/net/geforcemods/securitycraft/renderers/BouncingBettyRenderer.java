package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BouncingBettyRenderer extends EntityRenderer<BouncingBettyEntity> {

	public BouncingBettyRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
		shadowSize = 0.5F;
	}

	@Override
	public void func_225623_a_(BouncingBettyEntity entity, float p_225623_2_, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		float x = entity.getPosition().getX();
		float y = entity.getPosition().getY();
		float z = entity.getPosition().getZ();
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		RenderSystem.pushMatrix();
		RenderSystem.translatef(x, y + 0.5F, z);
		float alpha;

		if (entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			RenderSystem.scalef(scale, scale, scale);
		}

		alpha = (1.0F - (entity.fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;
		bindEntityTexture(entity);
		RenderSystem.translatef(-0.5F, -0.5F, 0.5F);
		blockrendererdispatcher.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), entity.getBrightness());
		RenderSystem.translatef(0.0F, 0.0F, 1.0F);

		if (entity.fuse / 5 % 2 == 0)
		{
			RenderSystem.disableTexture();
			RenderSystem.disableLighting();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(770, 772);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
			RenderSystem.enablePolygonOffset();
			RenderSystem.polygonOffset(-3.0F, -3.0F);
			blockrendererdispatcher.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), 1.0F);
			RenderSystem.polygonOffset(0.0F, 0.0F);
			RenderSystem.disablePolygonOffset();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.disableBlend();
			RenderSystem.enableLighting();
			RenderSystem.enableTexture();
		}

		RenderSystem.popMatrix();
		super.func_225623_a_(entity, p_225623_2_, partialTicks, stack, buffer, p_225623_6_);
	}

	@Override
	public ResourceLocation getEntityTexture(BouncingBettyEntity entity)
	{
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}