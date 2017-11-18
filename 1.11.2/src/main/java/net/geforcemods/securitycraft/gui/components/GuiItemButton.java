package net.geforcemods.securitycraft.gui.components;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GuiItemButton extends GuiButton{

	private final RenderItem itemRenderer;
	private Block blockToRender;
	private Item itemToRender;

	public GuiItemButton(int id, int xPos, int yPos, int width, int height, String displayString, RenderItem par7, ItemStack itemToRender) {
		super(id, xPos, yPos, width, height, displayString);
		itemRenderer = par7;

		if(!itemToRender.isEmpty() && itemToRender.getItem().getUnlocalizedName().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(itemToRender.getItem());
		else
			this.itemToRender = itemToRender.getItem();
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
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = par2 >= xPosition && par3 >= yPosition && par2 < xPosition + width && par3 < yPosition + height;
			int var5 = getHoverState(hovered);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(xPosition, yPosition, 0, 46 + var5 * 20, width / 2, height);
			this.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + var5 * 20, width / 2, height);

			if(blockToRender != null){
				GL11.glEnable(GL12.GL_RESCALE_NORMAL); //(this.width / 2) - 8
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(blockToRender), xPosition + 2, yPosition + 3);
				itemRenderer.renderItemOverlayIntoGUI(par1.fontRendererObj, new ItemStack(blockToRender), xPosition + 2, yPosition + 3, "");
			}else{
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(itemToRender), xPosition + 2, yPosition + 2);
				itemRenderer.renderItemOverlayIntoGUI(par1.fontRendererObj, new ItemStack(itemToRender), xPosition + 2, yPosition + 2, "");
				GL11.glDisable(GL11.GL_LIGHTING);
			}

			mouseDragged(par1, par2, par3);

			int var6 = 14737632;


			if (!enabled)
				var6 = 10526880;
			else if (hovered)
				var6 = 16777120;

			drawCenteredString(var4, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, var6);

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
