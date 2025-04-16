package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiUtils {
	//@formatter:off
	private static final ResourceLocation[] MODULE_TEXTURES = {
			new ResourceLocation(SecurityCraft.MODID, "textures/items/module_background.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/whitelist_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/blacklist_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/harming_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/smart_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/storage_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/disguise_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/items/module_background.png")
	};
	//@formatter:on
	private static final ResourceLocation REDSTONE_TEXTURE = new ResourceLocation("textures/items/redstone_dust.png");
	private static final ResourceLocation SUGAR_TEXTURE = new ResourceLocation("textures/items/sugar.png");

	private GuiUtils() {}

	public static void drawItemStackToGui(ItemStack stack, int x, int y, boolean fixLighting) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem renderItem = mc.getRenderItem();

		if (fixLighting)
			GlStateManager.enableLighting();

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		renderItem.renderItemAndEffectIntoGUI(stack, x, y);
		renderItem.renderItemOverlays(mc.fontRenderer, stack, x, y);
		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
	}

	public static void renderModuleInfo(ModuleType module, String moduleTooltip, boolean isModuleInstalled, int moduleLeft, int moduleTop, int screenWidth, int screenHeight, int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		float alpha = isModuleInstalled ? 1.0F : 0.5F;
		int moduleRight = moduleLeft + 16;
		int moduleBottom = moduleTop + 16;

		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(MODULE_TEXTURES[module.ordinal()]);
		drawTexture(Tessellator.getInstance(), moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);

		if (module == ModuleType.REDSTONE) {
			mc.getTextureManager().bindTexture(REDSTONE_TEXTURE);
			drawTexture(Tessellator.getInstance(), moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}
		else if (module == ModuleType.SPEED) {
			mc.getTextureManager().bindTexture(SUGAR_TEXTURE);
			drawTexture(Tessellator.getInstance(), moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}

		if (moduleTooltip != null && !moduleTooltip.isEmpty() && mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom) {
			net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Arrays.asList(moduleTooltip), mouseX, mouseY, screenWidth, screenHeight, -1, mc.fontRenderer);
			RenderHelper.disableStandardItemLighting();
		}

		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.enableLighting();
	}

	private static void drawTexture(Tessellator tess, int moduleLeft, int moduleTop, int moduleRight, int moduleBottom, float alpha) {
		BufferBuilder bufferBuilder = tess.getBuffer();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferBuilder.pos(moduleLeft, moduleBottom, 0).tex(0, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleRight, moduleBottom, 0).tex(1, 1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleRight, moduleTop, 0).tex(1, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		bufferBuilder.pos(moduleLeft, moduleTop, 0).tex(0, 0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		tess.draw();
	}
}
