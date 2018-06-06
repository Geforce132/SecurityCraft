package net.geforcemods.securitycraft.gui.components;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiPictureButton extends GuiButton{

	private final RenderItem itemRenderer;
	private Block blockToRender;
	private Item itemToRender;
	private ResourceLocation textureLocation;
	private int u;
	private int v;
	private int texWidth;
	private int texHeight;

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, RenderItem par7, ItemStack itemToRender) {
		super(id, xPos, yPos, width, height, "");
		itemRenderer = par7;

		if(itemToRender != null && itemToRender.getItem().getUnlocalizedName().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(itemToRender.getItem());
		else
			this.itemToRender = itemToRender.getItem();
	}

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int textureWidth, int textureHeight)
	{
		super(id, xPos, yPos, width, height, "");

		itemRenderer = null;
		textureLocation = texture;
		u = textureX;
		v = textureY;
		texWidth = textureWidth;
		texHeight = textureHeight;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft par1, int par2, int par3)
	{
		if (visible)
		{
			FontRenderer var4 = par1.fontRendererObj;
			par1.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = par2 >= xPosition && par3 >= yPosition && par2 < xPosition + width && par3 < yPosition + height;
			int var5 = getHoverState(hovered);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(xPosition, yPosition, 0, 46 + var5 * 20, width / 2, height);
			this.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + var5 * 20, width / 2, height);

			if(blockToRender != null){
				GlStateManager.enableRescaleNormal(); //(this.width / 2) - 8
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(blockToRender), xPosition + 2, yPosition + 3);
				itemRenderer.renderItemOverlayIntoGUI(par1.fontRendererObj, new ItemStack(blockToRender), xPosition + 2, yPosition + 3, "");
				GlStateManager.disableRescaleNormal();
			}else if(itemToRender != null){
				GlStateManager.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(itemToRender), xPosition + 2, yPosition + 2);
				itemRenderer.renderItemOverlayIntoGUI(par1.fontRendererObj, new ItemStack(itemToRender), xPosition + 2, yPosition + 2, "");
				GlStateManager.disableRescaleNormal();
			}
			else if(textureLocation != null)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				par1.getTextureManager().bindTexture(textureLocation);
				drawTexturedModalRect(xPosition, yPosition + 1, u, v, texWidth, texHeight);
			}

			mouseDragged(par1, par2, par3);

			int var6 = 14737632;

			if (!enabled)
				var6 = 10526880;
			else if (hovered)
				var6 = 16777120;

			drawCenteredString(var4, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, var6);
			GlStateManager.disableBlend();
		}
	}

	public void setDisplayItem(ItemStack par1ItemStack){
		blockToRender = null;
		itemToRender = null;

		if(par1ItemStack.getUnlocalizedName().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(par1ItemStack.getItem());
		else
			itemToRender = par1ItemStack.getItem();

	}

	public Item getItemStack() {
		return (blockToRender != null ? Item.getItemFromBlock(blockToRender) : itemToRender);
	}

}
