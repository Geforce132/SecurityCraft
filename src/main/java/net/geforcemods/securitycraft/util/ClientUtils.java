package net.geforcemods.securitycraft.util;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

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

	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 */
	public static String getFormattedMinecraftTime() {
		Long time = Minecraft.getInstance().level.getDayTime();

		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (time.longValue() / 16.666666F % 60.0F);

		return String.format("%02d:%02d %s", Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM");
	}

	public static void renderModuleInfo(MatrixStack matrix, ModuleType module, ITextComponent moduleTooltip, ITextComponent noModuleTooltip, boolean isModuleInstalled, int moduleLeft, int moduleTop, int screenWidth, int screenHeight, int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getInstance();
		float alpha = isModuleInstalled ? 1.0F : 0.5F;
		int moduleRight = moduleLeft + 16;
		int moduleBottom = moduleTop + 16;
		Matrix4f m4f = matrix.last().pose();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuilder();

		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.defaultBlendFunc();

		mc.getTextureManager().bind(MODULE_TEXTURES[module.ordinal()]);
		drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);

		if (module == ModuleType.REDSTONE) {
			mc.getTextureManager().bind(REDSTONE_TEXTURE);
			drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}
		else if (module == ModuleType.SPEED) {
			mc.getTextureManager().bind(SUGAR_TEXTURE);
			drawTexture(bufferBuilder, m4f, moduleLeft, moduleTop, moduleRight, moduleBottom, alpha);
		}

		RenderSystem.disableBlend();

		if (mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom) {
			ITextComponent text = isModuleInstalled ? moduleTooltip : noModuleTooltip;

			if (text != null)
				GuiUtils.drawHoveringText(matrix, Arrays.asList(text), mouseX, mouseY, screenWidth, screenHeight, -1, mc.font);
		}
	}

	private static void drawTexture(BufferBuilder bufferBuilder, Matrix4f m4f, int moduleLeft, int moduleTop, int moduleRight, int moduleBottom, float alpha) {
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferBuilder.vertex(m4f, moduleLeft, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(0, 1).endVertex();
		bufferBuilder.vertex(m4f, moduleRight, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(1, 1).endVertex();
		bufferBuilder.vertex(m4f, moduleRight, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(1, 0).endVertex();
		bufferBuilder.vertex(m4f, moduleLeft, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).uv(0, 0).endVertex();
		bufferBuilder.end();
		WorldVertexBufferUploader.end(bufferBuilder);
	}

	public static Quaternion fromXYZDegrees(float x, float y, float z) {
		return fromXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
	}

	public static Quaternion fromXYZ(float x, float y, float z) {
		Quaternion quaternion = Quaternion.ONE.copy();

		quaternion.mul(new Quaternion((float) Math.sin(x / 2.0F), 0.0F, 0.0F, (float) Math.cos(x / 2.0F)));
		quaternion.mul(new Quaternion(0.0F, (float) Math.sin(y / 2.0F), 0.0F, (float) Math.cos(y / 2.0F)));
		quaternion.mul(new Quaternion(0.0F, 0.0F, (float) Math.sin(z / 2.0F), (float) Math.cos(z / 2.0F)));
		return quaternion;
	}

	public static void renderBoxInLevel(IRenderTypeBuffer buffer, Matrix4f positionMatrix, int minX, int maxX, int minZ, int maxZ, int height, int rgbColor) {
		IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
		int r = rgbColor >> 16 & 255;
		int g = rgbColor >> 8 & 255;
		int b = rgbColor & 255;

		//bottom lines
		builder.vertex(positionMatrix, minX, 0.0F, minZ).color(r, g, b, 255).normal(0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, 0.0F, minZ).color(r, g, b, 255).normal(0.0F, 0.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, 0.0F, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, 0.0F, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, 0.0F, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, 0.0F, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, 0.0F, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, 0.0F, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		//top lines
		builder.vertex(positionMatrix, minX, height, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, height, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, height, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, height, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, height, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, height, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, height, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, height, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		//corner edge lines
		builder.vertex(positionMatrix, minX, 0.0F, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, height, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, 0.0F, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, minX, height, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, 0.0F, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, height, maxZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, 0.0F, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, maxX, height, minZ).color(r, g, b, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
	}

	public static void fillHorizontalGradient(MatrixStack pose, int zLevel, int left, int top, int right, int bottom, int fromColor, int toColor) {
		float fromAlpha = (fromColor >> 24 & 255) / 255.0F;
		float fromRed = (fromColor >> 16 & 255) / 255.0F;
		float fromGreen = (fromColor >> 8 & 255) / 255.0F;
		float fromBlue = (fromColor & 255) / 255.0F;
		float toAlpha = (toColor >> 24 & 255) / 255.0F;
		float toRed = (toColor >> 16 & 255) / 255.0F;
		float toGreen = (toColor >> 8 & 255) / 255.0F;
		float toBlue = (toColor & 255) / 255.0F;
		Matrix4f mat = pose.last().pose();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();

		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.vertex(mat, right, top, zLevel).color(toRed, toGreen, toBlue, toAlpha).endVertex();
		buffer.vertex(mat, left, top, zLevel).color(fromRed, fromGreen, fromBlue, fromAlpha).endVertex();
		buffer.vertex(mat, left, bottom, zLevel).color(fromRed, fromGreen, fromBlue, fromAlpha).endVertex();
		buffer.vertex(mat, right, bottom, zLevel).color(toRed, toGreen, toBlue, toAlpha).endVertex();
		tessellator.end();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
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

		return 0xFF000000 | (r << 16) | (g << 8) | (b << 0);
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