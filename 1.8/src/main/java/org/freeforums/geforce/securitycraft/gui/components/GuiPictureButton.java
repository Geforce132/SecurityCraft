package org.freeforums.geforce.securitycraft.gui.components;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiPictureButton extends GuiButton{
	
	private final RenderItem itemRenderer;
	private Block blockToRender;
	private Item itemToRender;
	public String extraData;

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, String displayString, RenderItem par7, ItemStack itemToRender, String extraData) {
		super(id, xPos, yPos, width, height, displayString);
		this.itemRenderer = par7;
		this.extraData = extraData;
		
		if(itemToRender != null && itemToRender.getItem().getUnlocalizedName().startsWith("tile.")){
			this.blockToRender = Block.getBlockFromItem(itemToRender.getItem());
		}else{
			this.itemToRender = itemToRender.getItem();
		}
	}
	
	/**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1, int par2, int par3)
    {
        if (this.visible)
        {
            FontRenderer var4 = par1.fontRendererObj;
            par1.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.hovered);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var5 * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + var5 * 20, this.width / 2, this.height);
                       
            if(this.blockToRender != null){
	            GL11.glEnable(GL12.GL_RESCALE_NORMAL); //(this.width / 2) - 8
	            itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(this.blockToRender), this.xPosition + 2, this.yPosition + 3);
	            itemRenderer.renderItemOverlayIntoGUI(par1.fontRendererObj, new ItemStack(this.blockToRender), this.xPosition + 2, this.yPosition + 3, "");
            }else{
	            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	            itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(this.itemToRender), this.xPosition + 2, this.yPosition + 2);
	            itemRenderer.renderItemOverlayIntoGUI(par1.fontRendererObj, new ItemStack(this.itemToRender), this.xPosition + 2, this.yPosition + 2, "");
	            GL11.glDisable(GL11.GL_LIGHTING);
            }

            this.mouseDragged(par1, par2, par3);

            int var6 = 14737632;

            
            if (!this.enabled)
            {
                var6 = 10526880;
            }
            else if (this.hovered)
            {
                var6 = 16777120;
            }
            
            this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
        
        }
    }
    
    public void setDisplayItem(ItemStack par1ItemStack){
    	this.blockToRender = null;
    	this.itemToRender = null;
		//if(this.itemToRender != null && par1ItemStack.getUnlocalizedName().startsWith("tile.")){
		if(par1ItemStack.getUnlocalizedName().startsWith("tile.")){
    		this.blockToRender = Block.getBlockFromItem(par1ItemStack.getItem());
		}else{
			this.itemToRender = par1ItemStack.getItem();
		}

    }

}
