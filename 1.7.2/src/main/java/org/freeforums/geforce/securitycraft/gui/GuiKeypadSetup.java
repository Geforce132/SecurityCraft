package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.freeforums.geforce.securitycraft.blocks.BlockKeypad;
import org.freeforums.geforce.securitycraft.containers.ContainerKeypad;
import org.freeforums.geforce.securitycraft.gui.components.GuiMovable;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetKeypadCode;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class GuiKeypadSetup extends GuiContainer
{
    private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntityKeypad keypadInventory;

	
    private GuiTextField textboxKeycode;
	private String currentString;
	private boolean flag = false;
	private GuiButton saveAndContinueButton;

	public GuiKeypadSetup(InventoryPlayer par1InventoryPlayer, TileEntityKeypad par2TileEntityFurnace)
    {
        super(new ContainerKeypad(par1InventoryPlayer, par2TileEntityFurnace));
        this.keypadInventory = par2TileEntityFurnace;
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
		this.textboxKeycode.setMaxStringLength(9);
		
		
		
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
		//this.updateButtonText();
		this.drawString(this.fontRendererObj, "CODE:", this.width / 2 - 67, this.height / 2 - 47 + 2, 4210752);		
		
    }
    
    
    protected void keyTyped(char par1, int par2){
		if(this.textboxKeycode.textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("MC|ItemName", this.textboxKeycode.getText().getBytes()));
		}
		
		else{
			super.keyTyped(par1, par2);
		}
	
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
        this.fontRendererObj.drawString("Keypad setup", this.xSize / 2 - this.fontRendererObj.getStringWidth("Keypad setup") / 2, 6, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        int i1;
        
    }
    
    private void updateButtonText(){

       this.saveAndContinueButton.displayString = !this.flag ? "Save & continue." : "Invalid code!";

   }
    
	

	
    
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
		case 0:
			try{		
				BlockKeypad.openCodeServer = Integer.parseInt(this.textboxKeycode.getText());
			
				mod_SecurityCraft.network.sendToServer(new PacketSetKeypadCode(BlockKeypad.lastKeypadX, BlockKeypad.lastKeypadY, BlockKeypad.lastKeypadZ, Integer.parseInt(this.textboxKeycode.getText())));
			}catch(Exception e){
				this.flag  = true;
				this.updateButtonText();
				e.printStackTrace();
				return;
			}
		
		      
			 BlockKeypad.playerObj.closeScreen();
			 BlockKeypad.playerObj.openGui(mod_SecurityCraft.instance, 0, BlockKeypad.worldObj, BlockKeypad.lastKeypadX, BlockKeypad.lastKeypadY, BlockKeypad.lastKeypadZ);
		    
			}
		}	
	
	
	
	
	
	

}
