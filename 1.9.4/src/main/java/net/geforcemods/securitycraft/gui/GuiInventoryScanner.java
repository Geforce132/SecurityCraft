package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.containers.ContainerInventoryScanner;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.packets.PacketSetISType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInventoryScanner extends GuiContainer {
	private static final ResourceLocation regularInventory = new ResourceLocation("securitycraft:textures/gui/container/inventoryScannerGUI.png");
	private static final ResourceLocation exhancedInventory = new ResourceLocation("securitycraft:textures/gui/container/inventoryScannerEnhancedGUI.png");

	private TileEntityInventoryScanner tileEntity;
    private EntityPlayer playerObj;
    private boolean hasStorageModule = false;
    
    public GuiInventoryScanner(IInventory par1IInventory, TileEntityInventoryScanner par2TileEntity, EntityPlayer par3EntityPlayer){
        super(new ContainerInventoryScanner(par1IInventory, par2TileEntity));
        this.tileEntity = par2TileEntity;
        this.playerObj = par3EntityPlayer;
        this.hasStorageModule = ((CustomizableSCTE) par2TileEntity).hasModule(EnumCustomModules.STORAGE);
        
        if(this.hasStorageModule){
        	this.xSize = 234;
        }else{ // 56
        	this.xSize = 176;
        }
        
        this.ySize = 196;
    }
    
    @Override
	public void initGui(){
    	super.initGui();
    	Keyboard.enableRepeatEvents(true); 		
    		
		if(tileEntity.getOwner().isOwner(playerObj)){
			this.buttonList.add(new GuiButton(0, this.width / 2 - 83 - (hasStorageModule ? 28 : 0), this.height / 2 - 63, 166, 20, this.tileEntity.getType().contains("check") ? I18n.translateToLocal("gui.invScan.checkInv") : I18n.translateToLocal("gui.invScan.emitRedstone")));
		}
    }
    
    @Override
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);

		if(!this.buttonList.isEmpty()){
			this.fontRendererObj.drawString(I18n.translateToLocal("gui.invScan.explanation.1"), this.width / 2 - 83 - (hasStorageModule ? 28 : 0), this.height / 2 - 38, 4210752);
			this.fontRendererObj.drawString(I18n.translateToLocal("gui.invScan.explanation.2"), this.width / 2 - 83 - (hasStorageModule ? 28 : 0), this.height / 2 - 28, 4210752);
			
			if(this.buttonList.get(0).displayString.matches(I18n.translateToLocal("gui.invScan.checkInv"))){
				this.fontRendererObj.drawString(I18n.translateToLocal("gui.invScan.explanation.checkInv.3"), this.width / 2 - 83 - (hasStorageModule ? 28 : 0), this.height / 2 - 18, 4210752);
				this.fontRendererObj.drawString(I18n.translateToLocal("gui.invScan.explanation.checkInv.4"), this.width / 2 - 83 - (hasStorageModule ? 28 : 0), this.height / 2 - 8, 4210752);
			}else{
				this.fontRendererObj.drawString(I18n.translateToLocal("gui.invScan.explanation.emitRedstone.3"), this.width / 2 - 83 - (hasStorageModule ? 28 : 0), this.height / 2 - 18, 4210752);
				this.fontRendererObj.drawString(I18n.translateToLocal("gui.invScan.explanation.emitRedstone.4"), this.width / 2 - 83 - (hasStorageModule ? 28 : 0), this.height / 2 - 8, 4210752);	
			}
		}else{
			if(this.tileEntity.getType() != null && this.tileEntity.getType() != ""){
				this.fontRendererObj.drawString(I18n.translateToLocal("gui.invScan.setTo"), this.width / 2 - 83, this.height / 2 - 61, 4210752);
				this.fontRendererObj.drawString((this.tileEntity.getType().matches("check") ? I18n.translateToLocal("gui.invScan.checkInv") : I18n.translateToLocal("gui.invScan.emitRedstone")), this.width / 2 - 83, this.height / 2 - 51, 4210752);
				
			}
		}
	
    }
    
    @Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
    
    @Override
	protected void keyTyped(char par1, int par2) throws IOException{
		super.keyTyped(par1, par2);		
    }
    
    @Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException{
		super.mouseClicked(par1, par2, par3);
		
	}
    
    @Override
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:
				if(guibutton.displayString.matches(I18n.translateToLocal("gui.invScan.checkInv"))){
					guibutton.displayString = I18n.translateToLocal("gui.invScan.emitRedstone");
				}else if(guibutton.displayString.matches(I18n.translateToLocal("gui.invScan.emitRedstone"))){
					guibutton.displayString = I18n.translateToLocal("gui.invScan.checkInv");
				}
				
				this.saveType(guibutton.displayString.matches(I18n.translateToLocal("gui.invScan.checkInv")) ? "check" : "redstone");
				
				break;
		}
		
    }
	
	private void saveType(String type){
		this.tileEntity.setType(type);
    	mod_SecurityCraft.network.sendToServer(new PacketSetISType(this.tileEntity.getPos().getX(), this.tileEntity.getPos().getY(), this.tileEntity.getPos().getZ(), type));
		
	}

	/**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString("Prohibited Items", 8, 6, 4210752);
        this.fontRendererObj.drawString(tileEntity.getOwner().isOwner(playerObj) ? (TextFormatting.UNDERLINE + I18n.translateToLocal("gui.invScan.mode.admin")) : (TextFormatting.UNDERLINE + I18n.translateToLocal("gui.invScan.mode.view")), 112, 6, 4210752);
        
        if(hasStorageModule && tileEntity.getOwner().isOwner(playerObj)){
        	this.fontRendererObj.drawString("Storage", 183, 6, 4210752);
        }
        
        this.fontRendererObj.drawString(I18n.translateToLocalFormatted("container.inventory", new Object[0]), 8, this.ySize - 93, 4210752);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(hasStorageModule){
			this.mc.getTextureManager().bindTexture(exhancedInventory);
		}else{
			this.mc.getTextureManager().bindTexture(regularInventory);
		}
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize + 30);
	}
}
