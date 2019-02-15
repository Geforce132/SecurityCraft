package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBouncingBetty extends Render<EntityBouncingBetty> {

	public RenderBouncingBetty(RenderManager renderManager)
	{
		super(renderManager);
		shadowSize = 0.5F;
	}

	@Override
	public void doRender(EntityBouncingBetty entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y + 0.5F, (float)z);
		float alpha;

		if (entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			GlStateManager.scale(scale, scale, scale);
		}

		alpha = (1.0F - (entity.fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;
		bindEntityTexture(entity);
		GlStateManager.translate(-0.5F, -0.5F, 0.5F);
		blockrendererdispatcher.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), entity.getBrightness());
		GlStateManager.translate(0.0F, 0.0F, 1.0F);

		if (entity.fuse / 5 % 2 == 0)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 772);
			GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
			GlStateManager.doPolygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			blockrendererdispatcher.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), 1.0F);
			GlStateManager.doPolygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
		}

		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBouncingBetty entity)
	{
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}