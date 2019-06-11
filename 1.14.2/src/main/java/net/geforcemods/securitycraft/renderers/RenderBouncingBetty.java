package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBouncingBetty extends EntityRenderer<EntityBouncingBetty> {

	public RenderBouncingBetty(EntityRendererManager renderManager)
	{
		super(renderManager);
		shadowSize = 0.5F;
	}

	@Override
	public void doRender(EntityBouncingBetty entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)x, (float)y + 0.5F, (float)z);
		float alpha;

		if (entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			GlStateManager.scalef(scale, scale, scale);
		}

		alpha = (1.0F - (entity.fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;
		bindEntityTexture(entity);
		GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
		blockrendererdispatcher.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), entity.getBrightness());
		GlStateManager.translatef(0.0F, 0.0F, 1.0F);

		if (entity.fuse / 5 % 2 == 0)
		{
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 772);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, alpha);
			GlStateManager.enablePolygonOffset();
			GlStateManager.polygonOffset(-3.0F, -3.0F);
			blockrendererdispatcher.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), 1.0F);
			GlStateManager.polygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
		}

		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBouncingBetty entity)
	{
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}