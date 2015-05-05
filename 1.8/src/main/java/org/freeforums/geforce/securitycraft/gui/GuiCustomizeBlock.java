package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.containers.ContainerCustomizeBlock;
import org.freeforums.geforce.securitycraft.gui.components.GuiPictureButton;
import org.freeforums.geforce.securitycraft.main.Utils.ModuleUtils;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiCustomizeBlock extends GuiContainer{
	
    private CustomizableSCTE tileEntity;
    private HoverChecker[] hoverCheckers = new HoverChecker[5];

    public GuiCustomizeBlock(InventoryPlayer par1InventoryPlayer, CustomizableSCTE par2TileEntity)
    {
        super(new ContainerCustomizeBlock(par1InventoryPlayer, par2TileEntity));
        this.tileEntity = par2TileEntity;
    }
    
    public void initGui(){
    	super.initGui();

    	for(int i = 0; i < tileEntity.getNumberOfCustomizableOptions(); i++){
    		GuiPictureButton button = new GuiPictureButton(i, 265, 45 + (i * 25), 20, 20, "", this.itemRender, new ItemStack(ModuleUtils.getItemFromModule(tileEntity.getCustomizableOptions()[i])), "");
    		this.buttonList.add(button);
    		this.hoverCheckers[i] = new HoverChecker(button, 20);
    	}
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	
    	for(int i = 0; i < hoverCheckers.length; i++){
    		if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)){
    			this.drawHoveringText(this.mc.fontRendererObj.listFormattedStringToWidth(tileEntity.getOptionDescriptions()[i], 150), mouseX, mouseY);
    		}
    	}
    }
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String s = this.tileEntity.hasCustomName() ? this.tileEntity.getName() : I18n.format(this.tileEntity.getName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("securitycraft:textures/gui/container/customize" + tileEntity.getNumberOfCustomizableOptions() + ".png"));
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}