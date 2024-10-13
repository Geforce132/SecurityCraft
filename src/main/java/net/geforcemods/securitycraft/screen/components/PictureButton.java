package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class PictureButton extends Button {
	private ItemStack blockToRender = ItemStack.EMPTY;
	private ItemStack itemToRender = ItemStack.EMPTY;
	private ResourceLocation sprite;
	private int drawOffsetX;
	private int drawOffsetY;
	private int drawWidth;
	private int drawHeight;

	public PictureButton(int xPos, int yPos, int width, int height, ItemStack itemToRender) {
		this(xPos, yPos, width, height, itemToRender, b -> {});
	}

	public PictureButton(int xPos, int yPos, int width, int height, ItemStack itemToRender, OnPress onPress) {
		super(xPos, yPos, width, height, Component.empty(), onPress, DEFAULT_NARRATION);

		if (!itemToRender.isEmpty() && itemToRender.getItem() instanceof BlockItem)
			blockToRender = new ItemStack(Block.byItem(itemToRender.getItem()));
		else
			this.itemToRender = new ItemStack(itemToRender.getItem());
	}

	public PictureButton(int xPos, int yPos, int width, int height, ResourceLocation sprite, int drawOffsetX, int drawOffsetY, int drawWidth, int drawHeight, OnPress onPress) {
		super(xPos, yPos, width, height, Component.empty(), onPress, DEFAULT_NARRATION);

		this.sprite = sprite;
		this.drawOffsetX = drawOffsetX;
		this.drawOffsetY = drawOffsetY;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			Minecraft mc = Minecraft.getInstance();
			Font font = mc.font;

			RenderSystem.setShader(CoreShaders.POSITION_TEX);
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			guiGraphics.blitSprite(RenderType::guiTextured, SPRITES.get(active, isHoveredOrFocused()), getX(), getY(), getWidth(), getHeight());

			if (!blockToRender.isEmpty()) {
				guiGraphics.renderItem(blockToRender, getX() + 2, getY() + 3);
				guiGraphics.renderItemDecorations(font, blockToRender, getX() + 2, getY() + 3, "");
			}
			else if (!itemToRender.isEmpty()) {
				guiGraphics.renderItem(itemToRender, getX() + 2, getY() + 2);
				guiGraphics.renderItemDecorations(font, itemToRender, getX() + 2, getY() + 2, "");
			}
			else if (getSpriteLocation() != null)
				guiGraphics.blitSprite(RenderType::guiTextured, getSpriteLocation(), getX() + drawOffsetX, getY() + drawOffsetY, drawWidth, drawHeight);
		}
	}

	public ResourceLocation getSpriteLocation() {
		return sprite;
	}

	public ItemStack getItemStack() {
		return !blockToRender.isEmpty() ? blockToRender : itemToRender;
	}
}
