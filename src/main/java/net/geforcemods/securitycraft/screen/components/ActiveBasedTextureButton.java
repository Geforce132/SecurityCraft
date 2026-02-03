package net.geforcemods.securitycraft.screen.components;

import net.minecraft.resources.ResourceLocation;

public class ActiveBasedTextureButton extends PictureButton {
	private final ResourceLocation inactiveSprite;

	public ActiveBasedTextureButton(int xPos, int yPos, int width, int height, ResourceLocation sprite, ResourceLocation inactiveSprite, int drawOffsetX, int drawOffsetY, int drawWidth, int drawHeight, OnPress onPress) {
		super(xPos, yPos, width, height, sprite, drawOffsetX, drawOffsetY, drawWidth, drawHeight, onPress);

		this.inactiveSprite = inactiveSprite;
	}

	@Override
	public ResourceLocation getSpriteLocation() {
		return active ? super.getSpriteLocation() : inactiveSprite;
	}
}