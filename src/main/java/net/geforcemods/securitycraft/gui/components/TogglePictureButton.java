package net.geforcemods.securitycraft.gui.components;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class TogglePictureButton extends ClickButton {

	private ResourceLocation textureLocation;
	private int[] u;
	private int[] v;
	private int currentIndex = 0;
	private final int toggleCount;
	private final int drawOffset;
	private final int drawWidth;
	private final int drawHeight;
	private final int uWidth;
	private final int vHeight;
	private final int textureWidth;
	private final int textureHeight;

	public TogglePictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int[] textureX, int[] textureY, int drawOffset, int toggleCount, Consumer<ClickButton> onClick)
	{
		this(id, xPos, yPos, width, height, texture, textureX, textureY, drawOffset, 16, 16, 16, 16, 256, 256, toggleCount, onClick);
	}

	public TogglePictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int[] textureX, int[] textureY, int drawOffset, int drawWidth, int drawHeight, int uWidth, int vHeight, int textureWidth, int textureHeight, int toggleCount, Consumer<ClickButton> onClick)
	{
		super(id, xPos, yPos, width, height, "", onClick);

		if(textureX.length != toggleCount || textureY.length != toggleCount)
			throw new RuntimeException("GuiTogglePictureButton was set up incorrectly. Array lengths must match toggleCount!");

		this.toggleCount = toggleCount;
		textureLocation = texture;
		u = textureX;
		v = textureY;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.drawOffset = drawOffset;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (visible)
		{
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, x, y, 0, 46 + getHoverState(hovered) * 20, width, height, 200, 20, 2, 3, 2, 2, zLevel);

			if(getTextureLocation() != null)
			{
				mc.getTextureManager().bindTexture(getTextureLocation());
				drawScaledCustomSizeModalRect(x + drawOffset, y + drawOffset, u[currentIndex], v[currentIndex], uWidth, vHeight, drawWidth, drawHeight, textureWidth, textureHeight);
			}
		}
	}

	@Override
	public void onClick()
	{
		setCurrentIndex(currentIndex + 1);
		super.onClick();
	}

	public int getCurrentIndex()
	{
		return currentIndex;
	}

	public void setCurrentIndex(int newIndex)
	{
		currentIndex = newIndex % toggleCount;
	}

	public ResourceLocation getTextureLocation()
	{
		return textureLocation;
	}
}
