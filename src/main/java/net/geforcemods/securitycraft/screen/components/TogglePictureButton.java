package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TogglePictureButton extends Button implements IToggleableButton {
	private final ResourceLocation[] sprites;
	private final int toggleCount;
	private final int drawOffset;
	private final int drawWidth;
	private final int drawHeight;
	private int currentIndex = 0;

	public TogglePictureButton(int xPos, int yPos, int width, int height, int drawOffset, int drawWidth, int drawHeight, int toggleCount, OnPress onPress, ResourceLocation... sprites) {
		super(xPos, yPos, width, height, Component.empty(), onPress, DEFAULT_NARRATION);

		if (sprites.length != toggleCount)
			throw new IllegalArgumentException("TogglePictureButton was set up incorrectly. Amount of sprites must match toggleCount!");

		this.sprites = sprites;
		this.toggleCount = toggleCount;
		this.drawOffset = drawOffset;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			ResourceLocation sprite = getCurrentSprite();

			RenderSystem.setShader(CoreShaders.POSITION_TEX);
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			guiGraphics.blitSprite(RenderType::guiTextured, SPRITES.get(active, isHoveredOrFocused()), getX(), getY(), getWidth(), getHeight());

			if (sprite != null)
				guiGraphics.blitSprite(RenderType::guiTextured, sprite, getX() + drawOffset, getY() + drawOffset, drawWidth, drawHeight);
		}
	}

	@Override
	public void onPress() {
		if (Screen.hasShiftDown())
			setCurrentIndex(currentIndex - 1);
		else
			setCurrentIndex(currentIndex + 1);

		super.onPress();
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

	public ResourceLocation getCurrentSprite() {
		return sprites[getCurrentIndex()];
	}
}
