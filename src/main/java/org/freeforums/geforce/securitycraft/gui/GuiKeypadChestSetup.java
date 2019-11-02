package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.freeforums.geforce.securitycraft.containers.ContainerKeypadChestSetup;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetKeypadCode;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class GuiKeypadChestSetup extends GuiContainer
{
    private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntityKeypadChest keypadInventory;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', ''};

	
    private GuiTextField textboxKeycode;
	private String currentString;
	private boolean flag = false;
	private GuiButton saveAndContinueButton;

	public GuiKeypadChestSetup(InventoryPlayer par1InventoryPlayer, TileEntityKeypadChest par2TileEntityKeypadChest)
    {
        super(new ContainerKeypadChestSetup(par1InventoryPlayer, par2TileEntityKeypadChest));
        this.keypadInventory = 	par2TileEntityKeypadChest;
    }
    
    public void initGui(){
    	super.initGui();
		Keyboard.enableRepeatEvents(true);

    	int j = (this.height - this.height) / 2;
		this.buttonList.add(this.saveAndContinueButton = new GuiButton(0, this.width / 2 - 48, this.height / 2 + 30 + 10, 100, 20, !this.flag ? "Save & continue." : "Invalid code!"));
		

		this.textboxKeycode = new GuiTextField(this.fontRendererObj, this.width / 2 - 37, this.height / 2 - 47, 77, 12);
		
		this.textboxKeycode.setTextColor(-1);
		this.textboxKeycode.setDisabledTextColour(-1);
		this.textboxKeycode.setEnableBackgroundDrawing(true);
		this.textboxKeycode.setMaxStringLength(11);
				
		this.updateButtonText();
    }
    
    public void onGuiClosed(){
		super.onGuiClosed();
		this.flag = false;
		Keyboard.enableRepeatEvents(false);
	}
    
    
    public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.textboxKeycode.drawTextBox();
		this.drawString(this.fontRendererObj, "CODE:", this.width / 2 - 67, this.height / 2 - 47 + 2, 4210752);
    }

    protected void keyTyped(char par1, int par2){
		if(this.textboxKeycode.isFocused() && isValidChar(par1)){
			this.textboxKeycode.textboxKeyTyped(par1, par2);
		}else{
			super.keyTyped(par1, par2);
		}
	
	}
    
    private boolean isValidChar(char par1) {
		for(int x = 1; x <= this.allowedChars.length; x++){
			if(par1 == this.allowedChars[x - 1]){
				return true;
			}else{
				continue;
			}
		}
		
		return false;
	}
    
    protected void mouseClicked(int par1, int par2, int par3){
		super.mouseClicked(par1, par2, par3);
		this.textboxKeycode.mouseClicked(par1, par2, par3);
	}
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString("Password-protected chest setup", this.xSize / 2 - this.fontRendererObj.getStringWidth("Password-protected chest setup") / 2, 6, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);     
    }
    
    private void updateButtonText(){
       this.saveAndContinueButton.displayString = !this.flag ? "Save & continue." : "Invalid code!";
    }	
    
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
		case 0:
			try{							
				mod_SecurityCraft.network.sendToServer(new PacketSetKeypadCode(keypadInventory.xCoord, keypadInventory.yCoord, keypadInventory.zCoord, this.textboxKeycode.getText()));						
			}catch(Exception e){
				this.flag  = true;
				this.updateButtonText();
				e.printStackTrace();
				return;
			}	
		      
			Minecraft.getMinecraft().thePlayer.closeScreen();
			Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, 13, Minecraft.getMinecraft().theWorld, keypadInventory.xCoord, keypadInventory.yCoord, keypadInventory.zCoord);
		    
		}
	}	

}
