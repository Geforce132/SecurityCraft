package net.geforcemods.securitycraft.gui.components;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TogglePictureButton extends ClickButton {

	private Block blockToRender;
	private Item itemToRender;
	private ResourceLocation textureLocation;
	private int[] u;
	private int[] v;
	private int currentIndex = 0;
	private final int toggleCount;

	public TogglePictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int[] textureX, int[] textureY, int toggleCount, Consumer<ClickButton> onClick)
	{
		super(id, xPos, yPos, width, height, "", onClick);

		if(textureX.length != toggleCount || textureY.length != toggleCount)
			throw new RuntimeException("GuiTogglePictureButton was set up incorrectly. Array lengths must match toggleCount!");

		this.toggleCount = toggleCount;
		textureLocation = texture;
		u = textureX;
		v = textureY;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (visible)
		{
			FontRenderer fontRenderer = mc.fontRenderer;
			RenderItem itemRenderer = mc.getRenderItem();
			int hoverState = getHoverState(hovered);

			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			GlStateManager.enableBlend();
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			drawTexturedModalRect(x, y, 0, 46 + hoverState * 20, width / 2, height);
			drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 46 + hoverState * 20, width / 2, height);

			if(blockToRender != null){
				GlStateManager.enableRescaleNormal(); //(this.width / 2) - 8
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(blockToRender), x + 2, y + 3);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(blockToRender), x + 2, y + 3, "");
			}else if(itemToRender != null){
				GlStateManager.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(itemToRender), x + 2, y + 2);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(itemToRender), x + 2, y + 2, "");
				GlStateManager.disableLighting();
			}
			else if(textureLocation != null)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(textureLocation);
				drawTexturedModalRect(x + 2, y + 2, u[currentIndex], v[currentIndex], 16, 16);
			}

			int color = 0xe0e0e0;

			if (!enabled)
				color = 0xa0a0a0;
			else if (hovered)
				color = 0xffffa0;

			drawCenteredString(fontRenderer, displayString, x + width / 2, y + (height - 8) / 2, color);
		}
	}

	@Override
	public void onClick()
	{
		setCurrentIndex(currentIndex + 1);
		super.onClick();
	}

	public int getCurrentIndex()
	{
		return currentIndex;
	}

	public void setCurrentIndex(int newIndex)
	{
		currentIndex = newIndex % toggleCount;
	}

	public Item getItemStack() {
		return (blockToRender != null ? Item.getItemFromBlock(blockToRender) : itemToRender);
	}
}
