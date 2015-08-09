package org.freeforums.geforce.securitycraft.renderers;

import org.freeforums.geforce.securitycraft.models.ModelKeyPanel;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeyPanel;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityKeyPanelRenderer extends TileEntitySpecialRenderer
{
	private ResourceLocation texture = new ResourceLocation("securitycraft:textures/blocks/keyPanel.png");
	private EntityItem item = null;
	
	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double x, double y, double z, float par5)
	{
		ModelKeyPanel model = new ModelKeyPanel();
		int slot = 1;
		TileEntityKeyPanel te = (TileEntityKeyPanel)par1TileEntity;

		if(te.hasWorldObj())
		{
			Tessellator tessellator = Tessellator.instance;
			float f = te.getWorldObj().getLightBrightness(te.xCoord, te.yCoord, te.zCoord);
			int l = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
			int l1 = l % 65536;
			int l2 = l / 65536;
			
			tessellator.setColorOpaque_F(f, f, f);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);
		}
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F); //centering the model around the block
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); //rotating the model by 180 degrees
		model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();

		if(te.hasWorldObj()) //don't render anything in the inventory
		{
			try
			{
				if(item == null || item.getEntityItem().getItem() != te.getStackInSlot(slot).getItem())
					item = new EntityItem(te.getWorldObj(), x, y, z, te.getStackInSlot(slot));

				GL11.glPushMatrix();
				item.hoverStart = 0.0F;
				RenderItem.renderInFrame = true;
				GL11.glTranslatef((float) x + 0.4F, (float) y + 0.175F, (float) z + 0.2F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 1.0F);
				RenderManager.instance.renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
				RenderItem.renderInFrame = false;
				GL11.glPopMatrix();
			}
			catch(NullPointerException e){/*catch so the client doesn't crash when no stack is in the slot >.<*/}
		}
	}
}
