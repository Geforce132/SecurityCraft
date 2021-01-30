package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiSlider;

public class NamedSlider extends GuiSlider
{
	public int id;
	private String blockName;
	private Consumer<NamedSlider> consumer;

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, int minVal, int maxVal, int currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
	{
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(initialString);
		blockName = bN;
		this.id = id;
	}

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
	{
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(initialString);
		blockName = bN;
		this.id = id;
	}

	public NamedSlider(String initialString, String bN, int id, int xPos, int yPos, int width, int height, String prefix, String suf, int minVal, int maxVal, int currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par, Consumer<NamedSlider> method)
	{
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(initialString);
		blockName = bN;
		this.id = id;
		consumer = method;
	}

	@Override
	protected void renderBg(Minecraft mc, int mouseX, int mouseY)
	{
		if(visible)
		{
			int offset = (isHovered() ? 2 : 1) * 20;

			if(dragging)
			{
				sliderValue = (mouseX - (x + 4)) / (float)(width - 8);
				updateSlider();
			}

			mc.getTextureManager().bindTexture(WIDGETS_LOCATION);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			blit(x + (int)(sliderValue * (width - 8)), y, 0, 46 + offset, 4, 20);
			blit(x + (int)(sliderValue * (width - 8)) + 4, y, 196, 46 + offset, 4, 20);
		}
	}

	@Override
	public void onRelease(double mouseX, double mouseY)
	{
		super.onRelease(mouseX, mouseY);

		if(consumer != null)
			consumer.accept(this);
	}

	public String getBlockName()
	{
		return blockName;
	}
}
