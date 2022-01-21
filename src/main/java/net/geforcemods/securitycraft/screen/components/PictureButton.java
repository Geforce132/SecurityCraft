package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmlclient.gui.GuiUtils;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;

public class PictureButton extends ExtendedButton {
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
		super(xPos, yPos, width, height, TextComponent.EMPTY, onPress);
		this.itemRenderer = itemRenderer;

		if (!itemToRender.isEmpty() && itemToRender.getItem() instanceof BlockItem)
			blockToRender = new ItemStack(Block.byItem(itemToRender.getItem()));
		else
			this.itemToRender = new ItemStack(itemToRender.getItem());
	}

	public PictureButton(int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int drawOffsetX, int drawOffsetY, int drawWidth, int drawHeight, int textureWidth, int textureHeight, OnPress onPress) {
		super(xPos, yPos, width, height, TextComponent.EMPTY, onPress);

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
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			GuiUtils.drawContinuousTexturedBox(pose, WIDGETS_LOCATION, x, y, 0, 46 + getYImage(isHovered()) * 20, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());

			if (!blockToRender.isEmpty()) {
				itemRenderer.renderAndDecorateItem(blockToRender, x + 2, y + 3);
				itemRenderer.renderGuiItemDecorations(font, blockToRender, x + 2, y + 3, "");
			}
			else if (!itemToRender.isEmpty()) {
				itemRenderer.renderAndDecorateItem(itemToRender, x + 2, y + 2);
				itemRenderer.renderGuiItemDecorations(font, itemToRender, x + 2, y + 2, "");
			}
			else {
				ResourceLocation texture = getTextureLocation();

				if (texture != null) {
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem._setShaderTexture(0, texture);
					blit(pose, x + drawOffsetX, y + drawOffsetY, drawWidth, drawHeight, u, v, drawWidth, drawHeight, textureWidth, textureHeight);
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
