package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.models.ModelKeypadFrame;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.lookingglass.api.view.IWorldView;

public class TileEntityFrameRenderer extends TileEntitySpecialRenderer {
	
	private ModelKeypadFrame keypadFrameModel;
	private ResourceLocation frameTexture;

	public TileEntityFrameRenderer() {
		this.keypadFrameModel = new ModelKeypadFrame();
		this.frameTexture = new ResourceLocation("textures/blocks/iron_block.png");
	}

	public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float par5) {
		int meta = par1TileEntity.hasWorldObj() ? par1TileEntity.getBlockMetadata() : par1TileEntity.blockMetadata;
		IWorldView lgView = null;
		float rotation = 0F;
		Tessellator tessellator = Tessellator.instance;

		if(par1TileEntity.hasWorldObj()){
			float f = par1TileEntity.getWorldObj().getLightBrightness(par1TileEntity.xCoord, par1TileEntity.yCoord, par1TileEntity.zCoord);
			int l = par1TileEntity.getWorldObj().getLightBrightnessForSkyBlocks(par1TileEntity.xCoord, par1TileEntity.yCoord, par1TileEntity.zCoord, 0);
			int l1 = l % 65536;
			int l2 = l / 65536;
			tessellator.setColorOpaque_F(f, f, f);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);
		}
		
		if(par1TileEntity.hasWorldObj() && lgView == null && mod_SecurityCraft.instance.hasViewForCoords(((TileEntityFrame) par1TileEntity).getCamX() + " " + ((TileEntityFrame) par1TileEntity).getCamY() + " " + ((TileEntityFrame) par1TileEntity).getCamZ())){
			lgView = mod_SecurityCraft.instance.getViewFromCoords(((TileEntityFrame) par1TileEntity).getCamX() + " " + ((TileEntityFrame) par1TileEntity).getCamY() + " " + ((TileEntityFrame) par1TileEntity).getCamZ()).getView();
		}
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(frameTexture);
		
		GL11.glPushMatrix();
		
		if(par1TileEntity.hasWorldObj()){
			if(meta == 2){
				rotation = 0F;
			}else if(meta == 4){
				rotation = 1F;
			}else if(meta == 3){
				rotation = -10000F; 
			}else if(meta == 5){
				rotation = -1F;
			}
		}else{
			rotation = -1F;
		}
		
		GL11.glRotatef(180F, rotation, 0.0F, 1.0F);
		
		this.keypadFrameModel.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		
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
