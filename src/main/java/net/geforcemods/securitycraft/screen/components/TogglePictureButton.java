package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class TogglePictureButton extends ExtendedButton implements IToggleableButton {
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

	public TogglePictureButton(int xPos, int yPos, int width, int height, ResourceLocation texture, int[] textureX, int[] textureY, int drawOffset, int toggleCount, OnPress onPress) {
		this(xPos, yPos, width, height, texture, textureX, textureY, drawOffset, 16, 16, 16, 16, 256, 256, toggleCount, onPress);
	}

	public TogglePictureButton(int xPos, int yPos, int width, int height, ResourceLocation texture, int[] textureX, int[] textureY, int drawOffset, int drawWidth, int drawHeight, int uWidth, int vHeight, int textureWidth, int textureHeight, int toggleCount, OnPress onPress) {
		super(xPos, yPos, width, height, Component.empty(), onPress);

		if (textureX.length != toggleCount || textureY.length != toggleCount)
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
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			ScreenUtils.blitWithBorder(pose, WIDGETS_LOCATION, x, y, 0, 46 + getYImage(isHoveredOrFocused()) * 20, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());

			if (getTextureLocation() != null) {
				RenderSystem._setShaderTexture(0, getTextureLocation());
				blit(pose, x + drawOffset, y + drawOffset, drawWidth, drawHeight, u[currentIndex], v[currentIndex], uWidth, vHeight, textureWidth, textureHeight);
			}
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
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		setCurrentIndex(currentIndex - (int) Math.signum(delta));
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

	public ResourceLocation getTextureLocation() {
		return textureLocation;
	}
}
