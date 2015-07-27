package org.freeforums.geforce.securitycraft.renderers;

import org.freeforums.geforce.securitycraft.models.ModelIMS;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityIMS;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityIMSRenderer extends TileEntitySpecialRenderer {

	private ModelIMS imsModel;
    private ResourceLocation texture = new ResourceLocation("securitycraft:textures/blocks/ims.png");

	public TileEntityIMSRenderer(){
		this.imsModel = new ModelIMS();
	}
	
	public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float par5) {
		int bombsRemaining = (par1TileEntity != null && par1TileEntity.hasWorldObj()) ? ((TileEntityIMS) par1TileEntity).getBombsRemaining() : 4;
		float rotationX = 0F;
		float rotationY = 0F;
		float rotationZ = 1F;

		if(par1TileEntity.hasWorldObj()){
			Tessellator tessellator = Tessellator.instance;
			float f = par1TileEntity.getWorldObj().getLightBrightness(par1TileEntity.xCoord, par1TileEntity.yCoord, par1TileEntity.zCoord);
			int l = par1TileEntity.getWorldObj().getLightBrightnessForSkyBlocks(par1TileEntity.xCoord, par1TileEntity.yCoord, par1TileEntity.zCoord, 0);
			int l1 = l % 65536;
			int l2 = l / 65536;
			tessellator.setColorOpaque_F(f, f, f);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);
		}
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		GL11.glPushMatrix();
		
		GL11.glRotatef(180F, rotationX, rotationY, rotationZ);
		
		this.imsModel.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, bombsRemaining);
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

}
