package net.geforcemods.securitycraft.gui.components;

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

//from Forge, changed scrollbar/-list color and removed things unnecessary for this usecase
public abstract class PatronScrollList
{
	private final Minecraft client;
	protected final int listWidth;
	protected final int listHeight;
	protected final int screenWidth;
	protected final int screenHeight;
	protected final int top;
	protected final int bottom;
	protected final int right;
	protected final int left;
	protected final int slotHeight;
	protected int mouseX;
	protected int mouseY;
	private float initialMouseClickY = -2.0F;
	private float scrollFactor;
	public float scrollDistance;
	protected int selectedIndex = -1;
	private int headerHeight;
	protected boolean captureMouse = true;
	public boolean isHovering;

	public PatronScrollList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, int screenWidth, int screenHeight)
	{
		this.client = client;
		listWidth = width;
		listHeight = height;
		this.top = top;
		this.bottom = bottom;
		slotHeight = entryHeight;
		this.left = left;
		right = width + left;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	public abstract int getSize();

	public abstract void drawPanel(int entryRight, int relativeY, Tessellator tesselator, int mouseX, int mouseY);

	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		int scrollBarWidth = 6;
		int scrollBarRight = left + listWidth;
		int scrollBarLeft = scrollBarRight - scrollBarWidth;
		int viewHeight = bottom - top;
		int border = 4;

		this.mouseX = mouseX;
		this.mouseY = mouseY;
		isHovering = mouseX >= left && mouseX <= left + listWidth && mouseY >= top && mouseY <= bottom;

		if(Mouse.isButtonDown(0))
		{
			if(initialMouseClickY == -1.0F)
			{
				if(isHovering)
				{
					if(mouseX >= scrollBarLeft && mouseX <= scrollBarRight)
					{
						int scrollHeight = getContentHeight() - viewHeight - border;

						scrollFactor = -1.0F;

						if(scrollHeight < 1)
							scrollHeight = 1;

						scrollFactor /= (float)(viewHeight - getBarHeight(viewHeight, border)) / (float)scrollHeight;
					}
					else
						scrollFactor = 1.0F;

					initialMouseClickY = mouseY;
				}
				else
					initialMouseClickY = -2.0F;
			}
			else if(initialMouseClickY >= 0.0F)
			{
				scrollDistance -= (mouseY - initialMouseClickY) * scrollFactor;
				initialMouseClickY = mouseY;
			}
		}
		else
			initialMouseClickY = -1.0F;


		Tessellator tess = Tessellator.getInstance();
		BufferBuilder worldr = tess.getBuffer();
		ScaledResolution res = new ScaledResolution(client);
		double scaleW = client.displayWidth / res.getScaledWidth_double();
		double scaleH = client.displayHeight / res.getScaledHeight_double();
		int extraHeight = (getContentHeight() + border) - viewHeight;

		applyScrollLimits();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int)(left * scaleW), (int)(client.displayHeight - (bottom * scaleH)), (int)(listWidth * scaleW), (int)(viewHeight * scaleH));
		GuiUtils.drawGradientRect(0, left, top, right, bottom, 0xC0BFBBB2, 0xD0BFBBB2); //list background
		drawPanel(right, top + border - (int)scrollDistance, tess, mouseX, mouseY);
		GlStateManager.disableDepth();

		if(extraHeight > 0)
		{
			int height = getBarHeight(viewHeight, border);
			int barTop = (int)scrollDistance * (viewHeight - height) / extraHeight + top;

			if(barTop < top)
				barTop = top;

			GlStateManager.disableTexture2D();
			//scrollbar background
			worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			worldr.pos(scrollBarLeft,  bottom, 0.0D).tex(0.0D, 1.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
			worldr.pos(scrollBarRight, bottom, 0.0D).tex(1.0D, 1.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
			worldr.pos(scrollBarRight, top,    0.0D).tex(1.0D, 0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
			worldr.pos(scrollBarLeft,  top,    0.0D).tex(0.0D, 0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
			tess.draw();
			//scrollbar border
			worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			worldr.pos(scrollBarLeft,  barTop + height, 0.0D).tex(0.0D, 1.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
			worldr.pos(scrollBarRight, barTop + height, 0.0D).tex(1.0D, 1.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
			worldr.pos(scrollBarRight, barTop,          0.0D).tex(1.0D, 0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
			worldr.pos(scrollBarLeft,  barTop,          0.0D).tex(0.0D, 0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
			tess.draw();
			//scrollbar
			worldr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			worldr.pos(scrollBarLeft,      barTop + height - 1, 0.0D).tex(0.0D, 1.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
			worldr.pos(scrollBarRight - 1, barTop + height - 1, 0.0D).tex(1.0D, 1.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
			worldr.pos(scrollBarRight - 1, barTop,              0.0D).tex(1.0D, 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
			worldr.pos(scrollBarLeft,      barTop,              0.0D).tex(0.0D, 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
			tess.draw();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public int getContentHeight()
	{
		return getSize() * slotHeight + headerHeight;
	}

	public void applyScrollLimits()
	{
		int listHeight = getContentHeight() - (bottom - top - 7);

		if(listHeight < 0)
			listHeight /= 2;

		if(scrollDistance < 0.0F)
			scrollDistance = 0.0F;
		else if(scrollDistance > listHeight)
			scrollDistance = listHeight;
	}

	public void handleMouseInput(int mouseX, int mouseY) throws IOException
	{
		if(!isHovering)
			return;

		int scroll = Mouse.getEventDWheel();

		if(scroll != 0)
			scrollDistance += (-1 * scroll / 120.0F) * slotHeight / 2;
	}

	public int getBarHeight(int viewHeight, int border)
	{
		int barHeight = (int)((float)(viewHeight * viewHeight) / (float)getContentHeight());

		if(barHeight < 32)
			barHeight = 32;

		if(barHeight > viewHeight - border * 2)
			barHeight = viewHeight - border * 2;

		return barHeight;
	}
}