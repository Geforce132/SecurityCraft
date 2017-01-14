package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class GuiLogger extends GuiContainer{
	
	private TileEntityLogger tileEntity;
    private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public GuiLogger(InventoryPlayer par1InventoryPlayer, TileEntityLogger par2TileEntityFurnace) {
        super(new ContainerGeneric(par1InventoryPlayer, par2TileEntityFurnace));
        this.tileEntity = par2TileEntityFurnace;
	}
	
	public void initGui(){
		super.initGui();
	}
	
    public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);

	}
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString(I18n.translateToLocal("gui.logger.logged"), this.xSize / 2 - this.fontRendererObj.getStringWidth("Logged players:") / 2, 6, 4210752);
        
        for(int i = 0; i < this.tileEntity.players.length; i++){
			if(this.tileEntity.players[i] != ""){
				this.fontRendererObj.drawString(this.tileEntity.players[i], this.xSize / 2 - this.fontRendererObj.getStringWidth(this.tileEntity.players[i]) / 2, 25 + (10 * i), 4210752);
			}
		}
    
    }

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

}
