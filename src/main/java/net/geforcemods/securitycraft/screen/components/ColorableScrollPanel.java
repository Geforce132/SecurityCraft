package net.geforcemods.securitycraft.screen.components;

import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.config.GuiUtils;

// from Forge, changed scrollbar/-list color and removed things unnecessary for this usecase
public abstract class ColorableScrollPanel {
	public static class Color {
		public final int r, g, b, a;

		public Color(int r, int g, int b, int a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		public int combinedRGBA() {
			return ((r & 0xFF) << 24) | ((g & 0xFF) << 16) | ((b & 0xFF) << 8) | (a & 0xFF);
		}
	}

	private final Minecraft client;
	protected final int listWidth;
	protected final int listHeight;
	protected final int top;
	protected final int bottom;
	protected final int right;
	protected final int left;
	protected final int slotHeight;
	protected final int scrollBarWidth = 6;
	protected final int scrollBarRight;
	protected final int scrollBarLeft;
	protected final int viewHeight;
	protected final int border = 4;
	protected int mouseX;
	protected int mouseY;
	private float initialMouseClickY = -2.0F;
	private float scrollFactor;
	public float scrollDistance;
	protected int selectedIndex = -1;
	private int headerHeight;
	protected boolean captureMouse = true;
	public boolean isHovering;
	private Color backgroundTo;
	private Color backgroundFrom;
	private Color scrollbarBackground;
	private Color scrollbarBorder;
	private Color scrollbar;

	public ColorableScrollPanel(Minecraft client, int width, int height, int top, int left) {
		this(client, width, height, top, top + height, left, 12, new Color(0xC0, 0x10, 0x10, 0x10), new Color(0xD0, 0x10, 0x10, 0x10), new Color(0x00, 0x00, 0x00, 0xFF), new Color(0x80, 0x80, 0x80, 0xFF), new Color(0xC0, 0xC0, 0xC0, 0xFF));
	}

	public ColorableScrollPanel(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, Color backgroundFrom, Color backgroundTo) {
		this(client, width, height, top, bottom, left, entryHeight, backgroundFrom, backgroundTo, new Color(0x00, 0x00, 0x00, 0xFF), new Color(0x80, 0x80, 0x80, 0xFF), new Color(0xC0, 0xC0, 0xC0, 0xFF));
	}

	public ColorableScrollPanel(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, Color backgroundFrom, Color backgroundTo, Color scrollbarBackground, Color scrollbarBorder, Color scrollbar) {
		this.client = client;
		listWidth = width;
		listHeight = height;
		this.top = top;
		this.bottom = bottom;
		slotHeight = entryHeight;
		this.left = left;
		right = width + left;
		this.backgroundFrom = backgroundFrom;
		this.backgroundTo = backgroundTo;
		this.scrollbarBackground = scrollbarBackground;
		this.scrollbarBorder = scrollbarBorder;
		this.scrollbar = scrollbar;
		scrollBarRight = left + listWidth;
		scrollBarLeft = scrollBarRight - scrollBarWidth;
		viewHeight = bottom - top;
	}

	public abstract int getSize();

	public abstract void drawPanel(int entryRight, int relativeY, Tessellator tesselator, int mouseX, int mouseY);

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		isHovering = mouseX >= left && mouseX <= left + listWidth && mouseY >= top && mouseY <= bottom;

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder worldr = tess.getBuffer();
		ScaledResolution res = new ScaledResolution(client);
		double scaleW = client.displayWidth / res.getScaledWidth_double();
		double scaleH = client.displayHeight / res.getScaledHeight_double();
		int extraHeight = (getContentHeight() + border) - viewHeight;

		applyScrollLimits();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) (left * scaleW), (int) (client.displayHeight - (bottom * scaleH)), (int) (listWidth * scaleW), (int) (viewHeight * scaleH));
		GuiUtils.drawGradientRect(0, left, top, right, bottom, backgroundFrom.combinedRGBA(), backgroundTo.combinedRGBA()); //list background
		drawPanel(right, top + border - (int) scrollDistance, tess, mouseX, mouseY);
		GlStateManager.disableDepth();

		if (extraHeight > 0) {
			int height = getBarHeight(viewHeight, border);
			int barTop = (int) scrollDistance * (viewHeight - height) / extraHeight + top;

			if (barTop < top)
				barTop = top;

			GlStateManager.disableTexture2D();
			//scrollbar background
			worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			worldr.pos(scrollBarLeft, bottom, 0.0D).tex(0.0D, 1.0D).color(scrollbarBackground.r, scrollbarBackground.g, scrollbarBackground.b, scrollbarBackground.a).endVertex();
			worldr.pos(scrollBarRight, bottom, 0.0D).tex(1.0D, 1.0D).color(scrollbarBackground.r, scrollbarBackground.g, scrollbarBackground.b, scrollbarBackground.a).endVertex();
			worldr.pos(scrollBarRight, top, 0.0D).tex(1.0D, 0.0D).color(scrollbarBackground.r, scrollbarBackground.g, scrollbarBackground.b, scrollbarBackground.a).endVertex();
			worldr.pos(scrollBarLeft, top, 0.0D).tex(0.0D, 0.0D).color(scrollbarBackground.r, scrollbarBackground.g, scrollbarBackground.b, scrollbarBackground.a).endVertex();
			tess.draw();
			//scrollbar border
			worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			worldr.pos(scrollBarLeft, barTop + height, 0.0D).tex(0.0D, 1.0D).color(scrollbarBorder.r, scrollbarBorder.g, scrollbarBorder.b, scrollbarBorder.a).endVertex();
			worldr.pos(scrollBarRight, barTop + height, 0.0D).tex(1.0D, 1.0D).color(scrollbarBorder.r, scrollbarBorder.g, scrollbarBorder.b, scrollbarBorder.a).endVertex();
			worldr.pos(scrollBarRight, barTop, 0.0D).tex(1.0D, 0.0D).color(scrollbarBorder.r, scrollbarBorder.g, scrollbarBorder.b, scrollbarBorder.a).endVertex();
			worldr.pos(scrollBarLeft, barTop, 0.0D).tex(0.0D, 0.0D).color(scrollbarBorder.r, scrollbarBorder.g, scrollbarBorder.b, scrollbarBorder.a).endVertex();
			tess.draw();
			//scrollbar
			worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			worldr.pos(scrollBarLeft, barTop + height - 1, 0.0D).tex(0.0D, 1.0D).color(scrollbar.r, scrollbar.g, scrollbar.b, scrollbar.a).endVertex();
			worldr.pos(scrollBarRight - 1, barTop + height - 1, 0.0D).tex(1.0D, 1.0D).color(scrollbar.r, scrollbar.g, scrollbar.b, scrollbar.a).endVertex();
			worldr.pos(scrollBarRight - 1, barTop, 0.0D).tex(1.0D, 0.0D).color(scrollbar.r, scrollbar.g, scrollbar.b, scrollbar.a).endVertex();
			worldr.pos(scrollBarLeft, barTop, 0.0D).tex(0.0D, 0.0D).color(scrollbar.r, scrollbar.g, scrollbar.b, scrollbar.a).endVertex();
			tess.draw();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public int getContentHeight() {
		return getSize() * slotHeight + headerHeight;
	}

	public void applyScrollLimits() {
		int listHeight = getContentHeight() - (bottom - top - 4);

		if (listHeight < 0)
			listHeight /= 2;

		if (scrollDistance < 0.0F)
			scrollDistance = 0.0F;
		else if (scrollDistance > listHeight)
			scrollDistance = listHeight;
	}

	public void handleMouseInput(int mouseX, int mouseY) throws IOException {
		if (isHovering) {
			int scroll = Mouse.getEventDWheel();

			if (scroll != 0)
				scrollDistance += (-scroll / 120.0F) * slotHeight / 2;
		}

		if (Mouse.isButtonDown(0)) {
			if (initialMouseClickY == -1.0F) {
				if (isHovering) {
					int entryLeft = left;
					int entryRight = left + listWidth - 7;
					int mouseListY = mouseY - top - headerHeight + (int) scrollDistance - border;
					int slotIndex = mouseListY / slotHeight;

					if (mouseX >= entryLeft && mouseX <= entryRight && slotIndex >= 0 && mouseListY >= 0 && slotIndex < getSize())
						elementClicked(mouseX, mouseY, slotIndex);

					if (mouseX >= scrollBarLeft && mouseX <= scrollBarRight) {
						int scrollHeight = getContentHeight() - viewHeight - border;

						scrollFactor = -1.0F;

						if (scrollHeight < 1)
							scrollHeight = 1;

						scrollFactor /= (float) (viewHeight - getBarHeight(viewHeight, border)) / (float) scrollHeight;
					}
					else
						scrollFactor = 1.0F;

					initialMouseClickY = mouseY;
				}
				else
					initialMouseClickY = -2.0F;
			}
			else if (initialMouseClickY >= 0.0F) {
				scrollDistance -= (mouseY - initialMouseClickY) * scrollFactor;
				initialMouseClickY = mouseY;
			}
		}
		else
			initialMouseClickY = -1.0F;

		applyScrollLimits();
	}

	public void elementClicked(int mouseX, int mouseY, int slotIndex) {}

	public int getBarHeight(int viewHeight, int border) {
		int barHeight = (int) ((float) (viewHeight * viewHeight) / (float) getContentHeight());

		if (barHeight < 32)
			barHeight = 32;

		if (barHeight > viewHeight - border * 2)
			barHeight = viewHeight - border * 2;

		return barHeight;
	}
}