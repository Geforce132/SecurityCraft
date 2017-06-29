package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.models.ModelSecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySecurityCameraRenderer extends TileEntitySpecialRenderer<TileEntitySecurityCamera> {
	
	private ModelSecurityCamera modelSecurityCamera;
	private ResourceLocation cameraTexture = new ResourceLocation("securitycraft:textures/blocks/security_camera1.png");

	public TileEntitySecurityCameraRenderer() {
		this.modelSecurityCamera = new ModelSecurityCamera();
	}

	@Override
	public void renderTileEntityAt(TileEntitySecurityCamera par1TileEntity, double x, double y, double z, float par5, int par6) {
		float rotation = 0F;
		
		if(par1TileEntity.hasWorld()){
			Tessellator tessellator = Tessellator.getInstance();
			float f = par1TileEntity.getWorld().getLightBrightness(par1TileEntity.getPos());
			int l = par1TileEntity.getWorld().getCombinedLight(par1TileEntity.getPos(), 0);
			int l1 = l % 65536;
			int l2 = l / 65536;
			tessellator.getBuffer().putColorRGBA(0, (int)(f * 255.0F), (int)(f * 255.0F), (int)(f * 255.0F), 255); //TODO: does this work?

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
		}
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(cameraTexture);
		
		GL11.glPushMatrix();
		
		if(par1TileEntity.hasWorld() && BlockUtils.getBlock(par1TileEntity.getWorld(), par1TileEntity.getPos()) == mod_SecurityCraft.securityCamera){
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
		
		this.modelSecurityCamera.cameraRotationPoint.rotateAngleY = par1TileEntity.cameraRotation;
		
		this.modelSecurityCamera.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	

}
