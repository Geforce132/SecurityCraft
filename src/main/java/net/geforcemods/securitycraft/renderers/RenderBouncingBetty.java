package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderBouncingBetty extends Render
{
	private RenderBlocks blockRenderer = new RenderBlocks();

	public RenderBouncingBetty()
	{
		shadowSize = 0.5F;
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		renderBouncingBetty((EntityBouncingBetty)entity, x, y, z, entityYaw, partialTicks);
	}

	public void renderBouncingBetty(EntityBouncingBetty bouncingBetty, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
		float alpha;

		if (bouncingBetty.fuse - partialTicks + 1.0F < 10.0F)
		{
			alpha = 1.0F - (bouncingBetty.fuse - partialTicks + 1.0F) / 10.0F;

			if (alpha < 0.0F)
				alpha = 0.0F;

			if (alpha > 1.0F)
				alpha = 1.0F;

			alpha *= alpha;
			alpha *= alpha;
			float scaleFactor = 1.0F + alpha * 0.3F;
			GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
		}

		alpha = (1.0F - (bouncingBetty.fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;
		bindEntityTexture(bouncingBetty);
		blockRenderer.renderBlockAsItem(SCContent.bouncingBetty, 0, bouncingBetty.getBrightness(partialTicks));

		if (bouncingBetty.fuse / 5 % 2 == 0)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
			blockRenderer.renderBlockAsItem(SCContent.bouncingBetty, 0, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		GL11.glPopMatrix();
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return TextureMap.locationBlocksTexture;
	}
}
