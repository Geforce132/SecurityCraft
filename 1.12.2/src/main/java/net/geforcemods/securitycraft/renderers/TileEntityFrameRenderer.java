package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.EnumSkyBlock;

public class TileEntityFrameRenderer extends TileEntitySpecialRenderer<TileEntityFrame> {

	@Override
	public void render(TileEntityFrame te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		IWorldView lgView = null;

		if(te.hasWorld() && te.hasCameraLocation() && SecurityCraft.instance.hasViewForCoords(te.getCameraView().toNBTString()) && te.shouldShowView())
			lgView = SecurityCraft.instance.getViewFromCoords(te.getCameraView().toNBTString()).getView();
		else return;

		int meta = te.hasWorld() ? te.getBlockMetadata() : te.getBlockMetadata();
		float rotation = 0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		if(te.hasWorld())
		{
			//			float brightness = te.getWorld().getLightBrightness(te.getPos());
			int skyBrightness = te.getWorld().getLightFor(EnumSkyBlock.SKY, te.getPos());
			int l1 = skyBrightness % 65536;
			int l2 = skyBrightness / 65536;
			//			tessellator.setColorOpaque_F(brightness, brightness, brightness);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
		GlStateManager.pushMatrix();

		if(te.hasWorld())
		{
			System.out.println(meta);
			if(meta == 2)
				rotation = 0F;
			else if(meta == 4)
				rotation = 1F;
			else if(meta == 3)
				rotation = -10000F;
			else if(meta == 5)
				rotation = -1F;
		}
		else
			rotation = -1F;

		GlStateManager.rotate(180F, rotation, 0.0F, 1.0F);

		if(lgView != null)
		{
			if(lgView.getTexture() != 0)
			{
				GlStateManager.translate(0.625F, 0.375F, -0.475F);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.disableAlpha();
				GlStateManager.disableLighting();
				GlStateManager.bindTexture(lgView.getTexture());

				bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				bufferBuilder.pos(0.25, 1, 0).tex(1, 0).endVertex();
				bufferBuilder.pos(0.25, 0.25, 0).tex(1, 1).endVertex();
				bufferBuilder.pos(1, 0.25, 0).tex(0, 1).endVertex();
				bufferBuilder.pos(1, 1, 0).tex(0, 0).endVertex();
				tessellator.draw();

				GlStateManager.bindTexture(0);
				GlStateManager.enableAlpha();
				GlStateManager.enableLighting();
			}

			lgView.markDirty();
		}

		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}
}
