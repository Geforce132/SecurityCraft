package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.gui.ScreenUtils;

public class PictureButton extends Button {
	private final ItemRenderer itemRenderer;
	private ItemStack blockToRender = ItemStack.EMPTY;
	private ItemStack itemToRender = ItemStack.EMPTY;
	private ResourceLocation textureLocation;
	private int u;
	private int v;
	private int drawOffsetX;
	private int drawOffsetY;
	private int drawWidth;
	private int drawHeight;
	private int textureWidth;
	private int textureHeight;

	public PictureButton(int xPos, int yPos, int width, int height, ItemRenderer itemRenderer, ItemStack itemToRender) {
		this(xPos, yPos, width, height, itemRenderer, itemToRender, b -> {});
	}

	public PictureButton(int xPos, int yPos, int width, int height, ItemRenderer itemRenderer, ItemStack itemToRender, OnPress onPress) {
		super(xPos, yPos, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
		this.itemRenderer = itemRenderer;

		if (!itemToRender.isEmpty() && itemToRender.getItem() instanceof BlockItem)
			blockToRender = new ItemStack(Block.byItem(itemToRender.getItem()));
		else
			this.itemToRender = new ItemStack(itemToRender.getItem());
	}

	public PictureButton(int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int drawOffsetX, int drawOffsetY, int drawWidth, int drawHeight, int textureWidth, int textureHeight, OnPress onPress) {
		super(xPos, yPos, width, height, Component.empty(), onPress, DEFAULT_NARRATION);

		itemRenderer = null;
		textureLocation = texture;
		u = textureX;
		v = textureY;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.drawOffsetX = drawOffsetX;
		this.drawOffsetY = drawOffsetY;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			Minecraft mc = Minecraft.getInstance();
			Font font = mc.font;

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			ScreenUtils.blitWithBorder(pose, WIDGETS_LOCATION, getX(), getY(), 0, 46 + (!active ? 0 : (isHoveredOrFocused() ? 40 : 20)), width, height, 200, 20, 2, 3, 2, 2, 0);

			if (!blockToRender.isEmpty()) {
				itemRenderer.renderAndDecorateItem(pose, blockToRender, getX() + 2, getY() + 3);
				itemRenderer.renderGuiItemDecorations(pose, font, blockToRender, getX() + 2, getY() + 3, "");
			}
			else if (!itemToRender.isEmpty()) {
				itemRenderer.renderAndDecorateItem(pose, itemToRender, getX() + 2, getY() + 2);
				itemRenderer.renderGuiItemDecorations(pose, font, itemToRender, getX() + 2, getY() + 2, "");
			}
			else {
				ResourceLocation texture = getTextureLocation();

				if (texture != null) {
					RenderSystem._setShaderTexture(0, texture);
					blit(pose, getX() + drawOffsetX, getY() + drawOffsetY, drawWidth, drawHeight, u, v, drawWidth, drawHeight, textureWidth, textureHeight);
				}
			}
		}
	}

	public ResourceLocation getTextureLocation() {
		return textureLocation;
	}

	public ItemStack getItemStack() {
		return !blockToRender.isEmpty() ? blockToRender : itemToRender;
	}
}
