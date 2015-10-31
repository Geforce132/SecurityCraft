package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.Utils.ClientUtils;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiIMS extends GuiContainer{
	
    private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private TileEntityIMS tileEntity;
    private GuiButton targetButton;
    private int targetingOption = 0;

	public GuiIMS(InventoryPlayer par1InventoryPlayer, TileEntityIMS par2TileEntity) {
        super(new ContainerGeneric(par1InventoryPlayer, par2TileEntity));
        this.tileEntity = par2TileEntity;
        this.targetingOption = tileEntity.getTargetingOption();
	}
	
	public void initGui(){
		super.initGui();
		
		this.buttonList.add(this.targetButton = new GuiButton(0, this.width / 2 - 38, this.height / 2 - 58, 120, 20, tileEntity.getTargetingOption() == 1 ? StatCollector.translateToLocal("gui.ims.hostileAndPlayers") : StatCollector.translateToLocal("tooltip.module.players")));
	}
	
    public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
	}
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2){
        this.fontRendererObj.drawString(StatCollector.translateToLocal("tile.ims.name"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("tile.ims.name")) / 2, 6, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.ims.target"), this.xSize / 2 - 78, 30, 4210752);
    }

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
	
	protected void actionPerformed(GuiButton guibutton){
    	switch(guibutton.id){
    	case 0:
    		targetingOption++;
    		
    		if(targetingOption > 1){
    			targetingOption = 0;
    		}
    		    		
    		tileEntity.setTargetingOption(targetingOption);
    		
        	ClientUtils.syncTileEntity(tileEntity);
    		
    		this.updateButtonText();
    	}
    }

	private void updateButtonText() {
		if(this.targetingOption == 0){
			targetButton.displayString = StatCollector.translateToLocal("tooltip.module.players");
		}else if(this.targetingOption == 1){
			targetButton.displayString = StatCollector.translateToLocal("gui.ims.hostileAndPlayers");
		}
	}	

}
