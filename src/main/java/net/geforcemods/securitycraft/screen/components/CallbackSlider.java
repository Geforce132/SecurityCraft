package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class CallbackSlider extends ForgeSlider {
	private static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");
	private final Consumer<CallbackSlider> onApplyValue;

	public CallbackSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString, Consumer<CallbackSlider> onApplyValue) {
		this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1.0D, 0, drawString, onApplyValue);
	}

	public CallbackSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, Consumer<CallbackSlider> onApplyValue) {
		super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
		this.onApplyValue = onApplyValue;
	}

	@Override
	public void renderWidget(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		Minecraft minecraft = Minecraft.getInstance();
		int i = active ? 0xFFFFFF : 0xA0A0A0;

		RenderSystem.setShaderTexture(0, SLIDER_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		blitNineSliced(pose, getX(), getY(), getWidth(), getHeight(), 20, 4, 200, 20, 0, getTextureY());
		blitNineSliced(pose, getX() + (int) (value * (width - 8)), getY(), 8, 20, 20, 4, 200, 20, 0, getHandleTextureY());
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		renderScrollingString(pose, minecraft.font, 2, i | Mth.ceil(alpha * 255.0F) << 24);
	}

	@Override
	protected void applyValue() {
		super.applyValue();

		if (onApplyValue != null)
			onApplyValue.accept(this);
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
}
