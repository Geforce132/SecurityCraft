package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.models.ModelFrame;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityFrameRenderer extends TileEntitySpecialRenderer {

	private static final ModelFrame model = new ModelFrame();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/blocks/frame.png");

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		int meta = te.hasWorldObj() ? te.getBlockMetadata() : te.blockMetadata;
		IWorldView lgView = null;
		float rotation = 0F;
		Tessellator tessellator = Tessellator.instance;

		if(te.hasWorldObj()){
			float brightness = te.getWorld().getLightBrightness(te.xCoord, te.yCoord, te.zCoord);
			int skyBrightness = te.getWorld().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
			int lightmapX = skyBrightness % 65536;
			int lightmapY = skyBrightness / 65536;
			tessellator.setColorOpaque_F(brightness, brightness, brightness);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
		}

		if(te.hasWorldObj() && lgView == null && ((TileEntityFrame) te).hasCameraLocation() && SecurityCraft.instance.hasViewForCoords(((TileEntityFrame) te).getCameraView().toNBTString()) && ((TileEntityFrame) te).shouldShowView())
			lgView = SecurityCraft.instance.getViewFromCoords(((TileEntityFrame) te).getCameraView().toNBTString()).getView();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

		GL11.glPushMatrix();

		if(te.hasWorldObj()){
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

		GL11.glRotatef(180F, rotation, 0.0F, 1.0F);

		model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		if(lgView != null){
			if(lgView.getTexture() != 0){
				GL11.glTranslatef(0.625F, 0.375F, -0.475F);
				GL11.glRotated(180D, 0D, 1D, 0D);

				GL11.glDisable(3008);
				GL11.glDisable(2896);

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, lgView.getTexture());

				tessellator.startDrawingQuads();

				tessellator.addVertexWithUV(0.25, 1, 0, 1, 0);
				tessellator.addVertexWithUV(0.25, 0.25, 0, 1, 1);
				tessellator.addVertexWithUV(1, 0.25, 0, 0, 1);
				tessellator.addVertexWithUV(1, 1, 0, 0, 0);

				tessellator.draw();

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

				GL11.glEnable(3008);
				GL11.glEnable(2896);
			}

			lgView.markDirty();
		}

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
