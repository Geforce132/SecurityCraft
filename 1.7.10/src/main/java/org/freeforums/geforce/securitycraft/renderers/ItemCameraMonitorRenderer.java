package org.freeforums.geforce.securitycraft.renderers;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.freeforums.geforce.securitycraft.items.ItemCameraMonitor;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.lookingglass.api.view.IWorldView;

/**
 * The custom IItemRenderer for the handheld camera monitors.
 * 
 * @author Geforce
 */
public class ItemCameraMonitorRenderer implements IItemRenderer {
	
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.FIRST_PERSON_MAP;
	}

	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return type == ItemRenderType.FIRST_PERSON_MAP;
	}

	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if(type == ItemRenderType.FIRST_PERSON_MAP){
			
			//Draw the base monitor texture.
			((TextureManager) data[1]).bindTexture(new ResourceLocation("securitycraft:textures/gui/camera/cameraBackground.png"));
			Tessellator tessellator = Tessellator.instance;
			
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((double)(0 - 7), (double)(128 + 7), 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)(128 + 7), (double)(128 + 7), 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)(128 + 7), (double)(0 - 7), 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)(0 - 7), (double)(0 - 7), 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            //
            
			if(item != null && item.getItem() instanceof ItemCameraMonitor && ((ItemCameraMonitor) item.getItem()).hasCameraAdded(item.getTagCompound())){
				int camX = ((ItemCameraMonitor) item.getItem()).getCameraCoordinates(item.getTagCompound())[0];
				int camY = ((ItemCameraMonitor) item.getItem()).getCameraCoordinates(item.getTagCompound())[1];
				int camZ = ((ItemCameraMonitor) item.getItem()).getCameraCoordinates(item.getTagCompound())[2];

				if(mod_SecurityCraft.instance.hasViewForCoords(camX + " " + camY + " " + camZ)){
					IWorldView view = mod_SecurityCraft.instance.getViewFromCoords(camX + " " + camY + " " + camZ).getView();
					
				    if(view.isReady() && view.getTexture() != 0){
				    	//Bind the IWorldView texture then draw it.
				    	GL11.glDisable(3008);
				        GL11.glDisable(2896);

						GL11.glBindTexture(GL11.GL_TEXTURE_2D, view.getTexture()); 

						tessellator.startDrawingQuads();
						tessellator.addVertexWithUV((double)(128 + 7), (double)(0 - 7), 0.0D, 0.0D, 1.0D);
						tessellator.addVertexWithUV((double)(0 - 7), (double)(0 - 7), 0.0D, -1.0D, 1.0D);	  
						tessellator.addVertexWithUV((double)(0 - 7), (double)(128 + 7), 0.0D, -1.0D, 0.0D);
						tessellator.addVertexWithUV((double)(128 + 7), (double)(128 + 7), 0.0D, 0.0D, 0.0D);	  

						tessellator.draw();
						
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0); 

						GL11.glEnable(3008);
				        GL11.glEnable(2896);
						//
					}
					
					view.markDirty(); //Update the camera.
				}
			}	
		}
	}

}
