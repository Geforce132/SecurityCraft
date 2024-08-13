package net.geforcemods.securitycraft.util;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class ClientUtils {
	private ClientUtils() {}

	public static boolean hasShiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	public static boolean hasCtrlDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}

	public static void renderBoxInLevel(double minX, double maxX, double minZ, double maxZ, double height, int rgbColor) {
		float r = (rgbColor >> 16 & 0xFF) / 255.0F;
		float g = (rgbColor >> 8 & 0xFF) / 255.0F;
		float b = (rgbColor & 0xFF) / 255.0F;
		double shrinkFactor = 0.002F;

		minX += shrinkFactor;
		minZ += shrinkFactor;
		maxX -= shrinkFactor;
		maxZ -= shrinkFactor;
		height -= shrinkFactor;
		GlStateManager.glLineWidth(2F);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		RenderGlobal.drawBoundingBox(minX, shrinkFactor, minZ, maxX, height, maxZ, r, g, b, 1.0F);
		GlStateManager.enableLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		GlStateManager.enableTexture2D();
	}

	public static void fillHorizontalGradient(int zLevel, int left, int top, int right, int bottom, int fromColor, int toColor) {
		float fromAlpha = (fromColor >> 24 & 255) / 255.0F;
		float fromRed = (fromColor >> 16 & 255) / 255.0F;
		float fromGreen = (fromColor >> 8 & 255) / 255.0F;
		float fromBlue = (fromColor & 255) / 255.0F;
		float toAlpha = (toColor >> 24 & 255) / 255.0F;
		float toRed = (toColor >> 16 & 255) / 255.0F;
		float toGreen = (toColor >> 8 & 255) / 255.0F;
		float toBlue = (toColor & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(right, top, zLevel).color(toRed, toGreen, toBlue, toAlpha).endVertex();
		buffer.pos(left, top, zLevel).color(fromRed, fromGreen, fromBlue, fromAlpha).endVertex();
		buffer.pos(left, bottom, zLevel).color(fromRed, fromGreen, fromBlue, fromAlpha).endVertex();
		buffer.pos(right, bottom, zLevel).color(toRed, toGreen, toBlue, toAlpha).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
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