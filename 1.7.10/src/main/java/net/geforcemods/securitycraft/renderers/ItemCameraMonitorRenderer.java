package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CameraView;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

/**
 * The custom IItemRenderer for the handheld camera monitors.
 *
 * @author Geforce
 */
public class ItemCameraMonitorRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return type == ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if(type == ItemRenderType.FIRST_PERSON_MAP){

			//Draw the base monitor texture.
			((TextureManager) data[1]).bindTexture(new ResourceLocation("securitycraft:textures/gui/camera/cameraBackground.png"));
			Tessellator tessellator = Tessellator.instance;

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(0 - 7, 128 + 7, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(128 + 7, 128 + 7, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(128 + 7, 0 - 7, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(0 - 7, 0 - 7, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			//

			if(item != null && item.getItem() instanceof ItemCameraMonitor && ((ItemCameraMonitor) item.getItem()).hasCameraAdded(item.getTagCompound())){
				CameraView view = ((ItemCameraMonitor) item.getItem()).getCameraView(item.getTagCompound());

				if(mod_SecurityCraft.instance.hasViewForCoords(view.toNBTString())){
					IWorldView worldView = mod_SecurityCraft.instance.getViewFromCoords(view.toNBTString()).getView();

					if(worldView.isReady() && worldView.getTexture() != 0){
						//Bind the IWorldView texture then draw it.
						GL11.glDisable(3008);
						GL11.glDisable(2896);

						GL11.glBindTexture(GL11.GL_TEXTURE_2D, worldView.getTexture());

						tessellator.startDrawingQuads();
						tessellator.addVertexWithUV(128 + 7, 0 - 7, 0.0D, 0.0D, 1.0D);
						tessellator.addVertexWithUV(0 - 7, 0 - 7, 0.0D, -1.0D, 1.0D);
						tessellator.addVertexWithUV(0 - 7, 128 + 7, 0.0D, -1.0D, 0.0D);
						tessellator.addVertexWithUV(128 + 7, 128 + 7, 0.0D, 0.0D, 0.0D);

						tessellator.draw();

						GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

						GL11.glEnable(3008);
						GL11.glEnable(2896);
						//
					}

					worldView.markDirty(); //Update the camera.
				}
			}
		}
	}

}
