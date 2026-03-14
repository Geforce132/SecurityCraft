package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

// copy from vanilla's Checkbox to be able to change the text color and remove the shadow
public class CallbackCheckbox extends AbstractButton {
	private static final Identifier CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = SecurityCraft.mcResLoc("widget/checkbox_selected_highlighted");
	private static final Identifier CHECKBOX_SELECTED_SPRITE = SecurityCraft.mcResLoc("widget/checkbox_selected");
	private static final Identifier CHECKBOX_HIGHLIGHTED_SPRITE = SecurityCraft.mcResLoc("widget/checkbox_highlighted");
	private static final Identifier CHECKBOX_SPRITE = SecurityCraft.mcResLoc("widget/checkbox");
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
	public void onPress(InputWithModifiers input) {
		setSelected(!selected);
	}

	@Override
	public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		Identifier sprite;

		if (selected)
			sprite = isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
		else
			sprite = isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;

		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height);
		guiGraphics.text(minecraft.font, getMessage(), getX() + (int) (width * 1.2F), getY() + (height - 8) / 2, textColor | Mth.ceil(alpha * 255.0F) << 24, false);
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
