package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
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
public class BouncingBettyRenderer extends EntityRenderer<BouncingBettyEntity> {

	public BouncingBettyRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
		shadowSize = 0.5F;
	}

	@Override
	public void doRender(BouncingBettyEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)x, (float)y + 0.5F, (float)z);

		if(entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			float alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			GlStateManager.scalef(scale, scale, scale);
		}

		bindEntityTexture(entity);
		GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
		blockrendererdispatcher.renderBlockBrightness(SCContent.bouncingBetty.getDefaultState(), entity.getBrightness());
		GlStateManager.translatef(0.0F, 0.0F, 1.0F);
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(BouncingBettyEntity entity)
	{
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}