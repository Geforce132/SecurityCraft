package net.geforcemods.securitycraft.util;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GuiUtils{
	public static void drawItemToGui(Minecraft mc, Item item, int x, int y, boolean fixLighting){
		drawItemStackToGui(mc, new ItemStack(item), x, y, fixLighting);
	}

	public static void drawItemStackToGui(Minecraft mc, ItemStack stack, int x, int y, boolean fixLighting){
		if(fixLighting)
			GlStateManager.enableLighting();

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
		Minecraft.getInstance().getItemRenderer().renderItemOverlays(Minecraft.getInstance().fontRenderer, stack, x, y);

		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
	}
}
