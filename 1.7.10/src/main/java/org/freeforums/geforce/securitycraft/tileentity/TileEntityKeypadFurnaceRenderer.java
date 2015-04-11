package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.freeforums.geforce.securitycraft.models.ModelKeypadFurnaceActive;
import org.freeforums.geforce.securitycraft.models.ModelKeypadFurnaceDeactivated;
import org.lwjgl.opengl.GL11;

public class TileEntityKeypadFurnaceRenderer extends TileEntitySpecialRenderer {

	public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float par5) {
		
		boolean isActive = false;
		ModelKeypadFurnaceDeactivated modelDeactivated = new ModelKeypadFurnaceDeactivated();
		ModelKeypadFurnaceActive modelActive = new ModelKeypadFurnaceActive();

		if(par1TileEntity.hasWorldObj() && par1TileEntity.getBlockMetadata() >= 5){
			isActive = true;
		}
		
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
		
		if(isActive){
			ResourceLocation texture = new ResourceLocation("securitycraft:textures/blocks/keypadFurnaceActive.png");
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		}else{
			ResourceLocation texture = new ResourceLocation("securitycraft:textures/blocks/keypadFurnaceDeactivated.png");
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		}
		
		GL11.glPushMatrix();

		if(meta == 1 || meta == 6){
			rotation = 0F;
		}else if(meta == 2 || meta == 7){
			rotation = 1F;
		}else if(meta == 3 || meta == 8){
			rotation = -10000F; 
		}else if(meta == 4 || meta == 9){
			rotation = -1F;
		}
		
		GL11.glRotatef(180F, rotation, 0.0F, 1.0F);
		
		if(isActive){
			modelActive.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		}else{
			modelDeactivated.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		}
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	
}
