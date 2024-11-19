package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBetty;
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
public class BouncingBettyRenderer extends Render<BouncingBetty> {
	public BouncingBettyRenderer(RenderManager renderManager) {
		super(renderManager);
		shadowSize = 0.5F;
	}

	@Override
	public void doRender(BouncingBetty entity, double x, double y, double z, float entityYaw, float partialTicks) {
		BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
		int fuse = entity.getFuse();

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y + 0.5F, (float) z);

		if (fuse - partialTicks + 1.0F < 10.0F) {
			float scale = 1.0F - (fuse - partialTicks + 1.0F) / 10.0F;

			scale = MathHelper.clamp(scale, 0.0F, 1.0F);
			scale = scale * scale;
			scale = scale * scale;
			scale = 1.0F + scale * 0.3F;
			GlStateManager.scale(scale, scale, scale);
		}

		bindEntityTexture(entity);
		GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, 0.5F);
		brd.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), entity.getBrightness());
		GlStateManager.translate(0.0F, 0.0F, 1.0F);

		if (renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(getTeamColor(entity));
			brd.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), 1.0F);
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}
		else if (fuse / 5 % 2 == 0) {
			float alpha = (1.0F - (fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;

			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
			GlStateManager.doPolygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			brd.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), 1.0F);
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
	protected ResourceLocation getEntityTexture(BouncingBetty entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}