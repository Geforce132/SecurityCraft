package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

// copy from vanilla's Checkbox to be able to change the text color and remove the shadow
public class CallbackCheckbox extends AbstractButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
	private boolean selected;
	private final Consumer<Boolean> onChange;
	private final int textColor;

	public CallbackCheckbox(int x, int y, int width, int height, Component message, boolean selected, Consumer<Boolean> onChange, int textColor) {
		super(x, y, width, height, message);

		this.selected = selected;
		this.onChange = onChange;
		this.textColor = textColor;
	}

	@Override
	public void onPress() {
		setSelected(!selected);
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();

		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		guiGraphics.blit(TEXTURE, getX(), getY(), width, height, isFocused() ? 20.0F : 0.0F, selected ? 20.0F : 0.0F, 20, 20, 64, 64);
		guiGraphics.drawString(minecraft.font, getMessage(), getX() + (int)(width * 1.2F), getY() + (height - 8) / 2, textColor | Mth.ceil(alpha * 255.0F) << 24, false);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		narrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage());

		if (active) {
			if (isFocused())
				narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
			else
				narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
		}
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		onChange.accept(selected);
	}

	public boolean selected() {
		return selected;
	}
}
