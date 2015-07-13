package org.freeforums.geforce.securitycraft.gui;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiUtils extends Gui{
	
	public static ResourceLocation potionIcons = new ResourceLocation("minecraft:textures/gui/container/inventory.png");

	private static RenderItem itemRender = new RenderItem();

	public static void drawTooltip(List list, int x, int y, FontRenderer fontRenderer){
		if (!list.isEmpty()){
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int k = 0;
			Iterator iterator = list.iterator();

			while (iterator.hasNext())
			{
				String s = (String)iterator.next();
				int l = fontRenderer.getStringWidth(s);

				if (l > k)
				{
					k = l;
				}
			}

			int j2 = x + 12;
			int k2 = y - 12;
			int i1 = 8;

			if (list.size() > 1)
			{
				i1 += 2 + (list.size() - 1) * 10;
			}

			if (j2 + k > Minecraft.getMinecraft().displayWidth) //w
			{
				j2 -= 28 + k;
			}

			if (k2 + i1 + 6 > Minecraft.getMinecraft().displayHeight) //h
			{
				k2 = Minecraft.getMinecraft().displayHeight - i1 - 6; //h
			}

			itemRender.zLevel = 300.0F;
			int j1 = -267386864;
			drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1, 300.0F);
			drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1, 300.0F);
			drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1, 300.0F);
			drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1, 300.0F);
			drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1, 300.0F);
			int k1 = 1347420415;
			int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
			drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1, 300.0F);
			drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1, 300.0F);
			drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1, 300.0F);
			drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1, 300.0F);

			for (int i2 = 0; i2 < list.size(); ++i2)
			{
				String s1 = (String)list.get(i2);
				fontRenderer.drawStringWithShadow(s1, j2, k2, -1);

				if (i2 == 0)
				{
					k2 += 2;
				}

				k2 += 10;
			}

			itemRender.zLevel = 0.0F;
			//GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			//RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}
	
	public static void drawItemStackToGui(Minecraft mc, Item item, int x, int y, boolean fixLighting){
		if(fixLighting){
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(item), x, y);
        
        if(fixLighting){
        	GL11.glDisable(GL11.GL_LIGHTING);
        }
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}
	
	public static void drawItemStackToGui(Minecraft mc, Block block, int x, int y, boolean fixLighting){
		drawItemStackToGui(mc, Item.getItemFromBlock(block), x, y, fixLighting);
	}
	
//	public static void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight){
//		float f = 1F / (float) textureWidth;
//		float f1 = 1F / (float) textureHeight;
//		Tessellator tessellator = Tessellator.instance;
//		tessellator.startDrawingQuads();
//		tessellator.addVertexWithUV((double) x, (double) (y + height), 0, (double)((float) u * f), (double)((float)));
//		tessellator.draw();
//	}

	private static void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_, float zLevel){
		float f = (float)(p_73733_5_ >> 24 & 255) / 255.0F;
		float f1 = (float)(p_73733_5_ >> 16 & 255) / 255.0F;
		float f2 = (float)(p_73733_5_ >> 8 & 255) / 255.0F;
		float f3 = (float)(p_73733_5_ & 255) / 255.0F;
		float f4 = (float)(p_73733_6_ >> 24 & 255) / 255.0F;
		float f5 = (float)(p_73733_6_ >> 16 & 255) / 255.0F;
		float f6 = (float)(p_73733_6_ >> 8 & 255) / 255.0F;
		float f7 = (float)(p_73733_6_ & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(f1, f2, f3, f);
		tessellator.addVertex((double)p_73733_3_, (double)p_73733_2_, (double) zLevel);
		tessellator.addVertex((double)p_73733_1_, (double)p_73733_2_, (double) zLevel);
		tessellator.setColorRGBA_F(f5, f6, f7, f4);
		tessellator.addVertex((double)p_73733_1_, (double)p_73733_4_, (double) zLevel);
		tessellator.addVertex((double)p_73733_3_, (double)p_73733_4_, (double) zLevel);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
