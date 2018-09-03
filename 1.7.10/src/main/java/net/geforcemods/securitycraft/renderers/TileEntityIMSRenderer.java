package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityIMSRenderer extends TileEntitySpecialRenderer {

	private static final ModelIMS model = new ModelIMS();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/blocks/ims.png");

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		int bombsRemaining = (te != null && te.hasWorldObj()) ? ((TileEntityIMS) te).getBombsRemaining() : 4;
		float rotationX = 0F;
		float rotationY = 0F;
		float rotationZ = 1F;

		if(te.hasWorldObj()){
			Tessellator tessellator = Tessellator.instance;
			float brightness = te.getWorld().getLightBrightness(te.xCoord, te.yCoord, te.zCoord);
			int skyBrightness = te.getWorld().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
			int l1 = skyBrightness % 65536;
			int l2 = skyBrightness / 65536;
			tessellator.setColorOpaque_F(brightness, brightness, brightness);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

		GL11.glPushMatrix();

		GL11.glRotatef(180F, rotationX, rotationY, rotationZ);

		model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, bombsRemaining);

		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

}
