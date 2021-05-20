package net.geforcemods.securitycraft.gui.components;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiPictureButton extends ClickButton{

	private final RenderItem itemRenderer;
	private Block blockToRender;
	private Item itemToRender;
	private ResourceLocation textureLocation;
	private int u;
	private int v;
	private int drawOffsetX;
	private int drawOffsetY;
	private int drawWidth;
	private int drawHeight;
	private int textureWidth;
	private int textureHeight;

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, RenderItem par7, ItemStack itemToRender)
	{
		this(id, xPos, yPos, width, height, par7, itemToRender, null);
	}

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int drawOffsetX, int drawOffsetY, int drawWidth, int drawHeight, int textureWidth, int textureHeight)
	{
		this(id, xPos, yPos, width, height, texture, textureX, textureY, drawOffsetX, drawOffsetY, drawWidth, drawHeight, textureWidth, textureHeight, null);
	}

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, RenderItem par7, ItemStack itemToRender, Consumer<ClickButton> onClick) {
		super(id, xPos, yPos, width, height, "", onClick);
		itemRenderer = par7;

		if(!itemToRender.isEmpty() && itemToRender.getItem().getTranslationKey().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(itemToRender.getItem());
		else
			this.itemToRender = itemToRender.getItem();
	}

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int drawOffsetX, int drawOffsetY, int drawWidth, int drawHeight, int textureWidth, int textureHeight, Consumer<ClickButton> onClick)
	{
		super(id, xPos, yPos, width, height, "", onClick);

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
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (visible)
		{
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, x, y, 0, 46 + getHoverState(hovered) * 20, width, height, 200, 20, 2, 3, 2, 2, zLevel);

			if(blockToRender != null){
				GlStateManager.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(blockToRender), x + 2, y + 3);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(blockToRender), x + 2, y + 3, "");
			}else if(itemToRender != null){
				GlStateManager.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(itemToRender), x + 2, y + 2);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(itemToRender), x + 2, y + 2, "");
				GlStateManager.disableLighting();
			}
			else if(getTextureLocation() != null)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(getTextureLocation());
				drawModalRectWithCustomSizedTexture(x + drawOffsetX, y + drawOffsetY, u, v, drawWidth, drawHeight, textureWidth, textureHeight);
			}
		}
	}

	public ResourceLocation getTextureLocation()
	{
		return textureLocation;
	}

	public Item getItemStack() {
		return (blockToRender != null ? Item.getItemFromBlock(blockToRender) : itemToRender);
	}
}
