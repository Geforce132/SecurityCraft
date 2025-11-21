package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TogglePictureButton extends Button.Plain implements IToggleableButton {
	private final Identifier[] sprites;
	private final int toggleCount;
	private final int drawOffset;
	private final int drawWidth;
	private final int drawHeight;
	private int currentIndex = 0;

	public TogglePictureButton(int xPos, int yPos, int width, int height, int drawOffset, int drawWidth, int drawHeight, int toggleCount, OnPress onPress, Identifier... sprites) {
		super(xPos, yPos, width, height, Component.empty(), onPress, DEFAULT_NARRATION);

		if (sprites.length != toggleCount)
			throw new IllegalArgumentException("TogglePictureButton was set up incorrectly. Amount of sprites must match toggleCount!");

		this.sprites = sprites;
		this.toggleCount = toggleCount;
		this.drawOffset = drawOffset;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
	}

	//TODO: Test
	@Override
	public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.renderContents(guiGraphics, mouseX, mouseX, partialTicks);

		Identifier sprite = getCurrentSprite();

		if (sprite != null)
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + drawOffset, getY() + drawOffset, drawWidth, drawHeight);
	}

	@Override
	public void onPress(InputWithModifiers input) {
		if (Minecraft.getInstance().hasShiftDown())
			setCurrentIndex(currentIndex - 1);
		else
			setCurrentIndex(currentIndex + 1);

		super.onPress(input);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		setCurrentIndex(currentIndex - (int) Math.signum(scrollY));
		onPress.onPress(this);
		return true;
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void setCurrentIndex(int newIndex) {
		currentIndex = Math.floorMod(newIndex, toggleCount);
	}

	public Identifier getCurrentSprite() {
		return sprites[getCurrentIndex()];
	}
}
