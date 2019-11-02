package net.geforcemods.securitycraft.gui.components;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
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

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, RenderItem renderItem, ItemStack itemToRender) {
		super(id, xPos, yPos, width, height, "");
		itemRenderer = renderItem;

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
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (visible)
		{
			FontRenderer fontRenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int hoverState = getHoverState(hovered);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			drawTexturedModalRect(xPosition, yPosition, 0, 46 + hoverState * 20, width / 2, height);
			drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + hoverState * 20, width / 2, height);

			if(blockToRender != null){
				GL11.glEnable(GL12.GL_RESCALE_NORMAL); //(this.width / 2) - 8
				itemRenderer.renderItemAndEffectIntoGUI(mc.fontRendererObj, mc.getTextureManager(), new ItemStack(blockToRender), xPosition + 2, yPosition + 2);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, mc.getTextureManager(), new ItemStack(blockToRender), xPosition + 2, yPosition + 2);
			}else if(itemToRender != null){
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				itemRenderer.renderItemAndEffectIntoGUI(mc.fontRendererObj, mc.getTextureManager(), new ItemStack(itemToRender), xPosition + 2, yPosition + 2);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, mc.getTextureManager(), new ItemStack(itemToRender), xPosition + 2, yPosition + 2);
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			else if(textureLocation != null)
			{
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(textureLocation);
				drawTexturedModalRect(xPosition, yPosition + 1, u, v, texWidth, texHeight);
			}

			mouseDragged(mc, mouseX, mouseY);

			int color = 14737632;

			if (!enabled)
				color = 10526880;
			else if (hovered)
				color = 16777120;

			drawCenteredString(fontRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, color);
		}
	}

	public void setDisplayItem(ItemStack stack){
		blockToRender = null;
		itemToRender = null;

		if(stack.getUnlocalizedName().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(stack.getItem());
		else
			itemToRender = stack.getItem();

	}

	public Item getItemStack() {
		return (blockToRender != null ? Item.getItemFromBlock(blockToRender) : itemToRender);
	}

}
