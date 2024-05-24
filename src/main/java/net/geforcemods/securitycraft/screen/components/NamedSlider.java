package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class NamedSlider extends Slider {
	private final String denotation;
	private final Consumer<NamedSlider> onApplyValue;

	public NamedSlider(ITextComponent initialString, String denotation, int x, int y, int width, int height, ITextComponent prefix, String suffix, int minValue, int maxValue, int currentValue, boolean showDec, boolean drawString, @Nullable ISlider par, Consumer<NamedSlider> onApplyValue) {
		super(x, y, width, height, prefix, new StringTextComponent(suffix), minValue, maxValue, currentValue, showDec, drawString, b -> {}, par);

		setMessage(new StringTextComponent(initialString.getString()));
		this.denotation = denotation;
		this.onApplyValue = onApplyValue;
	}

	public NamedSlider(ITextComponent initialString, String denotation, int x, int y, int width, int height, ITextComponent prefix, String suffix, double minValue, double maxValue, double currentValue, boolean showDec, boolean drawString, @Nullable ISlider par, Consumer<NamedSlider> onApplyValue) {
		super(x, y, width, height, prefix, new StringTextComponent(suffix), minValue, maxValue, currentValue, showDec, drawString, b -> {}, par);

		setMessage(new StringTextComponent(initialString.getString()));
		this.denotation = denotation;
		this.onApplyValue = onApplyValue;
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

		if (onApplyValue != null)
			onApplyValue.accept(this);
	}

	public String getDenotation() {
		return denotation;
	}
}
