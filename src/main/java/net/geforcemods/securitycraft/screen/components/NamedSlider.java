package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class NamedSlider extends Slider {
	private Block block;
	private Consumer<NamedSlider> consumer;

	public NamedSlider(ITextComponent initialString, Block block, int xPos, int yPos, int width, int height, ITextComponent prefix, String suf, int minVal, int maxVal, int currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par, Consumer<NamedSlider> method) {
		super(xPos, yPos, width, height, prefix, new StringTextComponent(suf), minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(new StringTextComponent(initialString.getString()));
		this.block = block;
		consumer = method;
	}

	public NamedSlider(ITextComponent initialString, Block block, int xPos, int yPos, int width, int height, ITextComponent prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par, Consumer<NamedSlider> method) {
		super(xPos, yPos, width, height, prefix, new StringTextComponent(suf), minVal, maxVal, currentVal, showDec, drawStr, b -> {}, par);

		setMessage(new StringTextComponent(initialString.getString()));
		this.block = block;
		consumer = method;
	}

	@Override
	protected void renderBg(MatrixStack matrix, Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			int offset = (isHovered() && active ? 2 : 1) * 20;

			if (dragging) {
				sliderValue = (mouseX - (x + 4)) / (float) (width - 8);
				updateSlider();
			}

			mc.getTextureManager().bind(WIDGETS_LOCATION);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			blit(matrix, x + (int) (sliderValue * (width - 8)), y, 0, 46 + offset, 4, 20);
			blit(matrix, x + (int) (sliderValue * (width - 8)) + 4, y, 196, 46 + offset, 4, 20);
		}
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		super.onRelease(mouseX, mouseY);

		if (consumer != null)
			consumer.accept(this);
	}

	public Block getBlock() {
		return block;
	}
}
