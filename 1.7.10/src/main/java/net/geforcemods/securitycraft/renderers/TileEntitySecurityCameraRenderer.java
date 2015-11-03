package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelSecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntitySecurityCameraRenderer extends TileEntitySpecialRenderer {

	private ModelSecurityCamera modelSecurityCamera;
	private ResourceLocation cameraTexture = new ResourceLocation("securitycraft:textures/blocks/securityCamera.png");

	public TileEntitySecurityCameraRenderer() {
		this.modelSecurityCamera = new ModelSecurityCamera();
	}

	public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float par5) {
		int meta = par1TileEntity.hasWorldObj() ? par1TileEntity.getBlockMetadata() : par1TileEntity.blockMetadata;
		float rotation = 0F;
		
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
		
		Minecraft.getMinecraft().renderEngine.bindTexture(cameraTexture);
		
		GL11.glPushMatrix();
		
		if(par1TileEntity.hasWorldObj()){
			if(meta == 1 || meta == 5){
				rotation = -1F;
			}else if(meta == 2 || meta == 6){
				rotation = 1F;
			}else if(meta == 3 || meta == 7){
				rotation = -10000F; 
			}else if(meta == 4 || meta == 8){
				rotation = 0F;
			}
		}else{
			rotation = -10000F;
		}
		
		GL11.glRotatef(180F, rotation, 0.0F, 1.0F);
		
		this.modelSecurityCamera.cameraRotationPoint.rotateAngleY = ((TileEntitySecurityCamera) par1TileEntity).cameraRotation;
		
		this.modelSecurityCamera.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	

}
