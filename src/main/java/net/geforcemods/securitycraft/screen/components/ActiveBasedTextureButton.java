package net.geforcemods.securitycraft.screen.components;

import net.minecraft.resources.ResourceLocation;

public class ActiveBasedTextureButton extends PictureButton {
	private final ResourceLocation inactiveTexture;

	public ActiveBasedTextureButton(int xPos, int yPos, int width, int height, ResourceLocation texture, ResourceLocation inactiveTexture, int textureX, int textureY, int drawOffsetX, int drawOffsetY, int drawWidth, int drawHeight, int textureWidth, int textureHeight, OnPress onPress) {
		super(xPos, yPos, width, height, texture, textureX, textureY, drawOffsetX, drawOffsetY, drawWidth, drawHeight, textureWidth, textureHeight, onPress);

		this.inactiveTexture = inactiveTexture;
	}

	@Override
	public ResourceLocation getTextureLocation() {
		return active ? super.getTextureLocation() : inactiveTexture;
	}
}