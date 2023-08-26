package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class CallbackCheckbox extends ClickButton implements IToggleableButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft", "textures/gui/checkbox.png");
	private boolean selected;
	private final Consumer<Boolean> onChange;
	private final int textColor;

	public CallbackCheckbox(int id, int x, int y, int width, int height, String message, boolean selected, Consumer<Boolean> onChange, int textColor) {
		super(id, x, y, width, height, message, null);

		this.selected = selected;
		this.onChange = onChange;
		this.textColor = textColor;
	}

	@Override
	public void onClick() {
		setSelected(!selected);
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			minecraft.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			drawScaledCustomSizeModalRect(x, y, 0, selected ? 20.0F : 0.0F, 20, 20, width, height, 32, 64);
			minecraft.fontRenderer.drawString(displayString, x + (int) (width * 1.2F), y + (height - 8) / 2, textColor | MathHelper.ceil(255.0F) << 24);
		}
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		onChange.accept(selected);
	}

	public boolean selected() {
		return selected;
	}

	@Override
	public int getCurrentIndex() {
		return selected() ? 1 : 0;
	}

	@Override
	public void setCurrentIndex(int newIndex) {
		setSelected(newIndex == 1);
	}
}
