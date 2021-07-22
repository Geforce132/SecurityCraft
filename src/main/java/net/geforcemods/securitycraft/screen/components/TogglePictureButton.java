package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class TogglePictureButton extends IdButton{

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

	public TogglePictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int[] textureX, int[] textureY, int drawOffset, int toggleCount, Consumer<IdButton> onClick)
	{
		this(id, xPos, yPos, width, height, texture, textureX, textureY, drawOffset, 16, 16, 16, 16, 256, 256, toggleCount, onClick);
	}

	public TogglePictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int[] textureX, int[] textureY, int drawOffset, int drawWidth, int drawHeight, int uWidth, int vHeight, int textureWidth, int textureHeight, int toggleCount, Consumer<IdButton> onClick)
	{
		super(id, xPos, yPos, width, height, "", onClick);

		if(textureX.length != toggleCount || textureY.length != toggleCount)
			throw new RuntimeException("TogglePictureButton was set up incorrectly. Array lengths must match toggleCount!");

		textureLocation = texture;
		u = textureX;
		v = textureY;
		this.toggleCount = toggleCount;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.drawOffset = drawOffset;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		if (visible)
		{
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			GuiUtils.drawContinuousTexturedBox(matrix, WIDGETS_LOCATION, x, y, 0, 46 + getYImage(isHovered()) * 20, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());

			if(getTextureLocation() != null)
			{
				Minecraft.getInstance().getTextureManager().bind(getTextureLocation());
				blit(matrix, x + drawOffset, y + drawOffset, drawWidth, drawHeight, u[currentIndex], v[currentIndex], uWidth, vHeight, textureWidth, textureHeight);
			}
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY)
	{
		setCurrentIndex(currentIndex + 1);
		super.onClick(mouseX, mouseY);
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
