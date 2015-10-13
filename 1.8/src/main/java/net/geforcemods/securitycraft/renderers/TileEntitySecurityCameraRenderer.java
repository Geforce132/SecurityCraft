package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.models.ModelSecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySecurityCameraRenderer extends TileEntitySpecialRenderer {

	private final float CAMERA_ROTATION_SPEED = 0.0025F;
	
	private ModelSecurityCamera modelSecurityCamera;
	private ResourceLocation cameraTexture;

	public TileEntitySecurityCameraRenderer() {
		this.modelSecurityCamera = new ModelSecurityCamera();
		this.cameraTexture = new ResourceLocation("securitycraft:textures/blocks/cameraTexture.png");
	}

	public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float par5, int par6) {
		float rotation = 0F;
		
		if(par1TileEntity.hasWorldObj()){
			Tessellator tessellator = Tessellator.getInstance();
			float f = par1TileEntity.getWorld().getLightBrightness(par1TileEntity.getPos());
			int l = par1TileEntity.getWorld().getCombinedLight(par1TileEntity.getPos(), 0);
			int l1 = l % 65536;
			int l2 = l / 65536;
			tessellator.getWorldRenderer().setColorOpaque_F(f, f, f);

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);
		}
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(cameraTexture);
		
		GL11.glPushMatrix();
		
		if(par1TileEntity.hasWorldObj()){
			EnumFacing side = BlockUtils.getBlockPropertyAsEnum(getWorld(), par1TileEntity.getPos(), BlockSecurityCamera.FACING);
			if(side == EnumFacing.EAST){
				rotation = -1F;
			}else if(side == EnumFacing.SOUTH){
				rotation = -10000F; 
			}else if(side == EnumFacing.WEST){
				rotation = 1F; 
			}else if(side == EnumFacing.NORTH){
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
