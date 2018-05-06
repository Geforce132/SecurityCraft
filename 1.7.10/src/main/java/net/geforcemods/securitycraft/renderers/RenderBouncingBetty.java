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

	public void renderBouncingBetty(EntityBouncingBetty bouncingBetty, double par2, double par4, double par6, float par8, float par9) // i don't actually know
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		float f2;

		if (bouncingBetty.fuse - par9 + 1.0F < 10.0F)
		{
			f2 = 1.0F - (bouncingBetty.fuse - par9 + 1.0F) / 10.0F;

			if (f2 < 0.0F)
				f2 = 0.0F;

			if (f2 > 1.0F)
				f2 = 1.0F;

			f2 *= f2;
			f2 *= f2;
			float f3 = 1.0F + f2 * 0.3F;
			GL11.glScalef(f3, f3, f3);
		}

		f2 = (1.0F - (bouncingBetty.fuse - par9 + 1.0F) / 100.0F) * 0.8F;
		bindEntityTexture(bouncingBetty);
		blockRenderer.renderBlockAsItem(SCContent.bouncingBetty, 0, bouncingBetty.getBrightness(par9));

		if (bouncingBetty.fuse / 5 % 2 == 0)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, f2);
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

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9)
	{
		renderBouncingBetty((EntityBouncingBetty)entity, par2, par4, par6, par8, par9);
	}
}
