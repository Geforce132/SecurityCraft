package net.geforcemods.securitycraft.screen.components;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.ScrollPanel;

public abstract class ColorableScrollPanel extends ScrollPanel
{
	public record Color(int r, int g, int b, int a) {
		public int combinedRGBA()
		{
			return ((r & 0xFF) << 24) | ((g & 0xFF) << 16) | ((b & 0xFF) << 8) | (a & 0xFF);
		}
	};

	protected final int barWidth = 6;
	public final int barLeft;
	private Color backgroundTo;
	private Color backgroundFrom;
	private Color scrollbarBackground;
	private Color scrollbarBorder;
	private Color scrollbar;

	public ColorableScrollPanel(Minecraft client, int width, int height, int top, int left)
	{
		this(client, width, height, top, left, new Color(0xC0, 0x10, 0x10, 0x10), new Color(0xD0, 0x10, 0x10, 0x10), new Color(0x00, 0x00, 0x00, 0xFF), new Color(0x80, 0x80, 0x80, 0xFF), new Color(0xC0, 0xC0, 0xC0, 0xFF));
	}

	public ColorableScrollPanel(Minecraft client, int width, int height, int top, int left, Color backgroundFrom, Color backgroundTo, Color scrollbarBackground, Color scrollbarBorder, Color scrollbar)
	{
		super(client, width, height, top, left);

		barLeft = left + width - barWidth;
		this.backgroundFrom = backgroundFrom;
		this.backgroundTo = backgroundTo;
		this.scrollbarBackground = scrollbarBackground;
		this.scrollbarBorder = scrollbarBorder;
		this.scrollbar = scrollbar;
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		Tesselator tess = Tesselator.getInstance();
		BufferBuilder buffer = tess.getBuilder();
		Minecraft client = Minecraft.getInstance();
		double scale = client.getWindow().getGuiScale();
		int baseY = top + border - (int)scrollDistance;
		int extraHeight = getContentHeight() + border - height;

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int)(left  * scale), (int)(client.getWindow().getHeight() - (bottom * scale)),
				(int)(width * scale), (int)(height * scale));
		drawGradientRect(matrix, left, top, right, bottom, backgroundTo.combinedRGBA(), backgroundFrom.combinedRGBA()); //list background
		drawPanel(matrix, right, baseY, tess, mouseX, mouseY);
		RenderSystem.disableDepthTest();

		if(extraHeight > 0)
		{
			int barHeight = getBarHeight();
			int barTop = (int)scrollDistance * (height - barHeight) / extraHeight + top;

			if(barTop < top)
				barTop = top;

			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			//scrollbar background
			buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			buffer.vertex(barLeft,            bottom, 0.0D).color(scrollbarBackground.r, scrollbarBackground.g, scrollbarBackground.b, scrollbarBackground.a).endVertex();
			buffer.vertex(barLeft + barWidth, bottom, 0.0D).color(scrollbarBackground.r, scrollbarBackground.g, scrollbarBackground.b, scrollbarBackground.a).endVertex();
			buffer.vertex(barLeft + barWidth, top,    0.0D).color(scrollbarBackground.r, scrollbarBackground.g, scrollbarBackground.b, scrollbarBackground.a).endVertex();
			buffer.vertex(barLeft,            top,    0.0D).color(scrollbarBackground.r, scrollbarBackground.g, scrollbarBackground.b, scrollbarBackground.a).endVertex();
			tess.end();
			//scrollbar border
			buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			buffer.vertex(barLeft,            barTop + barHeight, 0.0D).color(scrollbarBorder.r, scrollbarBorder.g, scrollbarBorder.b, scrollbarBorder.a).endVertex();
			buffer.vertex(barLeft + barWidth, barTop + barHeight, 0.0D).color(scrollbarBorder.r, scrollbarBorder.g, scrollbarBorder.b, scrollbarBorder.a).endVertex();
			buffer.vertex(barLeft + barWidth, barTop,             0.0D).color(scrollbarBorder.r, scrollbarBorder.g, scrollbarBorder.b, scrollbarBorder.a).endVertex();
			buffer.vertex(barLeft,            barTop,             0.0D).color(scrollbarBorder.r, scrollbarBorder.g, scrollbarBorder.b, scrollbarBorder.a).endVertex();
			tess.end();
			//scrollbar
			buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			buffer.vertex(barLeft,                barTop + barHeight - 1, 0.0D).color(scrollbar.r, scrollbar.g, scrollbar.b, scrollbar.a).endVertex();
			buffer.vertex(barLeft + barWidth - 1, barTop + barHeight - 1, 0.0D).color(scrollbar.r, scrollbar.g, scrollbar.b, scrollbar.a).endVertex();
			buffer.vertex(barLeft + barWidth - 1, barTop,                 0.0D).color(scrollbar.r, scrollbar.g, scrollbar.b, scrollbar.a).endVertex();
			buffer.vertex(barLeft,                barTop,                 0.0D).color(scrollbar.r, scrollbar.g, scrollbar.b, scrollbar.a).endVertex();
			tess.end();
		}

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public int getBarHeight()
	{
		int barHeight = (height * height) / getContentHeight();

		if(barHeight < 32)
			barHeight = 32;

		if(barHeight > height - border * 2)
			barHeight = height - border * 2;

		return barHeight;
	}
}
