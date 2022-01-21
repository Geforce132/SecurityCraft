package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
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

	public PictureButton(int xPos, int yPos, int width, int height, ItemRenderer itemRenderer, ItemStack itemToRender, IPressable onClick) {
		super(xPos, yPos, width, height, StringTextComponent.EMPTY, onClick);
		this.itemRenderer = itemRenderer;

		if (!itemToRender.isEmpty() && itemToRender.getItem() instanceof BlockItem)
			blockToRender = new ItemStack(Block.getBlockFromItem(itemToRender.getItem()));
		else
			this.itemToRender = new ItemStack(itemToRender.getItem());
	}

	public PictureButton(int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int drawOffsetX, int drawOffsetY, int drawWidth, int drawHeight, int textureWidth, int textureHeight, IPressable onClick) {
		super(xPos, yPos, width, height, StringTextComponent.EMPTY, onClick);

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
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			Minecraft mc = Minecraft.getInstance();
			FontRenderer font = mc.fontRenderer;

			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			GuiUtils.drawContinuousTexturedBox(matrix, WIDGETS_LOCATION, x, y, 0, 46 + getYImage(isHovered()) * 20, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());

			if (!blockToRender.isEmpty()) {
				RenderSystem.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(blockToRender, x + 2, y + 3);
				itemRenderer.renderItemOverlayIntoGUI(font, blockToRender, x + 2, y + 3, "");
			}
			else if (!itemToRender.isEmpty()) {
				RenderSystem.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(itemToRender, x + 2, y + 2);
				itemRenderer.renderItemOverlayIntoGUI(font, itemToRender, x + 2, y + 2, "");
				RenderSystem.disableLighting();
			}
			else {
				ResourceLocation texture = getTextureLocation();

				if (texture != null) {
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(texture);
					blit(matrix, x + drawOffsetX, y + drawOffsetY, drawWidth, drawHeight, u, v, drawWidth, drawHeight, textureWidth, textureHeight);
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
