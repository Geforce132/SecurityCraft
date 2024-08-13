package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientUtils {
	//@formatter:off
	private static final ResourceLocation[] MODULE_TEXTURES = {
			new ResourceLocation(SecurityCraft.MODID, "textures/item/module_background.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/whitelist_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/blacklist_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/harming_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/smart_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/storage_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/disguise_module.png"),
			new ResourceLocation(SecurityCraft.MODID, "textures/item/module_background.png")
	};
	//@formatter:on
	private static final ResourceLocation REDSTONE_TEXTURE = new ResourceLocation("textures/item/redstone.png");
	private static final ResourceLocation SUGAR_TEXTURE = new ResourceLocation("textures/item/sugar.png");

	private ClientUtils() {}

	public static void renderModuleInfo(GuiGraphics guiGraphics, Font font, ModuleType module, Component moduleTooltip, boolean isModuleInstalled, int moduleLeft, int moduleTop, int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getInstance();
		float alpha = isModuleInstalled ? 1.0F : 0.5F;
		int moduleRight = moduleLeft + 16;
		int moduleBottom = moduleTop + 16;
		Matrix4f m4f = guiGraphics.pose().last().pose();
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem._setShaderTexture(0, MODULE_TEXTURES[module.ordinal()]);
		drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);

		if (module == ModuleType.REDSTONE) {
			RenderSystem._setShaderTexture(0, REDSTONE_TEXTURE);
			drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}
		else if (module == ModuleType.SPEED) {
			RenderSystem._setShaderTexture(0, SUGAR_TEXTURE);
			drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}

		RenderSystem.disableBlend();

		if (moduleTooltip != null && mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom && mc.screen != null)
			guiGraphics.renderComponentTooltip(font, Arrays.asList(moduleTooltip), mouseX, mouseY);
	}

	private static void drawTexture(BufferBuilder bufferBuilder, Matrix4f m4f, int moduleLeft, int moduleTop, int moduleRight, int moduleBottom, float alpha) {
		bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferBuilder.vertex(m4f, moduleLeft, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(0, 1).endVertex();
		bufferBuilder.vertex(m4f, moduleRight, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(1, 1).endVertex();
		bufferBuilder.vertex(m4f, moduleRight, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(1, 0).endVertex();
		bufferBuilder.vertex(m4f, moduleLeft, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(0, 0).endVertex();
		BufferUploader.drawWithShader(bufferBuilder.end());
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

	public static void fillHorizontalGradient(GuiGraphics guiGraphics, int zLevel, int left, int top, int right, int bottom, int fromColor, int toColor) {
		float fromAlpha = (fromColor >> 24 & 255) / 255.0F;
		float fromRed = (fromColor >> 16 & 255) / 255.0F;
		float fromGreen = (fromColor >> 8 & 255) / 255.0F;
		float fromBlue = (fromColor & 255) / 255.0F;
		float toAlpha = (toColor >> 24 & 255) / 255.0F;
		float toRed = (toColor >> 16 & 255) / 255.0F;
		float toGreen = (toColor >> 8 & 255) / 255.0F;
		float toBlue = (toColor & 255) / 255.0F;
		Matrix4f mat = guiGraphics.pose().last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();

		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		buffer.vertex(mat, right, top, zLevel).color(toRed, toGreen, toBlue, toAlpha).endVertex();
		buffer.vertex(mat, left, top, zLevel).color(fromRed, fromGreen, fromBlue, fromAlpha).endVertex();
		buffer.vertex(mat, left, bottom, zLevel).color(fromRed, fromGreen, fromBlue, fromAlpha).endVertex();
		buffer.vertex(mat, right, bottom, zLevel).color(toRed, toGreen, toBlue, toAlpha).endVertex();
		tessellator.end();
		RenderSystem.disableBlend();
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