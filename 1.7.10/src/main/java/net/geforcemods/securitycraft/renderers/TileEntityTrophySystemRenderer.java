package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelTrophySystem;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityTrophySystemRenderer extends TileEntitySpecialRenderer {

	private static final ModelTrophySystem trophySystemModel = new ModelTrophySystem();
	private static final ResourceLocation texture = new ResourceLocation("securitycraft:textures/blocks/trophySystem.png");

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		if(te.hasWorldObj()){
			Tessellator tessellator = Tessellator.instance;
			float brightness = te.getWorld().getLightBrightness(te.xCoord, te.yCoord, te.zCoord);
			int skyBrightness = te.getWorld().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
			int lightmapX = skyBrightness % 65536;
			int lightmapY = skyBrightness / 65536;
			tessellator.setColorOpaque_F(brightness, brightness, brightness);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
		}
		
		TileEntityTrophySystem tileEntityIn = ((TileEntityTrophySystem) te);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		GL11.glPushMatrix();

		GL11.glRotatef(180F, 0, 0.0F, 1.0F);

		trophySystemModel.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
		GL11.glPopMatrix();
		
		if(tileEntityIn.entityBeingTargeted == null) return;
		
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glTranslated(x, y, z);
		GL11.glLineWidth(2F);
		GL11.glColor4f(1, 0, 0, 0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorRGBA(255, 0, 0, 0);
		tess.addVertex(tileEntityIn.xCoord + 0.5D, tileEntityIn.yCoord + 0.5D, tileEntityIn.zCoord + 0.5D);
		tess.addVertex(tileEntityIn.entityBeingTargeted.posX - tileEntityIn.xCoord, tileEntityIn.entityBeingTargeted.posY - tileEntityIn.yCoord, tileEntityIn.entityBeingTargeted.posZ - tileEntityIn.zCoord);
		tess.draw();

		GL11.glEnd();
		GL11.glPopMatrix();
	}

}
