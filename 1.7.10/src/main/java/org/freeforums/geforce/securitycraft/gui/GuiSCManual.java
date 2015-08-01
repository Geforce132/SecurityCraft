package org.freeforums.geforce.securitycraft.gui;

import java.util.List;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;

public class GuiSCManual extends GuiScreen {

	private ResourceLocation infoBookTexture = new ResourceLocation("securitycraft:textures/gui/infoBookTexture.png");
	private ResourceLocation infoBookTitlePage = new ResourceLocation("securitycraft:textures/gui/infoBookTitlePage.png");
	protected ResourceLocation skinTexture = new ResourceLocation("textures/entity/steve.png");
	private static ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
	
    private int currentPage = -1;
    private ItemStack[] recipe;
    
	public GuiSCManual() {
		super();
	}
	
	public void initGui(){		
		Keyboard.enableRepeatEvents(true);

        int i = (this.width - 256) / 2;
        byte b0 = 2;
        GuiSCManual.NextPageButton nextButton = new GuiSCManual.NextPageButton(1, i + 210, b0 + 158, true);
        GuiSCManual.NextPageButton prevButton = new GuiSCManual.NextPageButton(2, i + 16, b0 + 158, false);

        this.buttonList.add(nextButton);
        this.buttonList.add(prevButton);
    }
	
	public void drawScreen(int par1, int par2, float par3){		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if(this.currentPage == -1){
	    	this.mc.getTextureManager().bindTexture(infoBookTitlePage);
		}else{
	    	this.mc.getTextureManager().bindTexture(infoBookTexture);
		}
		
	    int k = (this.width - 256) / 2;
	    this.drawTexturedModalRect(k, 5, 0, 0, 256, 180);
	    
	    if(this.currentPage > -1){
	    	this.fontRendererObj.drawString(mod_SecurityCraft.instance.manualPages.get(currentPage).getItemName(), k + 39, 27, 0, false);	
	    	this.fontRendererObj.drawSplitString(mod_SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo(), k + 18, 45, 225, 0);	
	    }else{
	    	this.fontRendererObj.drawString("Getting started with SecurityCraft", k + 39, 27, 0, false);	
	    	this.fontRendererObj.drawString("by Geforce", k + 99, 159, 0, false);	
	    }
	    
	    for(int i = 0; i < this.buttonList.size(); i++){
            ((GuiButton) this.buttonList.get(i)).drawButton(this.mc, par1, par2);
        }
	    
	    if(this.currentPage > -1){
	    	GuiUtils.drawItemStackToGui(mc, mod_SecurityCraft.instance.manualPages.get(currentPage).getItem(), k + 19, 22, !(mod_SecurityCraft.instance.manualPages.get(currentPage).getItem() instanceof ItemBlock));
	    	
	    	if(recipe != null){
		    	for(int i = 0; i < 3; i++){
		    		for(int j = 0; j < 3; j++){
		    			if(((i * 3) + j) >= recipe.length){ break; }
		    			if(this.recipe[(i * 3) + j] == null){ continue; }
		    			
		    	    	GuiUtils.drawItemStackToGui(mc, this.recipe[(i * 3) + j].getItem(), (k + 100) + (j * 20), 115 + (i * 20), !(this.recipe[(i * 3) + j].getItem() instanceof ItemBlock));
		    		}
		    	}
	    	}
	    	
	    	//this.mc.getTextureManager().bindTexture(skinTexture);
	    	//cpw.mods.fml.client.config.GuiUtils.drawTexturedModalRect(125, 150, 0, 0, 256, 256, this.zLevel);
	    }
	    
	    this.updateButtons();
	}
	
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
	
	protected void keyTyped(char par1, int par2){
		super.keyTyped(par1, par2);
		
		if(par2 == Keyboard.KEY_LEFT && (this.currentPage - 1) > -2){
			this.currentPage--;
			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			this.updateRecipe();
		}else if(par2 == Keyboard.KEY_RIGHT && (this.currentPage + 1) < mod_SecurityCraft.instance.manualPages.size()){
			this.currentPage++;
			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			this.updateRecipe();
		}
	}
	
    protected void actionPerformed(GuiButton par1GuiButton){
    	if(par1GuiButton.id == 1 && (this.currentPage + 1) < mod_SecurityCraft.instance.manualPages.size()){
    		this.currentPage++;
    		this.updateRecipe();
    	}else if(par1GuiButton.id == 2 && (this.currentPage - 1) > -2){
    		this.currentPage--;
    		this.updateRecipe();
    	}
    	    	
    	this.updateButtons();
    }
    
    private void updateRecipe(){
    	if(this.currentPage < 0){ 
    		recipe = null; 
    		return;
    	}
    	
    	for(Object object : CraftingManager.getInstance().getRecipeList()){
			if(object instanceof ShapedRecipes){
				ShapedRecipes recipe = (ShapedRecipes) object;
				
				if(recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() == mod_SecurityCraft.instance.manualPages.get(currentPage).getItem()){
					this.recipe = recipe.recipeItems;
					break;
				}
			}else if(object instanceof ShapelessRecipes){
				ShapelessRecipes recipe = (ShapelessRecipes) object;

				if(recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() == mod_SecurityCraft.instance.manualPages.get(currentPage).getItem()){
					this.recipe = this.toItemStackArray(recipe.recipeItems);
					break;
				}
			}
			
			this.recipe = null;
		}
    }
    
    private ItemStack[] toItemStackArray(List items){
    	ItemStack[] array = new ItemStack[9];
    	
    	for(int i = 0; i < items.size(); i++){
    		array[i] = (ItemStack) items.get(i);
    	}
    	
    	return array;
    }
    
    private void updateButtons(){
    	if(this.currentPage == -1){
    		((NextPageButton) this.buttonList.get(1)).visible = false;
    	}else if(this.currentPage == mod_SecurityCraft.instance.manualPages.size() - 1){
    		((NextPageButton) this.buttonList.get(0)).visible = false;
    	}else{
    		((NextPageButton) this.buttonList.get(0)).visible = true;
    		((NextPageButton) this.buttonList.get(1)).visible = true;
    	}
    }

	@SideOnly(Side.CLIENT)
    static class NextPageButton extends GuiButton {
		private final boolean field_146151_o;

		public NextPageButton(int par1, int par2, int par3, boolean par4){
			super(par1, par2, par3, 23, 13, "");
			this.field_146151_o = par4;
		}

		/**
		 * Draws this button to the screen.
		 */
		public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_){
			if(this.visible){
				boolean flag = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				p_146112_1_.getTextureManager().bindTexture(bookGuiTextures);
				int k = 0;
				int l = 192;

				if(flag){
					k += 23;
				}

				if(!this.field_146151_o){
					l += 13;
				}

				this.drawTexturedModalRect(this.xPosition, this.yPosition, k, l, 23, 13);
			}
		}
	}

}
