package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.joml.Quaternionf;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientUtils {
	//@formatter:off
	private static final ResourceLocation[] MODULE_TEXTURES = {
			SecurityCraft.resLoc("textures/item/module_background.png"),
			SecurityCraft.resLoc("textures/item/whitelist_module.png"),
			SecurityCraft.resLoc("textures/item/blacklist_module.png"),
			SecurityCraft.resLoc("textures/item/harming_module.png"),
			SecurityCraft.resLoc("textures/item/smart_module.png"),
			SecurityCraft.resLoc("textures/item/storage_module.png"),
			SecurityCraft.resLoc("textures/item/disguise_module.png"),
			SecurityCraft.resLoc("textures/item/module_background.png")
	};
	//@formatter:on
	private static final ResourceLocation REDSTONE_TEXTURE = SecurityCraft.mcResLoc("textures/item/redstone.png");
	private static final ResourceLocation SUGAR_TEXTURE = SecurityCraft.mcResLoc("textures/item/sugar.png");

	private ClientUtils() {}

	public static void renderModuleInfo(GuiGraphics guiGraphics, Font font, ModuleType module, Component moduleTooltip, boolean isModuleInstalled, int moduleLeft, int moduleTop, int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getInstance();
		int color = isModuleInstalled ? 0xFFFFFFFF : 0x7FFFFFFF;
		int moduleRight = moduleLeft + 16;
		int moduleBottom = moduleTop + 16;

		drawTexture(guiGraphics, MODULE_TEXTURES[module.ordinal()], moduleLeft, moduleTop, color);

		if (module == ModuleType.REDSTONE)
			drawTexture(guiGraphics, REDSTONE_TEXTURE, moduleLeft, moduleTop, color);
		else if (module == ModuleType.SPEED)
			drawTexture(guiGraphics, SUGAR_TEXTURE, moduleLeft, moduleTop, color);

		if (moduleTooltip != null && mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom && mc.screen != null)
			guiGraphics.setComponentTooltipForNextFrame(font, Arrays.asList(moduleTooltip), mouseX, mouseY); //TODO: works?
	}

	private static void drawTexture(GuiGraphics guiGraphics, ResourceLocation texture, int moduleLeft, int moduleTop, int color) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, moduleLeft, moduleTop, 0.0F, 0.0F, 16, 16, 16, 16, color);
	}

	public static Quaternionf fromXYZDegrees(float x, float y, float z) {
		return fromXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
	}

	public static Quaternionf fromXYZ(float x, float y, float z) {
		Quaternionf quaternion = new Quaternionf();

		quaternion.mul(new Quaternionf((float) Math.sin(x / 2.0F), 0.0F, 0.0F, (float) Math.cos(x / 2.0F)));
		quaternion.mul(new Quaternionf(0.0F, (float) Math.sin(y / 2.0F), 0.0F, (float) Math.cos(y / 2.0F)));
		quaternion.mul(new Quaternionf(0.0F, 0.0F, (float) Math.sin(z / 2.0F), (float) Math.cos(z / 2.0F)));
		return quaternion;
	}

	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0;
		int g = 0;
		int b = 0;

		if (saturation == 0)
			r = g = b = (int) (brightness * 255.0F + 0.5F);
		else {
			float h = (hue - (float) Math.floor(hue)) * 6.0F;
			float f = h - (float) Math.floor(h);
			float p = brightness * (1.0F - saturation);
			float q = brightness * (1.0F - saturation * f);
			float t = brightness * (1.0F - (saturation * (1.0F - f)));

			switch ((int) h) {
				case 0:
					r = (int) (brightness * 255.0F + 0.5F);
					g = (int) (t * 255.0F + 0.5F);
					b = (int) (p * 255.0F + 0.5F);
					break;
				case 1:
					r = (int) (q * 255.0F + 0.5F);
					g = (int) (brightness * 255.0F + 0.5F);
					b = (int) (p * 255.0F + 0.5F);
					break;
				case 2:
					r = (int) (p * 255.0F + 0.5F);
					g = (int) (brightness * 255.0F + 0.5F);
					b = (int) (t * 255.0F + 0.5F);
					break;
				case 3:
					r = (int) (p * 255.0F + 0.5F);
					g = (int) (q * 255.0F + 0.5F);
					b = (int) (brightness * 255.0F + 0.5F);
					break;
				case 4:
					r = (int) (t * 255.0F + 0.5F);
					g = (int) (p * 255.0F + 0.5F);
					b = (int) (brightness * 255.0F + 0.5F);
					break;
				case 5:
					r = (int) (brightness * 255.0F + 0.5F);
					g = (int) (p * 255.0F + 0.5F);
					b = (int) (q * 255.0F + 0.5F);
					break;
			}
		}

		return 0xFF000000 | (r << 16) | (g << 8) | b;
	}

	public static float[] RGBtoHSB(int r, int g, int b) {
		float hue, saturation, brightness;
		float[] hsb = new float[3];
		int cmax = (r > g) ? r : g;
		int cmin = (r < g) ? r : g;

		if (b > cmax)
			cmax = b;

		if (b < cmin)
			cmin = b;

		brightness = cmax / 255.0F;

		if (cmax != 0)
			saturation = ((float) (cmax - cmin)) / ((float) cmax);
		else
			saturation = 0;

		if (saturation == 0)
			hue = 0;
		else {
			float redc = (float) (cmax - r) / (float) (cmax - cmin);
			float greenc = (float) (cmax - g) / (float) (cmax - cmin);
			float bluec = (float) (cmax - b) / (float) (cmax - cmin);

			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0F + redc - bluec;
			else
				hue = 4.0F + greenc - redc;

			hue = hue / 6.0F;

			if (hue < 0)
				hue = hue + 1.0F;
		}

		hsb[0] = hue;
		hsb[1] = saturation;
		hsb[2] = brightness;
		return hsb;
	}
}