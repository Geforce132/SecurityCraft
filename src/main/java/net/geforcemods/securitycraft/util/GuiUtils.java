package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiUtils{
	private static final ResourceLocation SMART_MODULE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/item/smart_module.png");

	public static void drawItemToGui(Item item, int x, int y, boolean fixLighting){
		drawItemStackToGui(new ItemStack(item), x, y, fixLighting);
	}

	public static void drawItemStackToGui(ItemStack stack, int x, int y, boolean fixLighting){
		if(fixLighting)
			GlStateManager.enableLighting();

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
		Minecraft.getInstance().getItemRenderer().renderItemOverlays(Minecraft.getInstance().fontRenderer, stack, x, y);

		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
	}

	public static void renderSmartModuleInfo(String moduleTooltip, String noModuleTooltip, boolean isSmart, int guiLeft, int guiTop, int screenWidth, int screenHeight, int mouseX, int mouseY)
	{
		Minecraft mc = Minecraft.getInstance();
		float alpha = isSmart ? 1.0F : 0.5F;
		int moduleLeft = guiLeft + 5;
		int moduleRight = moduleLeft + 16;
		int moduleTop = guiTop + 5;
		int moduleBottom = moduleTop + 16;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tess.getBuffer();

		GlStateManager.enableAlphaTest();
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(SMART_MODULE_TEXTURE);
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferBuilder.pos(moduleLeft, moduleBottom, 0).tex(0, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleRight, moduleBottom, 0).tex(1, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleRight, moduleTop, 0).tex(1, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleLeft, moduleTop, 0).tex(0, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		tess.draw();
		GlStateManager.disableBlend();
		GlStateManager.disableAlphaTest();

		if(mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom)
		{
			String text = isSmart ? moduleTooltip : noModuleTooltip;

			if(text != null && !text.isEmpty())
				net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Arrays.asList(text), mouseX, mouseY, screenWidth, screenHeight, -1, mc.fontRenderer);
		}
	}
}
